package com.zalandunk.dawnsdew.data

import android.content.Context
import org.json.JSONObject

class CatalogRepository(private val context: Context) {
    fun load(): Catalog {
        val source = context.assets.open("catalog.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val root = JSONObject(source)
        val ingredients = root.getJSONArray("ingredients").mapObjects(RecipeJsonCodec::parseIngredient)
        val recipes = root.getJSONArray("recipes").mapObjects(RecipeJsonCodec::parseRecipe)
        require(ingredients.isNotEmpty()) { "Ingredient catalog is empty" }
        require(recipes.isNotEmpty()) { "Recipe catalog is empty" }
        return Catalog(ingredients, recipes)
    }

}
