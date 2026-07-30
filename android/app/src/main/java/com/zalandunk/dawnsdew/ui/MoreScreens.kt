package com.zalandunk.dawnsdew.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zalandunk.dawnsdew.data.LocalizedList
import com.zalandunk.dawnsdew.data.LocalizedText
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeIngredient
import java.util.UUID
import kotlin.math.roundToInt

@Composable
internal fun MoreScreen(controller: AppController, onMessage: (String) -> Unit) {
    val language = controller.language
    val recent = controller.state.recentIds.mapNotNull { id -> controller.allRecipes.find { it.id == id } }
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                Text(if (language == "zh") "更多" else "More", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text(if (language == "zh") "把酒柜、灵感和本地数据都收在这里。" else "Pantry, ideas and local data in one place.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            MoreAction(Icons.Rounded.Favorite, if (language == "zh") "我的收藏" else "Favorites", "${controller.state.favoriteIds.size}", MaterialTheme.colorScheme.secondary) {
                controller.navigate(Destination.Favorites)
            }
        }
        item {
            MoreAction(Icons.Rounded.EditNote, if (language == "zh") "自定义配方" else "Custom recipes", "${controller.state.customRecipes.size}", MaterialTheme.colorScheme.primary) {
                controller.navigate(Destination.Custom)
            }
        }
        item {
            MoreAction(Icons.Rounded.History, if (language == "zh") "最近查看" else "Recently viewed", "${recent.size}", MaterialTheme.colorScheme.tertiary) {
                controller.navigate(Destination.Recent)
            }
        }
        item {
            MoreAction(Icons.Rounded.Settings, if (language == "zh") "数据与估算设置" else "Data and estimates", if (language == "zh") "导入 · 导出" else "Import · Export", MaterialTheme.colorScheme.onSurface) {
                controller.navigate(Destination.Data)
            }
        }
        item {
            SectionTitle(
                if (language == "zh") "聚会模式" else "Gathering mode",
                if (language == "zh") "优先选择酒柜里能完成的配方，并避开最近抽中过的酒。" else "Prefer makeable recipes and avoid recent repeats."
            )
        }
        item {
            Button(
                onClick = {
                    controller.randomRecipe(true)
                    onMessage(if (language == "zh") "聚会推荐已生成" else "Gathering pick ready")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Groups, null)
                Spacer(Modifier.width(8.dp))
                Text(if (language == "zh") "来一杯不重样" else "Pick without repeats")
            }
        }
        if (recent.isNotEmpty()) {
            item { SectionTitle(if (language == "zh") "刚刚翻过" else "Just viewed") }
            items(recent.take(4), key = { "recent-${it.id}" }) { recipe ->
                Surface(
                    onClick = { controller.openRecipe(recipe) },
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                            Icon(Icons.Rounded.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(9.dp).size(20.dp))
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
                            Text(recipe.name.value(language), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(recipe.name.secondary(language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun MoreAction(icon: ImageVector, title: String, value: String, accent: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = accent.copy(alpha = 0.14f)) {
                Icon(icon, null, tint = accent, modifier = Modifier.padding(10.dp).size(22.dp))
            }
            Text(title, modifier = Modifier.weight(1f).padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
            Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
internal fun CustomRecipeScreen(controller: AppController, motion: MotionPolicy, onMessage: (String) -> Unit) {
    val language = controller.language
    var showEditor by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Recipe?>(null) }
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                Text(if (language == "zh") "自定义配方" else "Custom recipes", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text(if (language == "zh") "把自己的味道写进酒笺，也加入搜索、收藏和今夜酒单。" else "Your own drinks join search, favorites and tonight's menu.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = { showEditor = true }) {
                    Icon(Icons.Rounded.Add, null)
                    Text(if (language == "zh") "新建" else "Create")
                }
            }
        }
        if (controller.state.customRecipes.isEmpty()) {
            item {
                Surface(color = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        if (language == "zh") "还没有自定义配方。第一杯，可以从今晚的心情开始。" else "No custom recipes yet. Begin with tonight's mood.",
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(controller.state.customRecipes, key = { it.id }) { recipe ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    RecipeCard(
                        recipe = recipe,
                        ingredientMap = controller.ingredientMap,
                        state = controller.state,
                        language = language,
                        motion = motion,
                        onOpen = controller::openRecipe,
                        onToggleFavorite = controller::toggleFavorite,
                        onToggleTonight = controller::toggleTonight
                    )
                    TextButton(onClick = { deleteTarget = recipe }, modifier = Modifier.align(Alignment.End)) {
                        Icon(Icons.Rounded.DeleteOutline, null)
                        Text(if (language == "zh") "删除配方" else "Delete")
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
    if (showEditor) {
        CustomRecipeEditor(
            controller = controller,
            onDismiss = { showEditor = false },
            onSaved = {
                controller.addCustomRecipe(it)
                showEditor = false
                onMessage(if (language == "zh") "自定义配方已保存" else "Custom recipe saved")
            }
        )
    }
    deleteTarget?.let { recipe ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(if (language == "zh") "删除这杯配方？" else "Delete this recipe?") },
            text = { Text(recipe.name.value(language)) },
            confirmButton = {
                TextButton(onClick = {
                    controller.deleteCustomRecipe(recipe.id)
                    deleteTarget = null
                    onMessage(if (language == "zh") "自定义配方已删除" else "Custom recipe deleted")
                }) { Text(if (language == "zh") "删除" else "Delete") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text(if (language == "zh") "取消" else "Cancel") } }
        )
    }
}

private data class DraftIngredient(val id: String = "", val unit: String = "ml", val amount: String = "30")

@Composable
private fun CustomRecipeEditor(controller: AppController, onDismiss: () -> Unit, onSaved: (Recipe) -> Unit) {
    val language = controller.language
    var nameZh by remember { mutableStateOf("") }
    var nameEn by remember { mutableStateOf("") }
    var base by remember { mutableStateOf(controller.catalog.ingredients.firstOrNull { it.category == "spirit" }?.id.orEmpty()) }
    var taste by remember { mutableStateOf("fresh") }
    var difficulty by remember { mutableStateOf("easy") }
    var capacity by remember { mutableStateOf(formatNumber(controller.state.settings.glassCapacity)) }
    var stepsZh by remember { mutableStateOf("") }
    var stepsEn by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf(listOf(DraftIngredient(id = base))) }
    var error by remember { mutableStateOf("") }
    val ingredientOptions = controller.catalog.ingredients.map { it.id to it.name.value(language) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.96f).fillMaxHeight(0.94f),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(if (language == "zh") "写一杯自己的酒" else "Write your own drink", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(if (language == "zh") "每行一步，让这杯酒以后还能被准确重现。" else "Use one line per step so the drink can be recreated.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Rounded.Close, if (language == "zh") "关闭" else "Close") }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(nameZh, { nameZh = it }, Modifier.fillMaxWidth(), label = { Text("中文名称") }, singleLine = true)
                    OutlinedTextField(nameEn, { nameEn = it }, Modifier.fillMaxWidth(), label = { Text("English name") }, singleLine = true)
                    OptionChooser(if (language == "zh") "基酒" else "Base", base, ingredientOptions) {
                        base = it
                        if (rows.size == 1 && rows.first().id.isBlank()) rows = listOf(rows.first().copy(id = it))
                    }
                    OptionChooser(
                        if (language == "zh") "口味" else "Taste",
                        taste,
                        listOf("sweet", "creamy", "fresh", "citrus", "fruity", "sparkling", "strong", "bitter", "herbal", "dry", "spicy", "coffee").map { it to tasteLabel(it, language) }
                    ) { taste = it }
                    OptionChooser(
                        if (language == "zh") "难度" else "Difficulty",
                        difficulty,
                        listOf("easy", "medium", "hard").map { it to difficultyLabel(it, language) }
                    ) { difficulty = it }
                    OutlinedTextField(
                        capacity,
                        { capacity = it.filter { char -> char.isDigit() || char == '.' } },
                        Modifier.fillMaxWidth(),
                        label = { Text(if (language == "zh") "杯容量 ml" else "Glass capacity ml") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    SectionTitle(if (language == "zh") "材料" else "Ingredients")
                    rows.forEachIndexed { index, row ->
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(8.dp)) {
                            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OptionChooser(if (language == "zh") "材料" else "Ingredient", row.id, ingredientOptions) { selected ->
                                    rows = rows.toMutableList().also { it[index] = row.copy(id = selected) }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    OptionChooser(
                                        if (language == "zh") "用量" else "Unit",
                                        row.unit,
                                        listOf(
                                            "ml" to "ml",
                                            "piece" to if (language == "zh") "份/个" else "piece",
                                            "fill" to if (language == "zh") "加至杯量" else "fill ratio",
                                            "top" to if (language == "zh") "补满" else "top up"
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) { selected -> rows = rows.toMutableList().also { it[index] = row.copy(unit = selected) } }
                                    if (row.unit != "top") {
                                        OutlinedTextField(
                                            row.amount,
                                            { input -> rows = rows.toMutableList().also { it[index] = row.copy(amount = input.filter { char -> char.isDigit() || char == '.' }) } },
                                            Modifier.weight(0.7f),
                                            label = { Text(if (row.unit == "fill") "0.1–1" else if (language == "zh") "数量" else "Amount") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true
                                        )
                                    }
                                    IconButton(onClick = { if (rows.size > 1) rows = rows.filterIndexed { i, _ -> i != index } }) {
                                        Icon(Icons.Rounded.DeleteOutline, if (language == "zh") "移除材料" else "Remove ingredient")
                                    }
                                }
                            }
                        }
                    }
                    OutlinedButton(onClick = { rows = rows + DraftIngredient() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Rounded.Add, null)
                        Text(if (language == "zh") "添加材料" else "Add ingredient")
                    }
                    OutlinedTextField(
                        stepsZh,
                        { stepsZh = it },
                        Modifier.fillMaxWidth(),
                        label = { Text("中文制作步骤（每行一步）") },
                        minLines = 4
                    )
                    OutlinedTextField(
                        stepsEn,
                        { stepsEn = it },
                        Modifier.fillMaxWidth(),
                        label = { Text("English steps (one per line)") },
                        minLines = 4
                    )
                    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
                    Button(
                        onClick = {
                            val ingredients = rows.mapNotNull { row ->
                                if (row.id.isBlank()) return@mapNotNull null
                                when (row.unit) {
                                    "top" -> RecipeIngredient(row.id, null, "top", null, true, false)
                                    "fill" -> RecipeIngredient(row.id, null, "fill", (row.amount.toDoubleOrNull() ?: 0.8).coerceIn(0.1, 1.0), false, false)
                                    else -> RecipeIngredient(row.id, (row.amount.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0), row.unit, null, false, false)
                                }
                            }
                            val zhLines = stepsZh.lines().map(String::trim).filter(String::isNotBlank)
                            val enLines = stepsEn.lines().map(String::trim).filter(String::isNotBlank)
                            if (nameZh.isBlank() || nameEn.isBlank() || ingredients.isEmpty() || zhLines.isEmpty() || enLines.isEmpty()) {
                                error = if (language == "zh") "请填写中英文名称、步骤，并至少添加一种材料。" else "Add both names, steps and at least one ingredient."
                            } else {
                                onSaved(
                                    Recipe(
                                        id = "custom-${UUID.randomUUID()}",
                                        rating = "",
                                        name = LocalizedText(nameZh.trim(), nameEn.trim()),
                                        origin = "custom",
                                        base = listOf(base).filter(String::isNotBlank),
                                        taste = listOf(taste),
                                        difficulty = difficulty,
                                        colors = listOf("#7D3149", "#F18B69"),
                                        glassCapacity = (capacity.toDoubleOrNull() ?: controller.state.settings.glassCapacity).coerceAtLeast(30.0),
                                        ice = "",
                                        method = "build",
                                        ingredients = ingredients,
                                        steps = LocalizedList(zhLines, enLines),
                                        description = LocalizedText("写在今夜酒单里的个人创作。", "A personal creation written for tonight."),
                                        sourceText = "",
                                        aliases = emptyList(),
                                        createdAt = System.currentTimeMillis().toString()
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.Save, null)
                        Text(if (language == "zh") "保存自定义配方" else "Save custom recipe")
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
internal fun DataScreen(
    controller: AppController,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onMessage: (String) -> Unit
) {
    val language = controller.language
    var glassCapacity by remember(controller.state.settings) { mutableStateOf(formatNumber(controller.state.settings.glassCapacity)) }
    var icedCapacity by remember(controller.state.settings) { mutableStateOf(formatNumber(controller.state.settings.icedLiquidCapacity)) }
    var currency by remember(controller.state.settings) { mutableStateOf(controller.state.settings.currency) }
    var fontScale by remember(controller.state.settings.fontScale) { mutableFloatStateOf(controller.state.settings.fontScale) }
    var confirmReset by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(if (language == "zh") "数据与估算" else "Data and estimates", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        Text(
            if (language == "zh") "所有数据保存在本机。通过 JSON 备份，可在 Web v0.2 与原生版之间手动迁移。" else "Data stays on this device. JSON backups support manual migration from Web v0.2.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SectionTitle(if (language == "zh") "外观" else "Appearance")
        Text(if (language == "zh") "主题" else "Theme", fontWeight = FontWeight.Bold)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "light" to if (language == "zh") "朝露白" else "Dawn light",
                "dark" to if (language == "zh") "夜酿墨" else "Night ink",
                "system" to if (language == "zh") "跟随系统" else "System"
            ).forEach { (mode, label) ->
                FilterChip(
                    selected = controller.state.settings.themeMode == mode,
                    onClick = { controller.saveSettings(controller.state.settings.copy(themeMode = mode)) },
                    label = { Text(label) }
                )
            }
        }
        Text(if (language == "zh") "强调色" else "Accent", fontWeight = FontWeight.Bold)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("gold", if (language == "zh") "黎明金" else "Dawn gold", Color(0xFFF0C978)),
                Triple("coral", if (language == "zh") "珊瑚红" else "Coral", Color(0xFFF18B69)),
                Triple("sage", if (language == "zh") "月光青绿" else "Moon sage", Color(0xFFA5D0C0))
            ).forEach { (accent, label, color) ->
                FilterChip(
                    selected = controller.state.settings.accent == accent,
                    onClick = { controller.saveSettings(controller.state.settings.copy(accent = accent)) },
                    leadingIcon = { Surface(color = color, shape = CircleShape, modifier = Modifier.size(14.dp)) {} },
                    label = { Text(label) }
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (language == "zh") "字体大小" else "Text size", fontWeight = FontWeight.Bold)
            Text("${(fontScale * 100).roundToInt()}%", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(
            value = fontScale,
            onValueChange = { fontScale = it },
            onValueChangeFinished = {
                val snapped = (fontScale * 10).roundToInt() / 10f
                fontScale = snapped
                controller.saveSettings(controller.state.settings.copy(fontScale = snapped))
            },
            valueRange = 0.9f..1.3f,
            steps = 3
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Surface(color = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(8.dp)) {
            Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(if (language == "zh") "本地状态" else "Local state", fontWeight = FontWeight.Bold)
                Text(
                    if (language == "zh") "酒柜 ${controller.state.pantry.count { it.value.owned }} · 收藏 ${controller.state.favoriteIds.size} · 自定义 ${controller.state.customRecipes.size}" else "Pantry ${controller.state.pantry.count { it.value.owned }} · Favorites ${controller.state.favoriteIds.size} · Custom ${controller.state.customRecipes.size}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    if (controller.state.savedAt.isBlank()) if (language == "zh") "尚未保存" else "Not saved yet" else "${if (language == "zh") "上次保存" else "Last saved"}：${controller.state.savedAt}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onExport, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.Download, null)
                Text(if (language == "zh") "导出 JSON" else "Export JSON")
            }
            FilledTonalButton(onClick = onImport, modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.Upload, null)
                Text(if (language == "zh") "导入 JSON" else "Import JSON")
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        SectionTitle(if (language == "zh") "估算设置" else "Estimate settings")
        NumberSetting(if (language == "zh") "默认杯容量 ml" else "Default glass ml", glassCapacity) { glassCapacity = it }
        NumberSetting(if (language == "zh") "满冰可用容量 ml" else "Iced liquid ml", icedCapacity) { icedCapacity = it }
        OutlinedTextField(currency, { currency = it.take(4) }, Modifier.fillMaxWidth(), label = { Text(if (language == "zh") "货币符号" else "Currency") }, singleLine = true)
        Button(
            onClick = {
                controller.saveSettings(
                    controller.state.settings.copy(
                        glassCapacity = (glassCapacity.toDoubleOrNull() ?: 300.0).coerceAtLeast(30.0),
                        icedLiquidCapacity = (icedCapacity.toDoubleOrNull() ?: 150.0).coerceAtLeast(30.0),
                        currency = currency.ifBlank { "¥" }.take(4)
                    )
                )
                onMessage(if (language == "zh") "估算设置已保存" else "Estimate settings saved")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Rounded.Save, null)
            Text(if (language == "zh") "保存设置" else "Save settings")
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        SectionTitle(if (language == "zh") "重置本地数据" else "Reset local data")
        Text(if (language == "zh") "清空酒柜、收藏、今夜酒单、自定义配方和设置，不影响内置配方。" else "Clear pantry, favorites, tonight, custom recipes and settings.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedButton(onClick = { confirmReset = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.RestartAlt, null)
            Text(if (language == "zh") "清空全部本地数据" else "Clear all local data")
        }
        Spacer(Modifier.height(18.dp))
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text(if (language == "zh") "确定清空本地数据？" else "Clear local data?") },
            text = { Text(if (language == "zh") "当前状态会先保留一份内部备份，但建议先导出 JSON。" else "An internal backup is retained, but export JSON first if needed.") },
            confirmButton = {
                TextButton(onClick = {
                    controller.reset()
                    confirmReset = false
                    onMessage(if (language == "zh") "本地数据已重置" else "Local data reset")
                }) { Text(if (language == "zh") "清空" else "Clear") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text(if (language == "zh") "取消" else "Cancel") } }
        )
    }
}

@Composable
private fun NumberSetting(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value,
        { input -> onValueChange(input.filter { it.isDigit() || it == '.' }.take(10)) },
        Modifier.fillMaxWidth(),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
}
