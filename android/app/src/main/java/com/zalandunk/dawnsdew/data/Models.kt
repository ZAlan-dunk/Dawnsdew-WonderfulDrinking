package com.zalandunk.dawnsdew.data

data class LocalizedText(
    val zh: String,
    val en: String
) {
    fun value(language: String): String = if (language == "en") en.ifBlank { zh } else zh.ifBlank { en }
    fun secondary(language: String): String = if (language == "en") zh else en
}

data class Ingredient(
    val id: String,
    val name: LocalizedText,
    val category: String,
    val abv: Double,
    val pack: Double,
    val unit: String
)

data class RecipeIngredient(
    val id: String,
    val amount: Double?,
    val unit: String,
    val fillTo: Double?,
    val topUp: Boolean,
    val optional: Boolean
)

data class Recipe(
    val id: String,
    val rating: String,
    val name: LocalizedText,
    val origin: String,
    val base: List<String>,
    val taste: List<String>,
    val difficulty: String,
    val colors: List<String>,
    val glassCapacity: Double,
    val ice: String,
    val method: String,
    val ingredients: List<RecipeIngredient>,
    val steps: LocalizedList,
    val description: LocalizedText,
    val sourceText: String,
    val aliases: List<String>,
    val createdAt: String = ""
)

data class LocalizedList(
    val zh: List<String>,
    val en: List<String>
) {
    fun value(language: String): List<String> = if (language == "en") en.ifEmpty { zh } else zh.ifEmpty { en }
}

data class Catalog(
    val ingredients: List<Ingredient>,
    val recipes: List<Recipe>
)

data class AppSettings(
    val language: String = "zh",
    val glassCapacity: Double = 300.0,
    val icedLiquidCapacity: Double = 150.0,
    val currency: String = "¥",
    val themeMode: String = "light",
    val accent: String = "gold",
    val fontScale: Float = 1.0f
)

data class PantryEntry(
    val owned: Boolean = false,
    val stock: String = "",
    val price: String = "",
    val packSize: String = "",
    val abv: String = ""
)

data class AppState(
    val settings: AppSettings = AppSettings(),
    val pantry: Map<String, PantryEntry> = emptyMap(),
    val favoriteIds: Set<String> = emptySet(),
    val customRecipes: List<Recipe> = emptyList(),
    val tonightIds: List<String> = emptyList(),
    val recentIds: List<String> = emptyList(),
    val partyHistory: List<String> = emptyList(),
    val savedAt: String = ""
)
