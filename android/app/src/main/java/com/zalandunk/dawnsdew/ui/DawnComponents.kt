package com.zalandunk.dawnsdew.ui

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt
import com.zalandunk.dawnsdew.data.AppState
import com.zalandunk.dawnsdew.data.BrandProfiles
import com.zalandunk.dawnsdew.data.Ingredient
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeCalculator
import com.zalandunk.dawnsdew.ui.theme.DawnPalette

@Composable
internal fun AmbientBackdrop() {
    val wash = MaterialTheme.colorScheme.primary.copy(alpha = 0.035f)
    Canvas(Modifier.fillMaxWidth().fillMaxHeight()) {
        drawRect(
            brush = Brush.verticalGradient(listOf(wash, Color.Transparent, wash.copy(alpha = 0.018f))),
            size = size
        )
    }
}

@Composable
internal fun SectionTitle(title: String, subtitle: String? = null, action: (@Composable () -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        action?.invoke()
    }
}

@Composable
internal fun DawnGlassArt(modifier: Modifier, motion: MotionPolicy) {
    val pulse = if (motion.ambientEnabled) {
        val transition = rememberInfiniteTransition(label = "glassAmbient")
        val value by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(2800), repeatMode = RepeatMode.Reverse),
            label = "liquidGlow"
        )
        value
    } else {
        0.45f
    }
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.52f, h * 0.50f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(DawnPalette.Gold.copy(alpha = 0.26f + pulse * 0.10f), Color.Transparent),
                center = center,
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = center
        )
        drawCircle(
            DawnPalette.Coral.copy(alpha = 0.12f),
            radius = w * 0.34f,
            center = center,
            style = Stroke(w * 0.008f)
        )
        val glass = Path().apply {
            moveTo(w * 0.25f, h * 0.18f)
            lineTo(w * 0.77f, h * 0.18f)
            lineTo(w * 0.65f, h * 0.76f)
            quadraticTo(w * 0.61f, h * 0.83f, w * 0.51f, h * 0.83f)
            quadraticTo(w * 0.40f, h * 0.83f, w * 0.36f, h * 0.76f)
            close()
        }
        clipPath(glass) {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(DawnPalette.Gold, DawnPalette.Coral, DawnPalette.Wine),
                    startY = h * (0.34f - pulse * 0.018f),
                    endY = h * 0.84f
                ),
                topLeft = Offset(w * 0.22f, h * (0.36f - pulse * 0.018f)),
                size = Size(w * 0.58f, h * 0.52f),
                alpha = 0.94f
            )
            drawCircle(Color.White.copy(alpha = 0.16f), w * 0.11f, Offset(w * 0.43f, h * 0.54f))
        }
        drawPath(glass, Color.White.copy(alpha = 0.88f), style = Stroke(w * 0.014f))
        drawLine(
            Color.White.copy(alpha = 0.65f),
            Offset(w * 0.32f, h * 0.29f),
            Offset(w * 0.40f, h * 0.67f),
            strokeWidth = w * 0.019f,
            cap = StrokeCap.Round
        )
        drawCircle(DawnPalette.Gold, w * 0.052f, Offset(w * 0.73f, h * 0.23f))
        drawLine(
            DawnPalette.Sage,
            Offset(w * 0.72f, h * 0.24f),
            Offset(w * 0.83f, h * 0.08f),
            strokeWidth = w * 0.017f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
internal fun RecipeCard(
    recipe: Recipe,
    ingredientMap: Map<String, Ingredient>,
    state: AppState,
    language: String,
    motion: MotionPolicy,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onOpen: (Recipe) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleTonight: (String) -> Unit
) {
    val colors = recipe.gradientColors()
    val match = remember(recipe, state.pantry) { RecipeCalculator.match(recipe, state.pantry) }
    val estimate = remember(recipe, state.settings, state.pantry) {
        RecipeCalculator.estimate(recipe, state.settings, state.pantry, ingredientMap)
    }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.975f else 1f,
        tween(motionDuration(motion, if (pressed) 80 else 170)),
        label = "recipePress"
    )
    Card(
        onClick = { onOpen(recipe) },
        modifier = modifier.fillMaxWidth().graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
        interactionSource = interactionSource
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(if (compact) 194.dp else 226.dp)
                .background(Brush.linearGradient(colors))
        ) {
            MiniGlassArt(Modifier.align(Alignment.Center).size(if (compact) 122.dp else 152.dp), colors)
            Box(
                Modifier.matchParentSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0x33000000), Color(0xE00A090C)))
                )
            )
            Surface(
                color = if (match.status == "makeable") MaterialTheme.colorScheme.tertiaryContainer else Color(0xD92A252B),
                contentColor = if (match.status == "makeable") MaterialTheme.colorScheme.onTertiaryContainer else Color.White,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.TopStart).padding(if (compact) 7.dp else 10.dp)
            ) {
                Text(
                    if (compact) compactMatchLabel(match.status, match.missing.size, language) else matchLabel(match.status, match.missing.size, language),
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                ToggleIconButton(
                    active = recipe.id in state.tonightIds,
                    activeIcon = Icons.Rounded.Check,
                    inactiveIcon = Icons.Rounded.LocalBar,
                    activeDescription = if (language == "zh") "移出今夜酒单" else "Remove from tonight",
                    inactiveDescription = if (language == "zh") "加入今夜酒单" else "Add to tonight",
                    motion = motion,
                    onClick = { onToggleTonight(recipe.id) }
                )
                ToggleIconButton(
                    active = recipe.id in state.favoriteIds,
                    activeIcon = Icons.Rounded.Favorite,
                    inactiveIcon = Icons.Rounded.FavoriteBorder,
                    activeDescription = if (language == "zh") "取消收藏" else "Unfavorite",
                    inactiveDescription = if (language == "zh") "收藏" else "Favorite",
                    motion = motion,
                    onClick = { onToggleFavorite(recipe.id) }
                )
            }
            Column(Modifier.align(Alignment.BottomStart).padding(if (compact) 11.dp else 16.dp).fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(originLabel(recipe.origin, language), style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.86f))
                    Text("·", color = Color.White.copy(alpha = 0.60f))
                    Text("${formatNumber(estimate.abv)}% ABV", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                BrandProfiles.primary(recipe)?.let { profile ->
                    Text(
                        "${profile.brand} · ${profile.product}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    recipe.name.value(language),
                    style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!compact) {
                    Text(
                        recipe.name.secondary(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun compactMatchLabel(status: String, missing: Int, language: String): String = when (status) {
    "makeable" -> if (language == "zh") "可调" else "Ready"
    "almost" -> if (language == "zh") "差1" else "-1"
    else -> if (language == "zh") "缺$missing" else "-$missing"
}

@Composable
private fun ToggleIconButton(
    active: Boolean,
    activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
    activeDescription: String,
    inactiveDescription: String,
    motion: MotionPolicy,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        if (active) MaterialTheme.colorScheme.primary else Color.White,
        tween(motionDuration(motion, 180)),
        label = "toggleTint"
    )
    val scale by animateFloatAsState(
        if (active) 1.12f else 1f,
        tween(motionDuration(motion, 170)),
        label = "toggleScale"
    )
    Surface(shape = CircleShape, color = Color(0xA6262125), modifier = Modifier.padding(2.dp)) {
        IconButton(onClick = onClick) {
            AnimatedContent(
                targetState = active,
                transitionSpec = {
                    (fadeIn(tween(motionDuration(motion, 140))) + scaleIn(initialScale = 0.72f)) togetherWith
                        (fadeOut(tween(motionDuration(motion, 90))) + scaleOut(targetScale = 0.72f))
                },
                label = "toggleIcon"
            ) { selected ->
                Icon(
                    if (selected) activeIcon else inactiveIcon,
                    contentDescription = if (selected) activeDescription else inactiveDescription,
            tint = tint,
                    modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
                )
            }
        }
    }
}

@Composable
internal fun MiniGlassArt(modifier: Modifier, colors: List<Color>) {
    Canvas(modifier.graphicsLayer(alpha = 0.72f)) {
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
                topLeft = Offset(0f, h * 0.37f),
                size = Size(w, h * 0.52f),
                alpha = 0.96f
            )
        }
        drawPath(glass, Color.White.copy(alpha = 0.90f), style = Stroke(w * 0.013f))
        drawLine(
            Color.White.copy(alpha = 0.50f),
            Offset(w * 0.35f, h * 0.28f),
            Offset(w * 0.40f, h * 0.67f),
            strokeWidth = w * 0.018f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
internal fun RecipeDetailDialog(
    recipe: Recipe,
    ingredientMap: Map<String, Ingredient>,
    state: AppState,
    language: String,
    motion: MotionPolicy,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleTonight: () -> Unit,
    onAddMissing: () -> Unit
) {
    val estimate = remember(recipe, state.settings, state.pantry) {
        RecipeCalculator.estimate(recipe, state.settings, state.pantry, ingredientMap)
    }
    val match = remember(recipe, state.pantry) { RecipeCalculator.match(recipe, state.pantry) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact = maxWidth < 600.dp
            Surface(
                modifier = if (compact) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier.align(Alignment.Center).fillMaxWidth(0.95f).fillMaxHeight(0.93f).widthIn(max = 760.dp)
                },
                shape = if (compact) RoundedCornerShape(0.dp) else RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp
            ) {
            LazyColumn {
                item {
                    Box(
                        Modifier.fillMaxWidth().height(224.dp).background(Brush.linearGradient(recipe.gradientColors()))
                    ) {
                        MiniGlassArt(Modifier.align(Alignment.CenterEnd).padding(end = 22.dp).size(166.dp), recipe.gradientColors())
                        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xD60A090C)))))
                        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                            Icon(Icons.Rounded.Close, contentDescription = if (language == "zh") "关闭" else "Close", tint = Color.White)
                        }
                        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                            Text(originLabel(recipe.origin, language), color = Color.White.copy(alpha = 0.84f))
                            Text(
                                recipe.name.value(language),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(recipe.name.secondary(language), color = Color.White.copy(alpha = 0.78f))
                        }
                    }
                }
                item {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(recipe.description.value(language), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        BrandSuggestions(recipe, language)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricTile(Modifier.weight(1f), "${formatNumber(estimate.abv)}%", "ABV")
                            MetricTile(Modifier.weight(1f), "${formatNumber(estimate.totalMl)} ml", if (language == "zh") "酒液" else "Volume")
                            MetricTile(
                                Modifier.weight(1f),
                                if (estimate.hasCost) "${state.settings.currency}${formatNumber(estimate.cost, 2)}" else "—",
                                if (language == "zh") "估算成本" else "Cost"
                            )
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onToggleFavorite, modifier = Modifier.weight(1f)) {
                                Icon(if (recipe.id in state.favoriteIds) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, null)
                                Spacer(Modifier.width(6.dp))
                                Text(if (language == "zh") if (recipe.id in state.favoriteIds) "已收藏" else "收藏" else "Favorite")
                            }
                            FilledTonalButton(onClick = onToggleTonight, modifier = Modifier.weight(1f)) {
                                Icon(if (recipe.id in state.tonightIds) Icons.Rounded.Check else Icons.Rounded.LocalBar, null)
                                Spacer(Modifier.width(6.dp))
                                Text(if (language == "zh") if (recipe.id in state.tonightIds) "已入今夜" else "加入今夜" else "Tonight")
                            }
                        }
                        if (match.missing.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(matchLabel(match.status, match.missing.size, language), fontWeight = FontWeight.Bold)
                                    Text(
                                        match.missing.joinToString("、") { ingredientMap[it]?.name?.value(language) ?: it },
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    TextButton(onClick = onAddMissing) {
                                        Text(if (language == "zh") "把缺少材料加入酒柜" else "Add missing items to pantry")
                                    }
                                }
                            }
                        }
                        if (estimate.exceedsCapacity) {
                            Text(
                                if (language == "zh") "固定用量超过当前杯量，请更换杯具或减少冰量。" else "Fixed amounts exceed the current glass capacity.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        SectionTitle(if (language == "zh") "材料" else "Ingredients")
                    }
                }
                items(recipe.ingredients) { item ->
                    val ingredient = ingredientMap[item.id]
                    ListItem(
                        headlineContent = { Text(ingredient?.name?.value(language) ?: item.id, color = MaterialTheme.colorScheme.onSurface) },
                        supportingContent = if (item.optional) {
                            { Text(if (language == "zh") "可选" else "Optional", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else null,
                        trailingContent = { Text(amountLabel(item, language), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                        colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outlineVariant)
                }
                item {
                    SectionTitle(
                        if (language == "zh") "步骤" else "Steps",
                        modifierSubtitle(recipe.method, language),
                    )
                }
                items(recipe.steps.value(language).withIndex().toList()) { indexed ->
                    Row(
                        Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(
                                (indexed.index + 1).toString(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(indexed.value, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                item {
                    recipe.sourceText.takeIf(String::isNotBlank)?.let {
                        Text(
                            if (language == "zh") "原始记录：$it" else "Original note: $it",
                            modifier = Modifier.padding(20.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        if (language == "zh") "酒精度、容量和成本均为估算值。" else "ABV, volume and cost are estimates.",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Text(if (language == "zh") "关闭" else "Close")
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun MetricTile(modifier: Modifier, value: String, label: String) {
    Surface(modifier, color = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
private fun BrandSuggestions(recipe: Recipe, language: String) {
    val suggestions = BrandProfiles.suggestions(recipe)
    if (suggestions.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            if (language == "zh") "基酒建议" else "Base suggestions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { profile ->
                Surface(
                    modifier = Modifier.widthIn(min = 150.dp, max = 210.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(profile.brand, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(profile.product, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(profile.flavor(language), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

private fun modifierSubtitle(method: String, language: String): String = when (method) {
    "shake" -> if (language == "zh") "摇和" else "Shake"
    "stir" -> if (language == "zh") "搅拌" else "Stir"
    else -> if (language == "zh") "杯中直调" else "Build"
}

internal fun Recipe.gradientColors(): List<Color> {
    val values = colors.take(2).mapNotNull { runCatching { Color(it.toColorInt()) }.getOrNull() }
    return if (values.size == 2) values else listOf(DawnPalette.Wine, DawnPalette.Plum)
}
