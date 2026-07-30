package com.zalandunk.dawnsdew.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class CatalogRepository(private val context: Context) {
    fun load(): Catalog {
        val source = context.assets.open("catalog.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val root = JSONObject(source)
        val ingredients = root.getJSONArray("ingredients").mapObjects(::parseIngredient)
        val recipes = root.getJSONArray("recipes").mapObjects(::parseRecipe)
        require(ingredients.isNotEmpty()) { "Ingredient catalog is empty" }
        require(recipes.isNotEmpty()) { "Recipe catalog is empty" }
        return Catalog(ingredients, recipes)
    }

    private fun parseIngredient(json: JSONObject) = Ingredient(
        id = json.getString("id"),
        name = LocalizedText(json.getString("zh"), json.getString("en")),
        category = json.optString("category", "other"),
        abv = json.optDouble("abv", 0.0),
        pack = json.optDouble("pack", 0.0),
        unit = json.optString("unit", "ml")
    )

    private fun parseRecipe(json: JSONObject) = Recipe(
        id = json.getString("id"),
        rating = json.optString("rating"),
        name = json.localizedText("name"),
        origin = json.optString("origin", "classic"),
        base = json.optJSONArray("base").stringList(),
        taste = json.optJSONArray("taste").stringList(),
        difficulty = json.optString("difficulty", "easy"),
        colors = json.optJSONArray("colors").stringList(),
        glassCapacity = json.optDouble("glassCapacity", 300.0),
        ice = json.optString("ice"),
        method = json.optString("method", "build"),
        ingredients = json.optJSONArray("ingredients").mapObjects(::parseRecipeIngredient),
        steps = json.localizedList("steps"),
        description = json.localizedText("description"),
        sourceText = json.optString("sourceText"),
        aliases = json.optJSONArray("aliases").stringList()
    )

    private fun parseRecipeIngredient(json: JSONObject) = RecipeIngredient(
        id = json.getString("id"),
        amount = json.doubleOrNull("amount"),
        unit = json.optString("unit", "ml"),
        fillTo = json.doubleOrNull("fillTo"),
        topUp = json.optBoolean("topUp", false),
        optional = json.optBoolean("optional", false)
    )
}

private fun JSONObject.localizedText(key: String): LocalizedText {
    val value = optJSONObject(key) ?: JSONObject()
    return LocalizedText(value.optString("zh"), value.optString("en"))
}

private fun JSONObject.localizedList(key: String): LocalizedList {
    val value = optJSONObject(key) ?: JSONObject()
    return LocalizedList(value.optJSONArray("zh").stringList(), value.optJSONArray("en").stringList())
}

private fun JSONObject.doubleOrNull(key: String): Double? =
    if (has(key) && !isNull(key)) getDouble(key) else null

private fun JSONArray?.stringList(): List<String> {
    if (this == null) return emptyList()
    return buildList { for (index in 0 until length()) add(optString(index)) }
}

private fun <T> JSONArray?.mapObjects(transform: (JSONObject) -> T): List<T> {
    if (this == null) return emptyList()
    return buildList { for (index in 0 until length()) add(transform(getJSONObject(index))) }
}