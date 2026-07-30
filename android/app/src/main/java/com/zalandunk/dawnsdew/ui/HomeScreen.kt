package com.zalandunk.dawnsdew.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.PlaylistAddCheck
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeCalculator
import com.zalandunk.dawnsdew.ui.theme.DawnPalette
import kotlinx.coroutines.delay

@Composable
internal fun HomeScreen(
    controller: AppController,
    motion: MotionPolicy,
    onMessage: (String) -> Unit
) {
    val language = controller.language
    var heroVisible by remember { mutableStateOf(!motion.enabled) }
    var contentVisible by remember { mutableStateOf(!motion.enabled) }
    LaunchedEffect(motion.enabled) {
        heroVisible = true
        if (motion.enabled) delay(140)
        contentVisible = true
    }
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        AnimatedVisibility(
            visible = heroVisible,
            enter = fadeIn(tween(motionDuration(motion, 360))) +
                slideInVertically(initialOffsetY = { it / 7 }, animationSpec = tween(motionDuration(motion, 430)))
        ) {
            PoeticHero(controller, motion)
        }
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(motionDuration(motion, 330))) +
                slideInVertically(initialOffsetY = { 42 }, animationSpec = tween(motionDuration(motion, 400)))
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                HomeStats(controller)
                TonightPreview(controller)
                controller.dailyRecipe?.let { recipe ->
                    SectionTitle(
                        if (language == "zh") "今夜一杯" else "Tonight's pour",
                        if (language == "zh") "每天从酒笺里，替你翻开一页。" else "A new page from the collection each day."
                    )
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
                SectionTitle(
                    if (language == "zh") "随心而选" else "Let the night decide",
                    if (language == "zh") "独酌随缘，聚会则优先挑选酒柜里能完成的配方。" else "Pick freely, or prefer makeable drinks for a gathering."
                )
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    if (maxWidth < 390.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            RandomAction(true, language, Modifier.fillMaxWidth()) {
                                controller.randomRecipe(false)
                                onMessage(if (language == "zh") "今夜的这一杯，已替你选好" else "Tonight's pour is ready")
                            }
                            RandomAction(false, language, Modifier.fillMaxWidth()) {
                                val selected = controller.randomRecipe(true)
                                val makeable = selected?.let { RecipeCalculator.match(it, controller.state.pantry).status == "makeable" } == true
                                onMessage(if (language == "zh") if (makeable) "优先从可制作配方中选择" else "已从全部配方中选择" else "A gathering pick is ready")
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            RandomAction(true, language, Modifier.weight(1f)) {
                                controller.randomRecipe(false)
                                onMessage(if (language == "zh") "今夜的这一杯，已替你选好" else "Tonight's pour is ready")
                            }
                            RandomAction(false, language, Modifier.weight(1f)) {
                                val selected = controller.randomRecipe(true)
                                val makeable = selected?.let { RecipeCalculator.match(it, controller.state.pantry).status == "makeable" } == true
                                onMessage(if (language == "zh") if (makeable) "优先从可制作配方中选择" else "已从全部配方中选择" else "A gathering pick is ready")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PoeticHero(controller: AppController, motion: MotionPolicy) {
    val language = controller.language
    BoxWithConstraints(
        Modifier.fillMaxWidth().background(
            Brush.verticalGradient(
                listOf(Color(0xFF321C2A), Color(0xFF18141A), DawnPalette.Ink)
            )
        )
    ) {
        val compact = maxWidth < 600.dp
        DawnGlassArt(
            modifier = Modifier
                .align(if (compact) Alignment.TopEnd else Alignment.CenterEnd)
                .padding(end = if (compact) 2.dp else 32.dp, top = if (compact) 4.dp else 0.dp)
                .size(if (compact) 176.dp else 250.dp),
            motion = motion
        )
        Column(
            Modifier
                .fillMaxWidth(if (compact) 1f else 0.68f)
                .padding(start = 20.dp, end = if (compact) 20.dp else 8.dp, top = if (compact) 146.dp else 44.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text(
                if (language == "zh") "今夜酒单 · TONIGHT'S POUR" else "TONIGHT'S POUR · 今夜酒单",
                style = MaterialTheme.typography.labelMedium,
                color = DawnPalette.Gold,
                fontWeight = FontWeight.Bold
            )
            Text(
                if (language == "zh") "把今夜的心绪，斟成一杯" else "Pour tonight's mood into a glass",
                style = MaterialTheme.typography.headlineLarge,
                color = DawnPalette.Paper,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                if (language == "zh") {
                    "从暮色到晨露，把想喝的、想念的依次写进酒单；灯光落杯时，就从这一杯开始。"
                } else {
                    "From dusk to morning dew, gather the drinks and memories meant for tonight, then begin with one glass."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = DawnPalette.Muted
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { controller.navigate(Destination.Tonight) }) {
                    Icon(Icons.AutoMirrored.Rounded.PlaylistAddCheck, null)
                    Spacer(Modifier.width(7.dp))
                    Text(if (language == "zh") "打开今夜酒单" else "Open tonight")
                }
                FilledTonalButton(onClick = { controller.navigate(Destination.Recipes) }) {
                    Icon(Icons.AutoMirrored.Rounded.MenuBook, null)
                    Spacer(Modifier.width(7.dp))
                    Text(if (language == "zh") "翻阅配方" else "Browse")
                }
            }
        }
    }
}

@Composable
private fun HomeStats(controller: AppController) {
    val language = controller.language
    val makeable = remember(controller.state.pantry, controller.allRecipes) {
        controller.allRecipes.count { RecipeCalculator.match(it, controller.state.pantry).status == "makeable" }
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val compact = maxWidth < 520.dp
        if (compact) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(Modifier.weight(1f), controller.state.tonightIds.size.toString(), if (language == "zh") "今夜" else "Tonight", DawnPalette.Coral)
                StatTile(Modifier.weight(1f), makeable.toString(), if (language == "zh") "可调" else "Ready", DawnPalette.Sage)
                StatTile(Modifier.weight(1f), controller.state.favoriteIds.size.toString(), if (language == "zh") "收藏" else "Saved", DawnPalette.Gold)
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(Modifier.weight(1f), controller.allRecipes.size.toString(), if (language == "zh") "全部配方" else "Recipes", DawnPalette.Paper)
                StatTile(Modifier.weight(1f), controller.state.tonightIds.size.toString(), if (language == "zh") "今夜候选" else "Tonight", DawnPalette.Coral)
                StatTile(Modifier.weight(1f), makeable.toString(), if (language == "zh") "此刻可调" else "Ready", DawnPalette.Sage)
                StatTile(Modifier.weight(1f), controller.state.favoriteIds.size.toString(), if (language == "zh") "已收藏" else "Saved", DawnPalette.Gold)
            }
        }
    }
}

@Composable
private fun StatTile(modifier: Modifier, value: String, label: String, accent: Color) {
    Surface(modifier, color = DawnPalette.Surface, contentColor = DawnPalette.Paper, shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 13.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = accent, fontWeight = FontWeight.Black)
            Text(label, style = MaterialTheme.typography.labelSmall, color = DawnPalette.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun TonightPreview(controller: AppController) {
    val language = controller.language
    val recipes = controller.state.tonightIds.mapNotNull { id -> controller.allRecipes.find { it.id == id } }
    SectionTitle(
        if (language == "zh") "今夜酒单" else "Tonight's menu",
        if (recipes.isEmpty()) {
            if (language == "zh") "酒单还是空的，先收下一杯心动。" else "The menu is empty. Save a drink for later."
        } else {
            if (language == "zh") "${recipes.size} 杯候选，等一个合适的举杯时刻。" else "${recipes.size} drinks waiting for the right moment."
        },
        action = {
            FilledTonalButton(onClick = { controller.navigate(Destination.Tonight) }) {
                Text(if (language == "zh") "管理" else "Manage")
            }
        }
    )
    if (recipes.isEmpty()) return
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        recipes.take(6).forEach { recipe -> TonightMiniCard(recipe, language) { controller.openRecipe(recipe) } }
    }
}

@Composable
private fun TonightMiniCard(recipe: Recipe, language: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(176.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DawnPalette.Raised, contentColor = DawnPalette.Paper),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(76.dp).background(Brush.linearGradient(recipe.gradientColors()))) {
            MiniGlassArt(Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(68.dp), recipe.gradientColors())
            Icon(Icons.Rounded.LocalBar, null, tint = Color.White, modifier = Modifier.align(Alignment.TopStart).padding(10.dp).size(18.dp))
        }
        Column(Modifier.padding(11.dp)) {
            Text(recipe.name.value(language), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(recipe.name.secondary(language), style = MaterialTheme.typography.labelSmall, color = DawnPalette.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun RandomAction(primary: Boolean, language: String, modifier: Modifier, onClick: () -> Unit) {
    if (primary) {
        Button(onClick = onClick, modifier = modifier) {
            Icon(Icons.Rounded.AutoAwesome, null)
            Spacer(Modifier.width(7.dp))
            Text(if (language == "zh") "随心一杯" else "Pick one")
        }
    } else {
        FilledTonalButton(onClick = onClick, modifier = modifier) {
            Icon(Icons.Rounded.Groups, null)
            Spacer(Modifier.width(7.dp))
            Text(if (language == "zh") "聚会不重样" else "Party pick")
        }
    }
}
