package com.zalandunk.dawnsdew.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zalandunk.dawnsdew.data.Ingredient
import com.zalandunk.dawnsdew.data.IngredientProfile
import com.zalandunk.dawnsdew.data.IngredientProfiles
import com.zalandunk.dawnsdew.data.PantryEntry
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeCalculator

private val pantryCategoryOrder = listOf("spirit", "liqueur", "wine", "soda", "juice", "dairy", "tea", "fruit", "other")

@Composable
internal fun PantryScreen(controller: AppController, motion: MotionPolicy, onMessage: (String) -> Unit) {
    val language = controller.language
    val categories = remember(controller.catalog.ingredients) {
        pantryCategoryOrder.filter { category -> controller.catalog.ingredients.any { pantryCategory(it) == category } }
    }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "spirit") }
    var query by remember { mutableStateOf("") }
    var ownedOnly by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }
    val normalized = RecipeCalculator.normalizeSearch(query)
    val ingredients = remember(selectedCategory, normalized, ownedOnly, controller.state.pantry, controller.catalog.ingredients) {
        controller.catalog.ingredients.filter { ingredient ->
            pantryCategory(ingredient) == selectedCategory &&
                (!ownedOnly || RecipeCalculator.isOwned(controller.state.pantry[ingredient.id])) &&
                (normalized.isBlank() || RecipeCalculator.normalizeSearch("${ingredient.name.zh} ${ingredient.name.en}").contains(normalized))
        }
    }
    val ownedCount = controller.catalog.ingredients.count { RecipeCalculator.isOwned(controller.state.pantry[it.id]) }
    val makeableCount = controller.allRecipes.count { RecipeCalculator.match(it, controller.state.pantry).status == "makeable" }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (language == "zh") "$ownedCount 种已有 · $makeableCount 杯可调" else "$ownedCount owned · $makeableCount makeable",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (query.isNotBlank()) {
                        Text(
                            if (language == "zh") "正在搜索：$query" else "Searching: $query",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                PantryToolButton(
                    description = if (language == "zh") "搜索材料" else "Search ingredients",
                    active = query.isNotBlank(),
                    onClick = { showSearch = true }
                ) { Icon(Icons.Rounded.Search, null, modifier = Modifier.size(19.dp)) }
                PantryToolButton(
                    description = if (language == "zh") "只看已有" else "Owned only",
                    active = ownedOnly,
                    onClick = { ownedOnly = !ownedOnly }
                ) { Icon(Icons.Rounded.Inventory2, null, modifier = Modifier.size(19.dp)) }
                PantryToolButton(
                    description = if (language == "zh") "加入常用材料" else "Add common ingredients",
                    onClick = {
                        controller.selectCommonIngredients()
                        onMessage(if (language == "zh") "常用材料已加入酒柜" else "Common ingredients selected")
                    }
                ) { Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(19.dp)) }
            }
        }
        item {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                categories.forEach { category ->
                    PantryCategoryChip(
                        label = categoryLabel(category, language),
                        count = controller.catalog.ingredients.count { pantryCategory(it) == category },
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = category
                            query = ""
                        }
                    )
                }
            }
        }
        if (ingredients.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    Text(
                        if (language == "zh") "这个分类里没有符合条件的材料" else "No matching ingredients in this category",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(ingredients, key = { it.id }) { ingredient ->
                PantryIngredientRow(
                    ingredient = ingredient,
                    profile = IngredientProfiles.forIngredient(ingredient),
                    entry = controller.state.pantry[ingredient.id] ?: PantryEntry(),
                    recipeCount = controller.allRecipes.count { recipe -> ingredient.id in recipe.base || recipe.ingredients.any { it.id == ingredient.id } },
                    language = language,
                    onOpen = { selectedIngredient = ingredient },
                    onOwnedChange = { owned ->
                        val current = controller.state.pantry[ingredient.id] ?: PantryEntry()
                        controller.updatePantry(ingredient.id, current.copy(owned = owned))
                    }
                )
            }
        }
        item { Spacer(Modifier.height(18.dp)) }
    }

    if (showSearch) {
        FloatingIngredientSearch(
            query = query,
            language = language,
            onQueryChange = { query = it },
            onDismiss = { showSearch = false }
        )
    }
    selectedIngredient?.let { ingredient ->
        IngredientDetailDialog(
            ingredient = ingredient,
            profile = IngredientProfiles.forIngredient(ingredient),
            entry = controller.state.pantry[ingredient.id] ?: PantryEntry(),
            recipes = controller.allRecipes.filter { recipe -> ingredient.id in recipe.base || recipe.ingredients.any { it.id == ingredient.id } }
                .sortedByDescending { it.origin == "classic" },
            language = language,
            motion = motion,
            onDismiss = { selectedIngredient = null },
            onEntryChange = { controller.updatePantry(ingredient.id, it) },
            onOpenRecipe = { recipe ->
                selectedIngredient = null
                controller.openRecipe(recipe)
            }
        )
    }
}

