package com.zalandunk.dawnsdew.data

import java.text.Normalizer
import java.util.Locale

data class EstimatedIngredient(val ingredient: RecipeIngredient, val estimatedMl: Double)

data class RecipeEstimate(
    val capacity: Double,
    val totalMl: Double,
    val ingredients: List<EstimatedIngredient>,
    val abv: Double,
    val cost: Double,
    val costCoverage: Double,
    val hasCost: Boolean,
    val exceedsCapacity: Boolean
)

data class RecipeMatch(
    val required: List<String>,
    val missing: List<String>,
    val matched: Int
) {
    val ratio: Double get() = if (required.isEmpty()) 1.0 else matched.toDouble() / required.size
    val status: String get() = when (missing.size) {
        0 -> "makeable"
        1 -> "almost"
        else -> "missing"
    }
}

object RecipeCalculator {
    fun estimate(
        recipe: Recipe,
        settings: AppSettings,
        pantry: Map<String, PantryEntry>,
        ingredientMap: Map<String, Ingredient>
    ): RecipeEstimate {
        val capacity = if (recipe.ice == "full") settings.icedLiquidCapacity else recipe.glassCapacity.takeIf { it > 0 }
            ?: settings.glassCapacity
        var total = 0.0
        val amounts = recipe.ingredients.map { item ->
            val volume = when {
                item.unit == "ml" -> (item.amount ?: 0.0).coerceAtLeast(0.0)
                item.unit == "fill" || item.fillTo != null ->
                    (capacity * (item.fillTo ?: 0.0).coerceIn(0.0, 1.0) - total).coerceAtLeast(0.0)
                item.unit == "top" || item.topUp -> (capacity - total).coerceAtLeast(0.0)
                else -> 0.0
            }
            total += volume
            EstimatedIngredient(item, volume)
        }
        var pureAlcohol = 0.0
        var cost = 0.0
        var pricedVolume = 0.0
        var neededVolume = 0.0
        amounts.forEach { estimated ->
            val entry = pantry[estimated.ingredient.id]
            val catalogAbv = ingredientMap[estimated.ingredient.id]?.abv ?: 0.0
            val abv = entry?.abv?.toDoubleOrNull() ?: catalogAbv
            pureAlcohol += estimated.estimatedMl * abv / 100.0
            if (estimated.estimatedMl > 0.0) {
                neededVolume += estimated.estimatedMl
                val price = entry?.price?.toDoubleOrNull()
                val packSize = entry?.packSize?.toDoubleOrNull()
                if (price != null && price >= 0.0 && packSize != null && packSize > 0.0) {
                    cost += price / packSize * estimated.estimatedMl
                    pricedVolume += estimated.estimatedMl
                }
            }
        }
        return RecipeEstimate(
            capacity = capacity,
            totalMl = total,
            ingredients = amounts,
            abv = if (total > 0.0) pureAlcohol / total * 100.0 else 0.0,
            cost = cost,
            costCoverage = if (neededVolume > 0.0) pricedVolume / neededVolume else 0.0,
            hasCost = pricedVolume > 0.0,
            exceedsCapacity = total > capacity + 0.01
        )
    }

    fun isOwned(entry: PantryEntry?): Boolean {
        if (entry?.owned != true) return false
        return entry.stock.isBlank() || (entry.stock.toDoubleOrNull() ?: 0.0) > 0.0
    }

    fun match(recipe: Recipe, pantry: Map<String, PantryEntry>): RecipeMatch {
        val required = recipe.ingredients
            .filter { !it.optional && it.id != "ice" }
            .map { it.id }
            .distinct()
        val missing = required.filter { !isOwned(pantry[it]) }
        return RecipeMatch(required, missing, required.size - missing.size)
    }

    fun searchableText(recipe: Recipe, ingredientMap: Map<String, Ingredient>): String = normalizeSearch(
        buildList {
            add(recipe.id)
            add(recipe.name.zh)
            add(recipe.name.en)
            addAll(recipe.aliases)
            addAll(recipe.taste)
            recipe.base.forEach { id -> ingredientMap[id]?.let { add("${it.name.zh} ${it.name.en}") } ?: add(id) }
            recipe.ingredients.forEach { item -> ingredientMap[item.id]?.let { add("${it.name.zh} ${it.name.en}") } ?: add(item.id) }
            add(recipe.description.zh)
            add(recipe.description.en)
        }.joinToString(" ")
    )

    fun normalizeSearch(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase(Locale.ROOT)
        .replace(Regex("[’'·()（）&+]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}
