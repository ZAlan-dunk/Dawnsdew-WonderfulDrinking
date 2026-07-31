package com.zalandunk.dawnsdew.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.PlaylistAddCheck
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zalandunk.dawnsdew.data.AppStateStore
import com.zalandunk.dawnsdew.data.CatalogRepository
import com.zalandunk.dawnsdew.ui.theme.DawnsDewTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DawnsDewApp() {
    val context = LocalContext.current
    val catalogResult = remember { runCatching { CatalogRepository(context).load() } }
    val catalog = catalogResult.getOrNull()
    val motion = rememberMotionPolicy()
    if (catalog == null) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("配方数据加载失败", style = MaterialTheme.typography.titleLarge)
                Text(catalogResult.exceptionOrNull()?.message.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    val controller = remember(catalog, context) { AppController(catalog, AppStateStore(context)) }
    DawnsDewTheme(controller.state.settings) {
        DawnsDewThemedApp(controller, motion)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DawnsDewThemedApp(controller: AppController, motion: MotionPolicy) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var particleSequence by remember { mutableLongStateOf(0L) }
    var particleBurst by remember { mutableStateOf<TapParticleBurst?>(null) }
    fun message(value: String) {
        scope.launch { snackbarHostState.showSnackbar(value) }
    }

    var pendingExport by remember { mutableStateOf("") }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter(Charsets.UTF_8).use { writer ->
                requireNotNull(writer) { "Unable to open output" }
                writer.write(pendingExport)
            }
        }.onSuccess {
            message(if (controller.language == "zh") "数据已导出" else "Data exported")
        }.onFailure {
            message(if (controller.language == "zh") "导出失败" else "Export failed")
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            val source = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8).use { reader ->
                requireNotNull(reader) { "Unable to open input" }
                reader.readText()
            }
            controller.importJson(source)
        }.onSuccess {
            message(if (controller.language == "zh") "数据导入成功" else "Data imported")
        }.onFailure {
            message(if (controller.language == "zh") "导入失败：文件格式或版本不正确" else "Import failed: invalid file or version")
        }
    }

    BackHandler(enabled = controller.selectedRecipe != null || controller.destination != Destination.Home) {
        if (controller.selectedRecipe != null) controller.closeRecipe() else controller.navigate(Destination.Home)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .tapParticleEmitter(motion.ambientEnabled) { position ->
                particleSequence += 1
                particleBurst = TapParticleBurst(particleSequence, position)
            }
    ) {
        AmbientBackdrop()
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val expanded = maxWidth >= 840.dp
            val activePrimary = if (controller.destination in primaryDestinations) controller.destination else Destination.More
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = {
                            val pageTitle = when (controller.destination) {
                                Destination.Home -> if (controller.language == "zh") "朝露酒笺" else "Dawn's Dew"
                                Destination.Recipes -> "${controller.destination.label(controller.language)} · ${controller.allRecipes.size}"
                                else -> controller.destination.label(controller.language)
                            }
                            Text(pageTitle, fontWeight = FontWeight.Black)
                        },
                        actions = {
                            IconButton(onClick = controller::switchLanguage) {
                                Icon(
                                    Icons.Rounded.Language,
                                    contentDescription = if (controller.language == "zh") "Switch to English" else "切换到中文",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                bottomBar = {
                    if (!expanded) {
                        NavigationBar(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {
                            primaryDestinations.forEach { item ->
                                NavigationBarItem(
                                    selected = activePrimary == item,
                                    onClick = { controller.navigate(item) },
                                    icon = { Icon(destinationIcon(item), contentDescription = null) },
                                    label = {
                                        Text(item.label(controller.language), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                    alwaysShowLabel = false
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(Modifier.fillMaxSize().padding(innerPadding)) {
                    if (expanded) {
                        NavigationRail(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {
                            Spacer(Modifier.height(12.dp))
                            primaryDestinations.forEach { item ->
                                NavigationRailItem(
                                    selected = activePrimary == item,
                                    onClick = { controller.navigate(item) },
                                    icon = { Icon(destinationIcon(item), contentDescription = null) },
                                    label = { Text(item.label(controller.language)) }
                                )
                            }
                        }
                    }
                    AnimatedContent(
                        targetState = controller.destination,
                        modifier = Modifier.weight(1f),
                        transitionSpec = {
                            (fadeIn(tween(motionDuration(motion, 220))) + scaleIn(initialScale = 0.975f, animationSpec = tween(motionDuration(motion, 260)))) togetherWith
                                (fadeOut(tween(motionDuration(motion, 140))) + scaleOut(targetScale = 1.01f, animationSpec = tween(motionDuration(motion, 150))))
                        },
                        label = "destination"
                    ) { destination ->
                        AppContent(
                            destination = destination,
                            controller = controller,
                            motion = motion,
                            onMessage = ::message,
                            onExport = {
                                pendingExport = controller.exportJson()
                                val date = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
                                exportLauncher.launch("dawnsdew-v0.3.3-backup-$date.json")
                            },
                            onImport = { importLauncher.launch(arrayOf("application/json", "text/plain")) }
                        )
                    }
                }
            }
        }
        TapParticleOverlay(particleBurst, Modifier.fillMaxSize())
    }

    controller.selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            ingredientMap = controller.ingredientMap,
            state = controller.state,
            language = controller.language,
            motion = motion,
            onDismiss = controller::closeRecipe,
            onToggleFavorite = { controller.toggleFavorite(recipe.id) },
            onToggleTonight = {
                val removing = recipe.id in controller.state.tonightIds
                controller.toggleTonight(recipe.id)
                message(
                    if (controller.language == "zh") if (removing) "已移出今夜酒单" else "已加入今夜酒单"
                    else if (removing) "Removed from tonight" else "Added to tonight"
                )
            },
            onAddMissing = {
                controller.addMissingToPantry(recipe)
                message(if (controller.language == "zh") "缺少材料已加入酒柜" else "Missing ingredients added")
            }
        )
    }
}

@Composable
private fun AppContent(
    destination: Destination,
    controller: AppController,
    motion: MotionPolicy,
    onMessage: (String) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    when (destination) {
        Destination.Home -> HomeScreen(controller, motion, onMessage)
        Destination.Recipes, Destination.Tonight, Destination.Favorites, Destination.Recent ->
            RecipeCollectionScreen(controller, motion, destination, onMessage)
        Destination.Pantry -> PantryScreen(controller, motion, onMessage)
        Destination.More -> MoreScreen(controller, onMessage)
        Destination.Custom -> CustomRecipeScreen(controller, motion, onMessage)
        Destination.Data -> DataScreen(controller, onExport, onImport, onMessage)
    }
}

private fun destinationIcon(destination: Destination): ImageVector = when (destination) {
    Destination.Home -> Icons.Rounded.Home
    Destination.Recipes -> Icons.AutoMirrored.Rounded.MenuBook
    Destination.Tonight -> Icons.AutoMirrored.Rounded.PlaylistAddCheck
    Destination.Pantry -> Icons.Rounded.Inventory2
    else -> Icons.Rounded.MoreHoriz
}
