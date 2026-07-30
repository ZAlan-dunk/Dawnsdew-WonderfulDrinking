package com.zalandunk.dawnsdew.ui

import android.provider.Settings
import androidx.core.graphics.toColorInt
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.PlaylistAddCheck
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zalandunk.dawnsdew.data.AppPreferences
import com.zalandunk.dawnsdew.data.Catalog
import com.zalandunk.dawnsdew.data.CatalogRepository
import com.zalandunk.dawnsdew.data.Ingredient
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeIngredient
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

enum class Destination(
    val icon: ImageVector,
    val zh: String,
    val en: String
) {
    Home(Icons.Rounded.Home, "首页", "Home"),
    Recipes(Icons.AutoMirrored.Rounded.MenuBook, "配方", "Recipes"),
    Tonight(Icons.AutoMirrored.Rounded.PlaylistAddCheck, "今夜", "Tonight"),
    Favorites(Icons.Rounded.Favorite, "收藏", "Favorites")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DawnsDewApp() {
    val context = LocalContext.current
    val preferences = remember { AppPreferences(context) }
    val catalogResult = remember { runCatching { CatalogRepository(context).load() } }
    val catalog = catalogResult.getOrNull()
    val motionEnabled = rememberMotionEnabled()

    if (catalog == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("配方数据加载失败", style = MaterialTheme.typography.titleLarge)
                Text(catalogResult.exceptionOrNull()?.message.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    var language by remember { mutableStateOf(preferences.language) }
    var destination by remember { mutableStateOf(Destination.Home) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var favoriteIds by remember { mutableStateOf(preferences.favoriteIds()) }
    var tonightIds by remember { mutableStateOf(preferences.tonightIds()) }

    fun toggleFavorite(id: String) {
        favoriteIds = favoriteIds.toggle(id)
        preferences.saveFavoriteIds(favoriteIds)
    }

    fun toggleTonight(id: String) {
        tonightIds = tonightIds.toggle(id)
        preferences.saveTonightIds(tonightIds)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF090A0D), Color(0xFF100D13), Color(0xFF090A0D))
                )
            )
    ) {
        AmbientBackdrop(motionEnabled)
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val expanded = maxWidth >= 840.dp
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    if (language == "zh") "朝露酒笺" else "Dawn's Dew",
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    "Native · 0.3 Alpha",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    language = if (language == "zh") "en" else "zh"
                                    preferences.language = language
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.Language,
                                    contentDescription = if (language == "zh") "Switch to English" else "切换到中文"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xED090A0D)
                        )
                    )
                },
                bottomBar = {
                    if (!expanded) {
                        NavigationBar(containerColor = Color(0xF5151318)) {
                            Destination.entries.forEach { item ->
                                NavigationBarItem(
                                    selected = destination == item,
                                    onClick = { destination = item },
                                    icon = { Icon(item.icon, contentDescription = null) },
                                    label = {
                                        Text(
                                            item.label(language),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(Modifier.fillMaxSize().padding(innerPadding)) {
                    if (expanded) {
                        NavigationRail(containerColor = Color(0xDB100F13)) {
                            Spacer(Modifier.height(12.dp))
                            Destination.entries.forEach { item ->
                                NavigationRailItem(
                                    selected = destination == item,
                                    onClick = { destination = item },
                                    icon = { Icon(item.icon, contentDescription = null) },
                                    label = { Text(item.label(language)) }
                                )
                            }
                        }
                    }
                    AnimatedContent(
                        targetState = destination,
                        modifier = Modifier.weight(1f),
                        transitionSpec = {
                            (fadeIn(tween(motionDuration(motionEnabled, 260))) +
                                scaleIn(
                                    initialScale = 0.985f,
                                    animationSpec = tween(motionDuration(motionEnabled, 320))
                                )) togetherWith
                                (fadeOut(tween(motionDuration(motionEnabled, 180))) +
                                    scaleOut(
                                        targetScale = 1.01f,
                                        animationSpec = tween(motionDuration(motionEnabled, 180))
                                    ))
                        },
                        label = "destination"
                    ) { target ->
                        AppContent(
                            modifier = Modifier.fillMaxSize(),
                            catalog = catalog,
                            language = language,
                            destination = target,
                            favoriteIds = favoriteIds,
                            tonightIds = tonightIds,
                            motionEnabled = motionEnabled,
                            onDestination = { destination = it },
                            onOpenRecipe = { selectedRecipe = it },
                            onToggleFavorite = ::toggleFavorite,
                            onToggleTonight = ::toggleTonight
                        )
                    }
                }
            }
        }
    }

    selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            ingredients = catalog.ingredients.associateBy { it.id },
            language = language,
            isFavorite = recipe.id in favoriteIds,
            isTonight = recipe.id in tonightIds,
            motionEnabled = motionEnabled,
            onDismiss = { selectedRecipe = null },
            onToggleFavorite = { toggleFavorite(recipe.id) },
            onToggleTonight = { toggleTonight(recipe.id) }
        )
    }
}

@Composable
private fun AmbientBackdrop(motionEnabled: Boolean) {
    val drift = if (motionEnabled) {
        val transition = rememberInfiniteTransition(label = "ambient")
        val value by transition.animateFloat(
            initialValue = -0.08f,
            targetValue = 0.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(7000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ambientDrift"
        )
        value
    } else {
        0f
    }
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            color = Color(0xFFE98252).copy(alpha = 0.08f),
            radius = size.minDimension * 0.43f,
            center = Offset(size.width * (0.88f + drift), size.height * 0.04f)
        )
        drawCircle(
            color = Color(0xFF8B68AA).copy(alpha = 0.07f),
            radius = size.minDimension * 0.5f,
            center = Offset(size.width * (0.12f - drift), size.height * 0.94f)
        )
    }
}

