package com.zalandunk.dawnsdew.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zalandunk.dawnsdew.data.AppSettings
import com.zalandunk.dawnsdew.data.AppState
import com.zalandunk.dawnsdew.data.AppStateStore
import com.zalandunk.dawnsdew.data.Catalog
import com.zalandunk.dawnsdew.data.PantryEntry
import com.zalandunk.dawnsdew.data.Recipe
import com.zalandunk.dawnsdew.data.RecipeCalculator

enum class Destination(val zh: String, val en: String) {
    Home("首页", "Home"),
    Recipes("配方", "Recipes"),
    Tonight("今夜", "Tonight"),
    Pantry("酒柜", "Pantry"),
    More("更多", "More"),
    Favorites("收藏", "Favorites"),
    Recent("最近查看", "Recently viewed"),
    Custom("自定义配方", "Custom recipes"),
    Data("数据管理", "Data")
}

val primaryDestinations = listOf(
    Destination.Home,
    Destination.Recipes,
    Destination.Tonight,
    Destination.Pantry,
    Destination.More
)

@Stable
class AppController(
    val catalog: Catalog,
    private val store: AppStateStore
) {
    var state by mutableStateOf(store.load())
        private set
    var destination by mutableStateOf(Destination.Home)
    var selectedRecipe by mutableStateOf<Recipe?>(null)
        private set

    val language: String get() = state.settings.language
    val allRecipes: List<Recipe> get() = catalog.recipes + state.customRecipes
    val ingredientMap = catalog.ingredients.associateBy { it.id }
    val dailyRecipe: Recipe?
        get() {
            val recipes = allRecipes
            if (recipes.isEmpty()) return null
            val day = (System.currentTimeMillis() / 86_400_000L).toInt()
            return recipes[Math.floorMod(day, recipes.size)]
        }

    fun navigate(target: Destination) {
        destination = target
    }

    fun switchLanguage() {
        persist(state.copy(settings = state.settings.copy(language = if (language == "zh") "en" else "zh")))
    }

    fun openRecipe(recipe: Recipe) {
        selectedRecipe = recipe
        val recent = listOf(recipe.id) + state.recentIds.filterNot { it == recipe.id }
        persist(state.copy(recentIds = recent.take(8)))
    }

    fun closeRecipe() {
        selectedRecipe = null
    }

    fun toggleFavorite(id: String) {
        val favorites = if (id in state.favoriteIds) state.favoriteIds - id else state.favoriteIds + id
        persist(state.copy(favoriteIds = favorites))
    }

    fun toggleTonight(id: String) {
        val tonight = if (id in state.tonightIds) {
            state.tonightIds.filterNot { it == id }
        } else {
            (state.tonightIds + id).takeLast(24)
        }
        persist(state.copy(tonightIds = tonight))
    }

    fun clearTonight() {
        persist(state.copy(tonightIds = emptyList()))
    }

    fun updatePantry(id: String, entry: PantryEntry) {
        persist(state.copy(pantry = state.pantry + (id to entry)))
    }

    fun selectCommonIngredients() {
        val common = setOf(
            "white_rum", "vodka", "brandy", "baileys", "blue_curacao", "cola", "sprite",
            "soda_water", "orange_juice", "grape_juice", "lemon_juice", "lime_juice"
        )
        val pantry = state.pantry.toMutableMap()
        common.forEach { id -> pantry[id] = (pantry[id] ?: PantryEntry()).copy(owned = true) }
        persist(state.copy(pantry = pantry))
    }

    fun addMissingToPantry(recipe: Recipe) {
        val pantry = state.pantry.toMutableMap()
        RecipeCalculator.match(recipe, state.pantry).missing.forEach { id ->
            pantry[id] = (pantry[id] ?: PantryEntry()).copy(owned = true)
        }
        persist(state.copy(pantry = pantry))
    }

    fun saveSettings(settings: AppSettings) {
        persist(state.copy(settings = settings))
    }

    fun addCustomRecipe(recipe: Recipe) {
        persist(state.copy(customRecipes = listOf(recipe) + state.customRecipes.filterNot { it.id == recipe.id }))
    }

    fun deleteCustomRecipe(id: String) {
        persist(
            state.copy(
                customRecipes = state.customRecipes.filterNot { it.id == id },
                favoriteIds = state.favoriteIds - id,
                tonightIds = state.tonightIds.filterNot { it == id },
                recentIds = state.recentIds.filterNot { it == id },
                partyHistory = state.partyHistory.filterNot { it == id }
            )
        )
    }

    fun randomRecipe(party: Boolean): Recipe? {
        val recipes = allRecipes
        if (recipes.isEmpty()) return null
        var candidates = if (party) {
            recipes.filter {
                RecipeCalculator.match(it, state.pantry).status == "makeable" && it.id !in state.partyHistory
            }
        } else {
            recipes
        }
        if (party && candidates.isEmpty()) {
            candidates = recipes.filter { RecipeCalculator.match(it, state.pantry).status == "makeable" }
        }
        if (party && candidates.isEmpty()) {
            candidates = recipes.filter { it.id !in state.partyHistory }
        }
        if (candidates.isEmpty()) candidates = recipes
        val selected = candidates.random()
        if (party) {
            persist(state.copy(partyHistory = (listOf(selected.id) + state.partyHistory.filterNot { it == selected.id }).take(12)))
        }
        openRecipe(selected)
        return selected
    }

    fun exportJson(): String = store.exportJson(state)

    fun importJson(source: String) {
        state = store.importJson(source)
        selectedRecipe = null
        destination = Destination.Home
    }

    fun reset() {
        state = store.reset()
        selectedRecipe = null
        destination = Destination.Home
    }

    private fun persist(next: AppState) {
        state = store.save(next)
    }
}

fun Destination.label(language: String): String = if (language == "zh") zh else en
