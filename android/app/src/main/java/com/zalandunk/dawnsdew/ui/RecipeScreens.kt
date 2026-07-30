package com.zalandunk.dawnsdew.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeCalculator
import com.zalandunk.dawnsdew.ui.theme.DawnPalette
import kotlinx.coroutines.delay

data class RecipeFilters(
    val origin: String = "",
    val base: String = "",
    val taste: String = "",
    val difficulty: String = "",
    val makeableOnly: Boolean = false,
    val favoriteOnly: Boolean = false
)

@Composable
internal fun RecipeCollectionScreen(
    controller: AppController,
    motion: MotionPolicy,
    mode: Destination,
    onMessage: (String) -> Unit
) {
    val language = controller.language
    val source = when (mode) {
        Destination.Tonight -> controller.state.tonightIds.mapNotNull { id -> controller.allRecipes.find { it.id == id } }
        Destination.Favorites -> controller.allRecipes.filter { it.id in controller.state.favoriteIds }
        Destination.Recent -> controller.state.recentIds.mapNotNull { id -> controller.allRecipes.find { it.id == id } }
        else -> controller.allRecipes
    }
    var query by remember(mode) { mutableStateOf("") }
    var filters by remember(mode) { mutableStateOf(RecipeFilters()) }
    var showFilters by remember { mutableStateOf(false) }
    val normalized = RecipeCalculator.normalizeSearch(query)
    val filtered = remember(source, normalized, filters, controller.state.pantry, controller.state.favoriteIds) {
        source.filter { recipe ->
            val match = RecipeCalculator.match(recipe, controller.state.pantry)
            (normalized.isBlank() || RecipeCalculator.searchableText(recipe, controller.ingredientMap).contains(normalized)) &&
                (filters.origin.isBlank() || recipe.origin == filters.origin) &&
                (filters.base.isBlank() || filters.base in recipe.base) &&
                (filters.taste.isBlank() || filters.taste in recipe.taste) &&
                (filters.difficulty.isBlank() || recipe.difficulty == filters.difficulty) &&
                (!filters.makeableOnly || match.status == "makeable") &&
                (!filters.favoriteOnly || recipe.id in controller.state.favoriteIds)
        }
    }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(screenTitle(mode, language), style = MaterialTheme.typography.headlineSmall, color = DawnPalette.Paper, fontWeight = FontWeight.Bold)
                Text(screenSubtitle(mode, source.size, language), style = MaterialTheme.typography.bodySmall, color = DawnPalette.Muted)
            }
            if (mode == Destination.Tonight && source.isNotEmpty()) {
                TextButton(onClick = {
                    controller.clearTonight()
                    onMessage(if (language == "zh") "今夜酒单已清空" else "Tonight's menu cleared")
                }) {
                    Icon(Icons.Rounded.ClearAll, null)
                    Text(if (language == "zh") "清空" else "Clear")
                }
            }
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            label = { Text(if (language == "zh") "搜索名称、基酒或材料" else "Search names, bases or ingredients") }
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { showFilters = true },
                label = { Text(if (language == "zh") "筛选" else "Filters") },
                leadingIcon = { Icon(Icons.Rounded.FilterList, null) }
            )
            FilterChip(
                selected = filters.makeableOnly,
                onClick = { filters = filters.copy(makeableOnly = !filters.makeableOnly) },
                label = { Text(if (language == "zh") "此刻可调" else "Makeable") }
            )
            FilterChip(
                selected = filters.favoriteOnly,
                onClick = { filters = filters.copy(favoriteOnly = !filters.favoriteOnly) },
                label = { Text(if (language == "zh") "只看收藏" else "Favorites") }
            )
            if (filters != RecipeFilters()) {
                AssistChip(
                    onClick = { filters = RecipeFilters() },
                    label = { Text(if (language == "zh") "重置" else "Reset") },
                    leadingIcon = { Icon(Icons.Rounded.RestartAlt, null) }
                )
            }
        }
        Text(
            if (language == "zh") "找到 ${filtered.size} 杯" else "${filtered.size} recipes",
            style = MaterialTheme.typography.labelMedium,
            color = DawnPalette.Muted,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (mode == Destination.Tonight && source.isEmpty()) {
                        if (language == "zh") "今夜酒单还是空的\n在配方卡片上点亮酒杯，把它留给今晚。" else "Tonight's menu is empty.\nAdd a drink from any recipe card."
                    } else {
                        if (language == "zh") "没有符合条件的配方" else "No matching recipes"
                    },
                    color = DawnPalette.Muted
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(260.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(filtered, key = { _, recipe -> recipe.id }) { index, recipe ->
                    var visible by remember(recipe.id, mode) { mutableStateOf(!motion.enabled) }
                    LaunchedEffect(recipe.id, motion.enabled) {
                        if (motion.enabled) delay(index.coerceAtMost(7) * 46L)
                        visible = true
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(motionDuration(motion, 280))) +
                            slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(motionDuration(motion, 360)))
                    ) {
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
                    }
                }
            }
        }
    }
    if (showFilters) {
        RecipeFilterDialog(
            recipes = source,
            controller = controller,
            current = filters,
            onApply = { filters = it; showFilters = false },
            onDismiss = { showFilters = false }
        )
    }
}