@Composable
private fun AppContent(
    modifier: Modifier,
    catalog: Catalog,
    language: String,
    destination: Destination,
    favoriteIds: Set<String>,
    tonightIds: Set<String>,
    motionEnabled: Boolean,
    onDestination: (Destination) -> Unit,
    onOpenRecipe: (Recipe) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleTonight: (String) -> Unit
) {
    val ingredientMap = remember(catalog) { catalog.ingredients.associateBy { it.id } }
    when (destination) {
        Destination.Home -> HomeScreen(
            modifier = modifier,
            catalog = catalog,
            language = language,
            favoriteCount = favoriteIds.size,
            tonightCount = tonightIds.size,
            motionEnabled = motionEnabled,
            onBrowse = { onDestination(Destination.Recipes) },
            onTonight = { onDestination(Destination.Tonight) },
            onOpenRecipe = onOpenRecipe,
            onToggleFavorite = onToggleFavorite,
            onToggleTonight = onToggleTonight,
            favoriteIds = favoriteIds,
            tonightIds = tonightIds
        )
        Destination.Recipes -> RecipeGridScreen(
            modifier, catalog.recipes, ingredientMap, language, favoriteIds, tonightIds,
            motionEnabled, onOpenRecipe, onToggleFavorite, onToggleTonight
        )
        Destination.Tonight -> RecipeGridScreen(
            modifier = modifier,
            recipes = catalog.recipes.filter { it.id in tonightIds },
            ingredientMap = ingredientMap,
            language = language,
            favoriteIds = favoriteIds,
            tonightIds = tonightIds,
            motionEnabled = motionEnabled,
            onOpenRecipe = onOpenRecipe,
            onToggleFavorite = onToggleFavorite,
            onToggleTonight = onToggleTonight,
            emptyMessage = if (language == "zh") "今夜酒单还是空的" else "Tonight's menu is empty"
        )
        Destination.Favorites -> RecipeGridScreen(
            modifier = modifier,
            recipes = catalog.recipes.filter { it.id in favoriteIds },
            ingredientMap = ingredientMap,
            language = language,
            favoriteIds = favoriteIds,
            tonightIds = tonightIds,
            motionEnabled = motionEnabled,
            onOpenRecipe = onOpenRecipe,
            onToggleFavorite = onToggleFavorite,
            onToggleTonight = onToggleTonight,
            emptyMessage = if (language == "zh") "还没有收藏配方" else "No favorite recipes yet"
        )
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier,
    catalog: Catalog,
    language: String,
    favoriteCount: Int,
    tonightCount: Int,
    motionEnabled: Boolean,
    onBrowse: () -> Unit,
    onTonight: () -> Unit,
    onOpenRecipe: (Recipe) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleTonight: (String) -> Unit,
    favoriteIds: Set<String>,
    tonightIds: Set<String>
) {
    val featured = catalog.recipes.firstOrNull()
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        DawnHero(language, motionEnabled, onBrowse, onTonight)
        ResponsiveStats(catalog.recipes.size, favoriteCount, tonightCount, language)
        featured?.let {
            Text(
                if (language == "zh") "今日推荐" else "Featured",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            RecipeCard(
                recipe = it,
                language = language,
                isFavorite = it.id in favoriteIds,
                isTonight = it.id in tonightIds,
                motionEnabled = motionEnabled,
                onOpenRecipe = onOpenRecipe,
                onToggleFavorite = onToggleFavorite,
                onToggleTonight = onToggleTonight
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DawnHero(
    language: String,
    motionEnabled: Boolean,
    onBrowse: () -> Unit,
    onTonight: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF68283B), Color(0xFF352941), Color(0xFF141217))
                )
            )
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth().heightIn(min = 280.dp)) {
            val compact = maxWidth < 620.dp
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x33F2B06B), Color.Transparent),
                            center = Offset(maxWidth.value * 2.1f, 0f),
                            radius = maxWidth.value * 1.8f
                        )
                    )
            )
            if (compact) {
                Column(
                    Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DawnGlassArt(
                        modifier = Modifier.align(Alignment.CenterHorizontally).size(148.dp),
                        motionEnabled = motionEnabled
                    )
                    HeroCopy(language, onBrowse, onTonight)
                }
            } else {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(26.dp)
                ) {
                    HeroCopy(language, onBrowse, onTonight, Modifier.weight(1f))
                    DawnGlassArt(Modifier.size(230.dp), motionEnabled)
                }
            }
        }
    }
}

