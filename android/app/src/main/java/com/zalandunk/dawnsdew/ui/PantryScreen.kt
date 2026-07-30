package com.zalandunk.dawnsdew.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zalandunk.dawnsdew.data.Ingredient
import com.zalandunk.dawnsdew.data.PantryEntry
import com.zalandunk.dawnsdew.data.RecipeCalculator
import com.zalandunk.dawnsdew.ui.theme.DawnPalette

@Composable
internal fun PantryScreen(controller: AppController, onMessage: (String) -> Unit) {
    val language = controller.language
    var query by remember { mutableStateOf("") }
    var ownedOnly by remember { mutableStateOf(false) }
    val normalized = RecipeCalculator.normalizeSearch(query)
    val ingredients = remember(controller.catalog.ingredients, normalized, ownedOnly, controller.state.pantry) {
        controller.catalog.ingredients.filter { ingredient ->
            (!ownedOnly || RecipeCalculator.isOwned(controller.state.pantry[ingredient.id])) &&
                (normalized.isBlank() || RecipeCalculator.normalizeSearch("${ingredient.name.zh} ${ingredient.name.en} ${ingredient.category}").contains(normalized))
        }
    }
    val groups = ingredients.groupBy { it.category }
    val ownedCount = controller.catalog.ingredients.count { RecipeCalculator.isOwned(controller.state.pantry[it.id]) }
    val makeableCount = controller.allRecipes.count { RecipeCalculator.match(it, controller.state.pantry).status == "makeable" }
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(if (language == "zh") "我的酒柜" else "My pantry", style = MaterialTheme.typography.headlineSmall, color = DawnPalette.Paper, fontWeight = FontWeight.Bold)
                Text(
                    if (language == "zh") "记下手边的酒与材料，配方会自动告诉你此刻能调什么。" else "Track what is on hand and discover what you can make now.",
                    color = DawnPalette.Muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PantrySummary(Modifier.weight(1f), ownedCount.toString(), if (language == "zh") "已有材料" else "Owned", DawnPalette.Gold)
                PantrySummary(Modifier.weight(1f), makeableCount.toString(), if (language == "zh") "此刻可调" else "Makeable", DawnPalette.Sage)
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                label = { Text(if (language == "zh") "搜索材料" else "Search ingredients") }
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = ownedOnly,
                    onClick = { ownedOnly = !ownedOnly },
                    label = { Text(if (language == "zh") "只看已有" else "Owned only") },
                    leadingIcon = { Icon(Icons.Rounded.Inventory2, null) }
                )
                Button(onClick = {
                    controller.selectCommonIngredients()
                    onMessage(if (language == "zh") "常用材料已加入酒柜" else "Common ingredients selected")
                }) {
                    Icon(Icons.Rounded.CheckCircle, null)
                    Text(if (language == "zh") "选择常用" else "Select common")
                }
            }
        }
        groups.forEach { (category, values) ->
            item(key = "header-$category") {
                Text(
                    categoryLabel(category, language),
                    style = MaterialTheme.typography.titleMedium,
                    color = DawnPalette.Gold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
            }
            items(values, key = { it.id }) { ingredient ->
                PantryIngredientCard(
                    ingredient = ingredient,
                    entry = controller.state.pantry[ingredient.id] ?: PantryEntry(),
                    language = language,
                    onChange = { controller.updatePantry(ingredient.id, it) }
                )
            }
        }
        item { Text("", modifier = Modifier.padding(bottom = 18.dp)) }
    }
}

@Composable
private fun PantrySummary(modifier: Modifier, value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(modifier, color = DawnPalette.Surface, contentColor = DawnPalette.Paper, shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.Black)
            Text(label, style = MaterialTheme.typography.labelMedium, color = DawnPalette.Muted)
        }
    }
}

@Composable
private fun PantryIngredientCard(
    ingredient: Ingredient,
    entry: PantryEntry,
    language: String,
    onChange: (PantryEntry) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DawnPalette.Surface, contentColor = DawnPalette.Paper),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = entry.owned, onCheckedChange = { onChange(entry.copy(owned = it)) })
                Column(Modifier.weight(1f)) {
                    Text(ingredient.name.value(language), fontWeight = FontWeight.Bold, color = DawnPalette.Paper)
                    Text(ingredient.name.secondary(language), style = MaterialTheme.typography.labelSmall, color = DawnPalette.Muted)
                }
                ingredient.abv.takeIf { it > 0.0 }?.let {
                    Text("${formatNumber(it)}%", color = DawnPalette.Gold, style = MaterialTheme.typography.labelMedium)
                }
            }
            if (entry.owned) {
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val compact = maxWidth < 360.dp
                    if (compact) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PantryField(if (language == "zh") "库存 ml/份" else "Stock", entry.stock, Modifier.fillMaxWidth()) { onChange(entry.copy(stock = it)) }
                            PantryField(if (language == "zh") "购入价" else "Price", entry.price, Modifier.fillMaxWidth()) { onChange(entry.copy(price = it)) }
                            PantryField(if (language == "zh") "包装容量" else "Pack size", entry.packSize, Modifier.fillMaxWidth(), ingredient.pack.takeIf { it > 0 }?.let(::formatNumber).orEmpty()) { onChange(entry.copy(packSize = it)) }
                            PantryField(if (language == "zh") "酒精度 %" else "ABV %", entry.abv, Modifier.fillMaxWidth(), ingredient.abv.takeIf { it > 0 }?.let(::formatNumber).orEmpty()) { onChange(entry.copy(abv = it)) }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PantryField(if (language == "zh") "库存 ml/份" else "Stock", entry.stock, Modifier.weight(1f)) { onChange(entry.copy(stock = it)) }
                                PantryField(if (language == "zh") "购入价" else "Price", entry.price, Modifier.weight(1f)) { onChange(entry.copy(price = it)) }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PantryField(if (language == "zh") "包装容量" else "Pack size", entry.packSize, Modifier.weight(1f), ingredient.pack.takeIf { it > 0 }?.let(::formatNumber).orEmpty()) { onChange(entry.copy(packSize = it)) }
                                PantryField(if (language == "zh") "酒精度 %" else "ABV %", entry.abv, Modifier.weight(1f), ingredient.abv.takeIf { it > 0 }?.let(::formatNumber).orEmpty()) { onChange(entry.copy(abv = it)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PantryField(
    label: String,
    value: String,
    modifier: Modifier,
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() || it == '.' }.take(10)) },
        modifier = modifier,
        singleLine = true,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotBlank()) Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}