@Composable
private fun RecipeFilterDialog(
    recipes: List<Recipe>,
    controller: AppController,
    current: RecipeFilters,
    onApply: (RecipeFilters) -> Unit,
    onDismiss: () -> Unit
) {
    val language = controller.language
    var draft by remember(current) { mutableStateOf(current) }
    val origins = recipes.map { it.origin }.distinct()
    val bases = recipes.flatMap { it.base }.distinct().sortedBy { controller.ingredientMap[it]?.name?.value(language) }
    val tastes = recipes.flatMap { it.taste }.distinct().sorted()
    val difficulties = recipes.map { it.difficulty }.distinct()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (language == "zh") "筛选配方" else "Filter recipes") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                OptionChooser(
                    if (language == "zh") "来源" else "Origin",
                    draft.origin,
                    listOf("" to if (language == "zh") "全部来源" else "All origins") + origins.map { it to originLabel(it, language) }
                ) { draft = draft.copy(origin = it) }
                OptionChooser(
                    if (language == "zh") "基酒" else "Base",
                    draft.base,
                    listOf("" to if (language == "zh") "全部基酒" else "All bases") + bases.map { id -> id to (controller.ingredientMap[id]?.name?.value(language) ?: id) }
                ) { draft = draft.copy(base = it) }
                OptionChooser(
                    if (language == "zh") "口味" else "Taste",
                    draft.taste,
                    listOf("" to if (language == "zh") "全部口味" else "All tastes") + tastes.map { it to tasteLabel(it, language) }
                ) { draft = draft.copy(taste = it) }
                OptionChooser(
                    if (language == "zh") "难度" else "Difficulty",
                    draft.difficulty,
                    listOf("" to if (language == "zh") "全部难度" else "All difficulties") + difficulties.map { it to difficultyLabel(it, language) }
                ) { draft = draft.copy(difficulty = it) }
            }
        },
        confirmButton = { TextButton(onClick = { onApply(draft) }) { Text(if (language == "zh") "应用" else "Apply") } },
        dismissButton = {
            TextButton(onClick = { draft = RecipeFilters() }) { Text(if (language == "zh") "清除" else "Clear") }
            TextButton(onClick = onDismiss) { Text(if (language == "zh") "取消" else "Cancel") }
        }
    )
}

@Composable
internal fun OptionChooser(
    label: String,
    value: String,
    options: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == value }?.second ?: value
    Box(modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$label：$selectedLabel", modifier = Modifier.weight(1f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.second) },
                    onClick = { onSelected(option.first); expanded = false }
                )
            }
        }
    }
}

private fun screenTitle(mode: Destination, language: String): String = when (mode) {
    Destination.Tonight -> if (language == "zh") "今夜酒单" else "Tonight's menu"
    Destination.Favorites -> if (language == "zh") "我的收藏" else "Favorites"
    Destination.Recent -> if (language == "zh") "最近查看" else "Recently viewed"
    else -> if (language == "zh") "配方酒笺" else "Recipe collection"
}

private fun screenSubtitle(mode: Destination, count: Int, language: String): String = when (mode) {
    Destination.Tonight -> if (language == "zh") "$count 杯候选，按今晚的心情慢慢挑。" else "$count drinks saved for tonight."
    Destination.Favorites -> if (language == "zh") "$count 杯被你留下的味道。" else "$count saved favorites."
    Destination.Recent -> if (language == "zh") "最近翻阅的 $count 杯配方。" else "$count recently viewed recipes."
    else -> if (language == "zh") "私人记录、经典配方与灵感特调，共 $count 杯。" else "Personal notes, classics and inspired mixes. $count recipes."
}