@Composable
private fun HeroCopy(
    language: String,
    onBrowse: () -> Unit,
    onTonight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            if (language == "zh") "便利店材料，也能调一杯" else "Mix something good with what you have",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black
        )
        Text(
            if (language == "zh") "原生 Android 第一阶段：更清晰的配方浏览、收藏与今夜酒单。" else "Native Android milestone one: a clearer way to browse, save and plan tonight's drinks.",
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onBrowse) { Text(if (language == "zh") "浏览配方" else "Browse") }
            FilledTonalButton(onClick = onTonight) { Text(if (language == "zh") "今夜酒单" else "Tonight") }
        }
    }
}

@Composable
private fun DawnGlassArt(modifier: Modifier, motionEnabled: Boolean) {
    val pulse = if (motionEnabled) {
        val transition = rememberInfiniteTransition(label = "dawnGlass")
        val value by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(4600),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dawnPulse"
        )
        value
    } else {
        0.35f
    }
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.52f, h * 0.48f)
        drawCircle(
            color = Color(0xFFFFB16B).copy(alpha = 0.10f + pulse * 0.08f),
            radius = w * (0.38f + pulse * 0.025f),
            center = center
        )
        drawCircle(
            color = Color(0xFFD8B26E).copy(alpha = 0.22f),
            radius = w * 0.28f,
            center = center,
            style = Stroke(width = w * 0.008f)
        )
        val glass = Path().apply {
            moveTo(w * 0.26f, h * 0.20f)
            lineTo(w * 0.76f, h * 0.20f)
            lineTo(w * 0.66f, h * 0.78f)
            quadraticTo(w * 0.62f, h * 0.84f, w * 0.51f, h * 0.84f)
            quadraticTo(w * 0.40f, h * 0.84f, w * 0.36f, h * 0.78f)
            close()
        }
        clipPath(glass) {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFE99A53), Color(0xFF9C3442), Color(0xFF542B52)),
                    startY = h * 0.37f,
                    endY = h * 0.84f
                ),
                topLeft = Offset(w * 0.24f, h * 0.38f),
                size = Size(w * 0.54f, h * 0.5f),
                alpha = 0.88f
            )
            drawCircle(Color.White.copy(alpha = 0.13f), w * 0.12f, Offset(w * 0.43f, h * 0.55f))
        }
        drawPath(glass, Color.White.copy(alpha = 0.72f), style = Stroke(width = w * 0.012f))
        drawLine(
            color = Color.White.copy(alpha = 0.55f),
            start = Offset(w * 0.32f, h * 0.31f),
            end = Offset(w * 0.40f, h * 0.69f),
            strokeWidth = w * 0.018f,
            cap = StrokeCap.Round
        )
        drawCircle(Color(0xFFF1D49A), w * 0.048f, Offset(w * 0.72f, h * 0.24f))
        drawLine(
            Color(0xFFE98252),
            Offset(w * 0.71f, h * 0.25f),
            Offset(w * 0.82f, h * 0.09f),
            strokeWidth = w * 0.016f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ResponsiveStats(recipeCount: Int, favoriteCount: Int, tonightCount: Int, language: String) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val compact = maxWidth < 520.dp
        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.fillMaxWidth(), recipeCount.toString(), if (language == "zh") "内置配方" else "Recipes")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(Modifier.weight(1f), favoriteCount.toString(), if (language == "zh") "已收藏" else "Favorites")
                    StatCard(Modifier.weight(1f), tonightCount.toString(), if (language == "zh") "今夜候选" else "Tonight")
                }
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), recipeCount.toString(), if (language == "zh") "内置配方" else "Recipes")
                StatCard(Modifier.weight(1f), favoriteCount.toString(), if (language == "zh") "已收藏" else "Favorites")
                StatCard(Modifier.weight(1f), tonightCount.toString(), if (language == "zh") "今夜候选" else "Tonight")
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String) {
    Card(
        modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.66f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RecipeGridScreen(
    modifier: Modifier,
    recipes: List<Recipe>,
    ingredientMap: Map<String, Ingredient>,
    language: String,
    favoriteIds: Set<String>,
    tonightIds: Set<String>,
    motionEnabled: Boolean,
    onOpenRecipe: (Recipe) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleTonight: (String) -> Unit,
    emptyMessage: String = if (language == "zh") "没有符合条件的配方" else "No matching recipes"
) {
    var query by remember { mutableStateOf("") }
    val normalized = query.trim().lowercase(Locale.ROOT)
    val filtered = remember(recipes, normalized, language) {
        if (normalized.isBlank()) recipes else recipes.filter { recipe ->
            buildString {
                append(recipe.name.zh).append(' ').append(recipe.name.en).append(' ')
                append(recipe.aliases.joinToString(" ")).append(' ')
                recipe.ingredients.forEach {
                    append(ingredientMap[it.id]?.name?.zh).append(' ')
                    append(ingredientMap[it.id]?.name?.en).append(' ')
                }
            }.lowercase(Locale.ROOT).contains(normalized)
        }
    }
    Column(modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            label = { Text(if (language == "zh") "搜索名称或材料" else "Search names or ingredients") }
        )
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(emptyMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(250.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(filtered, key = { _, recipe -> recipe.id }) { index, recipe ->
                    var visible by remember(recipe.id) { mutableStateOf(!motionEnabled) }
                    LaunchedEffect(recipe.id, motionEnabled) {
                        if (motionEnabled) {
                            delay(index.coerceAtMost(8) * 38L)
                            visible = true
                        } else {
                            visible = true
                        }
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(motionDuration(motionEnabled, 260))) +
                            slideInVertically(
                                initialOffsetY = { it / 8 },
                                animationSpec = tween(motionDuration(motionEnabled, 320))
                            ),
                        exit = fadeOut(tween(motionDuration(motionEnabled, 120))) +
                            slideOutVertically(
                                targetOffsetY = { it / 12 },
                                animationSpec = tween(motionDuration(motionEnabled, 120))
                            )
                    ) {
                        RecipeCard(
                            recipe = recipe,
                            language = language,
                            isFavorite = recipe.id in favoriteIds,
                            isTonight = recipe.id in tonightIds,
                            motionEnabled = motionEnabled,
                            onOpenRecipe = onOpenRecipe,
                            onToggleFavorite = onToggleFavorite,
                            onToggleTonight = onToggleTonight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    language: String,
    isFavorite: Boolean,
    isTonight: Boolean,
    motionEnabled: Boolean,
    onOpenRecipe: (Recipe) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleTonight: (String) -> Unit
) {
    val colors = recipe.gradientColors()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(motionDuration(motionEnabled, if (pressed) 90 else 180)),
        label = "cardPress"
    )
    Card(
        onClick = { onOpenRecipe(recipe) },
        modifier = Modifier.fillMaxWidth().graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp, pressedElevation = 1.dp),
        interactionSource = interactionSource
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(218.dp)
                .background(Brush.linearGradient(colors))
        ) {
            MiniGlassArt(
                modifier = Modifier.align(Alignment.Center).size(150.dp),
                colors = colors
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x33000000), Color(0xD908080A))
                        )
                    )
            )
            Column(Modifier.align(Alignment.BottomStart).padding(18.dp).fillMaxWidth()) {
                Text(
                    originLabel(recipe.origin, language),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    recipe.name.value(language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    recipe.name.secondary(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                ToggleIconButton(
                    active = isTonight,
                    activeIcon = Icons.Rounded.Check,
                    inactiveIcon = Icons.Rounded.LocalBar,
                    activeDescription = if (language == "zh") "从今夜酒单移除" else "Remove from tonight",
                    inactiveDescription = if (language == "zh") "加入今夜酒单" else "Add to tonight",
                    motionEnabled = motionEnabled,
                    onClick = { onToggleTonight(recipe.id) }
                )
                ToggleIconButton(
                    active = isFavorite,
                    activeIcon = Icons.Rounded.Favorite,
                    inactiveIcon = Icons.Rounded.FavoriteBorder,
                    activeDescription = if (language == "zh") "取消收藏" else "Unfavorite",
                    inactiveDescription = if (language == "zh") "收藏" else "Favorite",
                    motionEnabled = motionEnabled,
                    onClick = { onToggleFavorite(recipe.id) }
                )
            }
        }
    }
}

@Composable
private fun MiniGlassArt(modifier: Modifier, colors: List<Color>) {
    Canvas(modifier.graphicsLayer(alpha = 0.5f)) {
        val w = size.width
        val h = size.height
        val glass = Path().apply {
            moveTo(w * 0.28f, h * 0.16f)
            lineTo(w * 0.72f, h * 0.16f)
            lineTo(w * 0.64f, h * 0.78f)
            quadraticTo(w * 0.61f, h * 0.84f, w * 0.50f, h * 0.84f)
            quadraticTo(w * 0.39f, h * 0.84f, w * 0.36f, h * 0.78f)
            close()
        }
        clipPath(glass) {
            drawRect(
                Brush.verticalGradient(colors),
                topLeft = Offset(0f, h * 0.38f),
                size = Size(w, h * 0.5f),
                alpha = 0.9f
            )
        }
        drawPath(glass, Color.White.copy(alpha = 0.75f), style = Stroke(w * 0.012f))
        drawLine(
            Color.White.copy(alpha = 0.42f),
            Offset(w * 0.35f, h * 0.28f),
            Offset(w * 0.40f, h * 0.67f),
            strokeWidth = w * 0.018f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ToggleIconButton(
    active: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    activeDescription: String,
    inactiveDescription: String,
    motionEnabled: Boolean,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        if (active) MaterialTheme.colorScheme.primary else Color.White,
        tween(motionDuration(motionEnabled, 220)),
        label = "toggleTint"
    )
    val scale by animateFloatAsState(
        if (active) 1.12f else 1f,
        tween(motionDuration(motionEnabled, 180)),
        label = "toggleScale"
    )
    Surface(
        shape = CircleShape,
        color = Color(0x66090A0D),
        modifier = Modifier.padding(2.dp)
    ) {
        IconButton(onClick = onClick) {
            AnimatedContent(
                targetState = active,
                transitionSpec = {
                    (fadeIn(tween(motionDuration(motionEnabled, 150))) + scaleIn(initialScale = 0.72f)) togetherWith
                        (fadeOut(tween(motionDuration(motionEnabled, 100))) + scaleOut(targetScale = 0.72f))
                },
                label = "toggleIcon"
            ) { selected ->
                Icon(
                    imageVector = if (selected) activeIcon else inactiveIcon,
                    contentDescription = if (selected) activeDescription else inactiveDescription,
                    tint = tint,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                )
            }
        }
    }
}

@Composable
private fun RecipeDetailDialog(
    recipe: Recipe,
    ingredients: Map<String, Ingredient>,
    language: String,
    isFavorite: Boolean,
    isTonight: Boolean,
    motionEnabled: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleTonight: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.94f).fillMaxHeight(0.92f).widthIn(max = 760.dp),
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 8.dp
        ) {
            LazyColumn {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(226.dp)
                            .background(Brush.linearGradient(recipe.gradientColors()))
                    ) {
                        MiniGlassArt(
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 26.dp).size(170.dp),
                            colors = recipe.gradientColors()
                        )
                        Box(
                            Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color(0xC90A090C))
                                    )
                                )
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = if (language == "zh") "关闭" else "Close",
                                tint = Color.White
                            )
                        }
                        Column(Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                            Text(originLabel(recipe.origin, language), color = Color.White.copy(alpha = 0.8f))
                            Text(
                                recipe.name.value(language),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(recipe.name.secondary(language), color = Color.White.copy(alpha = 0.75f))
                        }
                    }
                }
                item {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(recipe.description.value(language), style = MaterialTheme.typography.bodyLarge)
                        BoxWithConstraints(Modifier.fillMaxWidth()) {
                            if (maxWidth < 500.dp) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    DetailActionButtons(
                                        language, isFavorite, isTonight, motionEnabled,
                                        onToggleFavorite, onToggleTonight, true
                                    )
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    DetailActionButtons(
                                        language, isFavorite, isTonight, motionEnabled,
                                        onToggleFavorite, onToggleTonight, false
                                    )
                                }
                            }
                        }
                        Text(
                            if (language == "zh") "材料" else "Ingredients",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                items(recipe.ingredients) { item ->
                    val ingredient = ingredients[item.id]
                    ListItem(
                        headlineContent = { Text(ingredient?.name?.value(language) ?: item.id) },
                        supportingContent = if (item.optional) {
                            { Text(if (language == "zh") "可选" else "Optional") }
                        } else {
                            null
                        },
                        trailingContent = { Text(amountLabel(item, language), fontWeight = FontWeight.Bold) }
                    )
                    HorizontalDivider(
                        Modifier.padding(horizontal = 22.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
                    )
                }
                item {
                    Text(
                        if (language == "zh") "步骤" else "Steps",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(22.dp, 24.dp, 22.dp, 8.dp)
                    )
                }
                items(recipe.steps.value(language).withIndex().toList()) { indexed ->
                    Row(
                        Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(
                                (indexed.index + 1).toString(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(indexed.value, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                item {
                    recipe.sourceText.takeIf { it.isNotBlank() }?.let {
                        Text(
                            if (language == "zh") "原始记录：$it" else "Original note: $it",
                            modifier = Modifier.padding(22.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(if (language == "zh") "关闭" else "Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailActionButtons(
    language: String,
    isFavorite: Boolean,
    isTonight: Boolean,
    motionEnabled: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleTonight: () -> Unit,
    fillWidth: Boolean
) {
    val modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier
    Button(onClick = onToggleFavorite, modifier = modifier) {
        AnimatedToggleIcon(
            active = isFavorite,
            activeIcon = Icons.Rounded.Favorite,
            inactiveIcon = Icons.Rounded.FavoriteBorder,
            motionEnabled = motionEnabled
        )
        Text(
            if (language == "zh") {
                if (isFavorite) "已收藏" else "收藏"
            } else {
                if (isFavorite) "Favorited" else "Favorite"
            },
            modifier = Modifier.padding(start = 6.dp)
        )
    }
    FilledTonalButton(onClick = onToggleTonight, modifier = modifier) {
        AnimatedToggleIcon(
            active = isTonight,
            activeIcon = Icons.Rounded.Check,
            inactiveIcon = Icons.Rounded.LocalBar,
            motionEnabled = motionEnabled
        )
        Text(
            if (language == "zh") {
                if (isTonight) "已加入今夜" else "加入今夜"
            } else {
                if (isTonight) "In tonight" else "Add tonight"
            },
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun AnimatedToggleIcon(
    active: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    motionEnabled: Boolean
) {
    AnimatedContent(
        targetState = active,
        transitionSpec = {
            (fadeIn(tween(motionDuration(motionEnabled, 160))) + scaleIn(initialScale = 0.7f)) togetherWith
                (fadeOut(tween(motionDuration(motionEnabled, 90))) + scaleOut(targetScale = 0.7f))
        },
        label = "actionIcon"
    ) { selected ->
        Icon(if (selected) activeIcon else inactiveIcon, contentDescription = null)
    }
}

@Composable
private fun rememberMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) > 0f
        }.getOrDefault(true)
    }
}

private fun motionDuration(enabled: Boolean, duration: Int): Int = if (enabled) duration else 0

private fun Destination.label(language: String) = if (language == "zh") zh else en

private fun Set<String>.toggle(id: String): Set<String> = if (id in this) this - id else this + id

private fun Recipe.gradientColors(): List<Color> {
    val values = colors.take(2).mapNotNull {
        runCatching { Color(it.toColorInt()) }.getOrNull()
    }
    return if (values.size == 2) values else listOf(Color(0xFF5E2738), Color(0xFF372E4D))
}

private fun originLabel(origin: String, language: String): String = when (origin) {
    "personal" -> if (language == "zh") "个人酒单" else "Personal"
    "convenience" -> if (language == "zh") "便利店灵感" else "Convenience"
    "custom" -> if (language == "zh") "自定义" else "Custom"
    else -> if (language == "zh") "经典配方" else "Classic"
}

private fun amountLabel(item: RecipeIngredient, language: String): String = when {
    item.topUp -> if (language == "zh") "补满" else "Top up"
    item.fillTo != null -> if (language == "zh") {
        "至 ${(item.fillTo * 10).roundToInt()} 分满"
    } else {
        "Fill to ${formatNumber(item.fillTo * 10)}/10"
    }
    item.unit == "piece" -> if (language == "zh") {
        "${formatNumber(item.amount ?: 0.0)} 份"
    } else {
        "${formatNumber(item.amount ?: 0.0)} pcs"
    }
    else -> "${formatNumber(item.amount ?: 0.0)} ml"
}

private fun formatNumber(value: Double): String = if (value % 1.0 == 0.0) {
    value.roundToInt().toString()
} else {
    String.format(Locale.ROOT, "%.1f", value)
}
