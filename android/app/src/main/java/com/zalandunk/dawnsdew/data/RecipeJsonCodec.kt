package com.zalandunk.dawnsdew.data

import org.json.JSONArray
import org.json.JSONObject

object RecipeJsonCodec {
    fun parseIngredient(json: JSONObject) = Ingredient(
        id = json.getString("id"),
        name = LocalizedText(json.optString("zh"), json.optString("en")),
        category = json.optString("category", "other"),
        abv = json.optDouble("abv", 0.0),
        pack = json.optDouble("pack", 0.0),
        unit = json.optString("unit", "ml")
    )

    fun parseRecipe(json: JSONObject) = Recipe(
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
        aliases = json.optJSONArray("aliases").stringList(),
        createdAt = json.optString("createdAt")
    )

    fun recipeToJson(recipe: Recipe) = JSONObject().apply {
        put("id", recipe.id)
        put("rating", recipe.rating)
        put("name", localizedTextToJson(recipe.name))
        put("origin", recipe.origin)
        put("base", JSONArray(recipe.base))
        put("taste", JSONArray(recipe.taste))
        put("difficulty", recipe.difficulty)
        put("colors", JSONArray(recipe.colors))
        put("glassCapacity", recipe.glassCapacity)
        put("ice", recipe.ice)
        put("method", recipe.method)
        put("ingredients", JSONArray().apply {
            recipe.ingredients.forEach { item ->
                put(JSONObject().apply {
                    put("id", item.id)
                    item.amount?.let { put("amount", it) }
                    put("unit", item.unit)
                    item.fillTo?.let { put("fillTo", it) }
                    if (item.topUp) put("topUp", true)
                    if (item.optional) put("optional", true)
                })
            }
        })
        put("steps", JSONObject().put("zh", JSONArray(recipe.steps.zh)).put("en", JSONArray(recipe.steps.en)))
        put("description", localizedTextToJson(recipe.description))
        if (recipe.sourceText.isNotBlank()) put("sourceText", recipe.sourceText)
        if (recipe.aliases.isNotEmpty()) put("aliases", JSONArray(recipe.aliases))
        if (recipe.createdAt.isNotBlank()) put("createdAt", recipe.createdAt)
    }

    private fun parseRecipeIngredient(json: JSONObject) = RecipeIngredient(
        id = json.getString("id"),
        amount = json.doubleOrNull("amount"),
        unit = json.optString("unit", "ml"),
        fillTo = json.doubleOrNull("fillTo"),
        topUp = json.optBoolean("topUp", false),
        optional = json.optBoolean("optional", false)
    )
}

internal fun JSONObject.localizedText(key: String): LocalizedText {
    val value = optJSONObject(key) ?: JSONObject()
    return LocalizedText(value.optString("zh"), value.optString("en"))
}

internal fun JSONObject.localizedList(key: String): LocalizedList {
    val value = optJSONObject(key) ?: JSONObject()
    return LocalizedList(value.optJSONArray("zh").stringList(), value.optJSONArray("en").stringList())
}

internal fun JSONObject.doubleOrNull(key: String): Double? =
    if (has(key) && !isNull(key)) optDouble(key) else null

internal fun JSONArray?.stringList(): List<String> {
    if (this == null) return emptyList()
    return buildList { for (index in 0 until length()) optString(index).takeIf(String::isNotBlank)?.let(::add) }
}

internal fun <T> JSONArray?.mapObjects(transform: (JSONObject) -> T): List<T> {
    if (this == null) return emptyList()
    return buildList { for (index in 0 until length()) optJSONObject(index)?.let { add(transform(it)) } }
}

private fun localizedTextToJson(value: LocalizedText) =
    JSONObject().put("zh", value.zh).put("en", value.en)