@Composable
private fun PantryToolButton(
    description: String,
    active: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.padding(start = 5.dp).size(38.dp).semantics { contentDescription = description }.clickable(onClick = onClick),
        shape = CircleShape,
        color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Composable
private fun PantryCategoryChip(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(36.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(count.toString(), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun PantryIngredientRow(
    ingredient: Ingredient,
    profile: IngredientProfile,
    entry: PantryEntry,
    recipeCount: Int,
    language: String,
    onOpen: () -> Unit,
    onOwnedChange: (Boolean) -> Unit
) {
    Card(
        onClick = onOpen,
        colors = CardDefaults.cardColors(
            containerColor = if (entry.owned) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f) else MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 78.dp).padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IngredientBottleArt(profile, Modifier.size(width = 48.dp, height = 64.dp))
            Column(Modifier.weight(1f).padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    ingredient.name.value(language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    ingredient.name.secondary(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    if (language == "zh") "$recipeCount 杯相关配方 · 查看酒款故事" else "$recipeCount recipes · Story and history",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                ingredient.abv.takeIf { it > 0.0 }?.let {
                    Text("${formatNumber(it)}%", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Switch(checked = entry.owned, onCheckedChange = onOwnedChange)
            }
        }
    }
}

@Composable
private fun FloatingIngredientSearch(
    query: String,
    language: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            Modifier.fillMaxSize().imePadding().padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().widthIn(max = 620.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester).padding(8.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    trailingIcon = {
                        IconButton(onClick = { if (query.isBlank()) onDismiss() else onQueryChange("") }) {
                            Icon(Icons.Rounded.Close, if (language == "zh") "清除" else "Clear")
                        }
                    },
                    placeholder = { Text(if (language == "zh") "搜索当前品类的酒或材料" else "Search this category") }
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientDetailDialog(
    ingredient: Ingredient,
    profile: IngredientProfile,
    entry: PantryEntry,
    recipes: List<Recipe>,
    language: String,
    motion: MotionPolicy,
    onDismiss: () -> Unit,
    onEntryChange: (PantryEntry) -> Unit,
    onOpenRecipe: (Recipe) -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact = maxWidth < 600.dp
            Surface(
                modifier = if (compact) Modifier.fillMaxSize() else Modifier.align(Alignment.Center).fillMaxWidth(0.94f).heightIn(max = 860.dp).widthIn(max = 720.dp),
                shape = if (compact) RoundedCornerShape(0.dp) else RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                LazyColumn {
                    item {
                        Box(
                            Modifier.fillMaxWidth().height(208.dp).background(
                                Brush.linearGradient(
                                    listOf(Color(profile.accent).copy(alpha = 0.62f), MaterialTheme.colorScheme.surfaceVariant)
                                )
                            )
                        ) {
                            IngredientBottleArt(profile, Modifier.align(Alignment.CenterEnd).padding(end = 32.dp).size(width = 104.dp, height = 156.dp))
                            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                                Icon(Icons.Rounded.Close, if (language == "zh") "关闭" else "Close")
                            }
                            Column(Modifier.align(Alignment.BottomStart).fillMaxWidth(0.72f).padding(20.dp)) {
                                Text(categoryLabel(pantryCategory(ingredient), language), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text(ingredient.name.value(language), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                                Text(ingredient.name.secondary(language), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    item {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            Text(profile.introduction.value(language), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Inventory2, null, tint = MaterialTheme.colorScheme.primary)
                                        Text(if (language == "zh") "我的库存" else "My stock", modifier = Modifier.weight(1f).padding(start = 8.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Switch(checked = entry.owned, onCheckedChange = { onEntryChange(entry.copy(owned = it)) })
                                    }
                                    if (entry.owned) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            PantryField(if (language == "zh") "库存 ml/份" else "Stock", entry.stock, Modifier.weight(1f)) { onEntryChange(entry.copy(stock = it)) }
                                            PantryField(if (language == "zh") "购入价" else "Price", entry.price, Modifier.weight(1f)) { onEntryChange(entry.copy(price = it)) }
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            PantryField(if (language == "zh") "包装容量" else "Pack", entry.packSize, Modifier.weight(1f), ingredient.pack.takeIf { it > 0 }?.let(::formatNumber).orEmpty()) { onEntryChange(entry.copy(packSize = it)) }
                                            PantryField(if (language == "zh") "酒精度 %" else "ABV %", entry.abv, Modifier.weight(1f), ingredient.abv.takeIf { it > 0 }?.let(::formatNumber).orEmpty()) { onEntryChange(entry.copy(abv = it)) }
                                        }
                                    }
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.HistoryEdu, null, tint = MaterialTheme.colorScheme.secondary)
                                    Text(if (language == "zh") "起源与发展" else "Origin and development", modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                }
                                Text(profile.origin.value(language), color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight)
                                profile.milestones.forEach { milestone ->
                                    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp)) {
                                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer) {
                                                Text(milestone.year, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            }
                                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                Text(milestone.title.value(language), fontWeight = FontWeight.Bold)
                                                Text(milestone.detail.value(language), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                                Text(profile.sourceNote.value(language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (language == "zh") "常见配方" else "Notable recipes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                if (recipes.isEmpty()) {
                                    Text(if (language == "zh") "当前酒笺还没有收录相关配方。" else "No related recipe is recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    recipes.take(5).forEach { recipe ->
                                        ListItem(
                                            modifier = Modifier.fillMaxWidth().clickable { onOpenRecipe(recipe) },
                                            headlineContent = { Text(recipe.name.value(language), fontWeight = FontWeight.Bold) },
                                            supportingContent = { Text("${originLabel(recipe.origin, language)} · ${recipe.name.secondary(language)}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                            trailingContent = { Icon(Icons.AutoMirrored.Rounded.ArrowForward, null) }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(14.dp))
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

private fun pantryCategory(ingredient: Ingredient): String = when (ingredient.id) {
    "prosecco" -> "wine"
    else -> ingredient.category
}
