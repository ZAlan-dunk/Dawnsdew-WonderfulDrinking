package com.zalandunk.dawnsdew.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorsTest {
    private val vodka = Ingredient("vodka", LocalizedText("伏特加", "Vodka"), "spirit", 40.0, 500.0, "ml")
    private val soda = Ingredient("soda", LocalizedText("苏打水", "Soda"), "mixer", 0.0, 330.0, "ml")
    private val recipe = Recipe(
        id = "test",
        rating = "",
        name = LocalizedText("测试", "Test"),
        origin = "custom",
        base = listOf("vodka"),
        taste = listOf("fresh"),
        difficulty = "easy",
        colors = emptyList(),
        glassCapacity = 300.0,
        ice = "full",
        method = "build",
        ingredients = listOf(
            RecipeIngredient("vodka", 30.0, "ml", null, false, false),
            RecipeIngredient("soda", null, "top", null, true, false)
        ),
        steps = LocalizedList(listOf("混合"), listOf("Mix")),
        description = LocalizedText("", ""),
        sourceText = "",
        aliases = emptyList()
    )

    @Test
    fun estimateCalculatesTopUpAbvAndCoveredCost() {
        val estimate = RecipeCalculator.estimate(
            recipe,
            AppSettings(icedLiquidCapacity = 150.0),
            mapOf(
                "vodka" to PantryEntry(owned = true, price = "100", packSize = "500"),
                "soda" to PantryEntry(owned = true)
            ),
            mapOf("vodka" to vodka, "soda" to soda)
        )

        assertEquals(150.0, estimate.totalMl, 0.001)
        assertEquals(8.0, estimate.abv, 0.001)
        assertEquals(6.0, estimate.cost, 0.001)
        assertEquals(0.2, estimate.costCoverage, 0.001)
        assertTrue(estimate.hasCost)
        assertFalse(estimate.exceedsCapacity)
    }

    @Test
    fun matchIgnoresOptionalAndTreatsZeroStockAsMissing() {
        val recipeWithOptional = recipe.copy(
            ingredients = recipe.ingredients + RecipeIngredient("garnish", 1.0, "piece", null, false, true)
        )
        val match = RecipeCalculator.match(
            recipeWithOptional,
            mapOf(
                "vodka" to PantryEntry(owned = true, stock = "0"),
                "soda" to PantryEntry(owned = true, stock = "100")
            )
        )

        assertEquals(listOf("vodka"), match.missing)
        assertEquals("almost", match.status)
        assertEquals(0.5, match.ratio, 0.001)
    }

    @Test
    fun normalizedSearchMatchesAccentsAndPunctuation() {
        assertEquals("creme de cafe", RecipeCalculator.normalizeSearch("Crème-de Café".replace('-', ' ')))
        assertEquals("dawn s dew", RecipeCalculator.normalizeSearch("Dawn’s Dew"))
    }
}
