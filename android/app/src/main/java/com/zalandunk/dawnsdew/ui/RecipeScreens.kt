package com.zalandunk.dawnsdew.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeCalculator
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
    val detailedFilterCount = listOf(filters.origin, filters.base, filters.taste, filters.difficulty).count(String::isNotBlank)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val fontScale = LocalDensity.current.fontScale * controller.state.settings.fontScale
        val compactGrid = maxWidth >= 350.dp && fontScale <= 1.2f
        Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactSearchField(
                    query = query,
                    placeholder = if (language == "zh") "搜索酒名、基酒或材料" else "Search drinks or ingredients",
                    onQueryChange = { query = it },
                    modifier = Modifier.weight(1f)
                )
                RoundToolButton(
                    contentDescription = if (language == "zh") "详细筛选" else "Detailed filters",
                    count = detailedFilterCount,
                    onClick = { showFilters = true }
                ) {
                    Icon(Icons.Rounded.FilterList, null)
                }
                if (mode == Destination.Tonight && source.isNotEmpty()) {
                    RoundToolButton(
                        contentDescription = if (language == "zh") "清空今夜酒单" else "Clear tonight",
                        onClick = {
                            controller.clearTonight()
                            onMessage(if (language == "zh") "今夜酒单已清空" else "Tonight's menu cleared")
                        }
                    ) { Icon(Icons.Rounded.DeleteSweep, null) }
                }
            }
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !filters.makeableOnly && !filters.favoriteOnly,
                    onClick = { filters = filters.copy(makeableOnly = false, favoriteOnly = false) },
                    label = { Text(if (language == "zh") "全部 ${filtered.size}" else "All ${filtered.size}") }
                )
                FilterChip(
                    selected = filters.makeableOnly,
                    onClick = { filters = filters.copy(makeableOnly = !filters.makeableOnly, favoriteOnly = false) },
                    label = { Text(if (language == "zh") "此刻可调" else "Makeable") }
                )
                FilterChip(
                    selected = filters.favoriteOnly,
                    onClick = { filters = filters.copy(favoriteOnly = !filters.favoriteOnly, makeableOnly = false) },
                    label = { Text(if (language == "zh") "收藏" else "Favorites") }
                )
                if (filters != RecipeFilters()) {
                    FilterChip(
                        selected = false,
                        onClick = { filters = RecipeFilters() },
                        leadingIcon = { Icon(Icons.Rounded.Close, null, modifier = Modifier.size(17.dp)) },
                        label = { Text(if (language == "zh") "清除" else "Clear") }
                    )
                }
            }
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (mode == Destination.Tonight && source.isEmpty()) {
                            if (language == "zh") "今夜酒单还是空的" else "Tonight's menu is empty"
                        } else if (language == "zh") "没有符合条件的配方" else "No matching recipes",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = if (compactGrid) GridCells.Fixed(2) else GridCells.Adaptive(260.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 28.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    itemsIndexed(filtered, key = { _, recipe -> recipe.id }) { index, recipe ->
                        var visible by remember(recipe.id, mode) { mutableStateOf(!motion.enabled) }
                        LaunchedEffect(recipe.id, motion.enabled) {
                            if (motion.enabled) delay(index.coerceAtMost(5) * 38L)
                            visible = true
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(motionDuration(motion, 220))) +
                                slideInVertically(initialOffsetY = { it / 7 }, animationSpec = tween(motionDuration(motion, 260)))
                        ) {
                            RecipeCard(
                                recipe = recipe,
                                ingredientMap = controller.ingredientMap,
                                state = controller.state,
                                language = language,
                                motion = motion,
                                compact = compactGrid,
                                onOpen = controller::openRecipe,
                                onToggleFavorite = controller::toggleFavorite,
                                onToggleTonight = controller::toggleTonight
                            )
                        }
                    }
                }
            }
        }
    }
    if (showFilters) {
        RecipeFilterSheet(
            recipes = source,
            controller = controller,
            current = filters,
            onApply = { filters = it; showFilters = false },
            onDismiss = { showFilters = false }
        )
    }
}

@Composable
private fun CompactSearchField(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(48.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            Modifier.fillMaxSize().padding(start = 13.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Icon(Icons.Rounded.Search, null, modifier = Modifier.size(20.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner ->
                    Box {
                        if (query.isBlank()) Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        inner()
                    }
                }
            )
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Close, if (placeholder.startsWith("搜索")) "清除搜索" else "Clear search")
                }
            }
        }
    }
}

@Composable
private fun RoundToolButton(
    contentDescription: String,
    count: Int = 0,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.size(48.dp).semantics { this.contentDescription = contentDescription }.clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
            if (count > 0) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).size(18.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(count.toString(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeFilterSheet(
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
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(if (language == "zh") "筛选配方" else "Filter recipes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TextButton(onClick = { draft = RecipeFilters() }) { Text(if (language == "zh") "清除" else "Clear") }
                Spacer(Modifier.weight(1f))
                Button(onClick = { onApply(draft) }) { Text(if (language == "zh") "应用筛选" else "Apply filters") }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
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
            Icon(Icons.Rounded.ExpandMore, null)
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
