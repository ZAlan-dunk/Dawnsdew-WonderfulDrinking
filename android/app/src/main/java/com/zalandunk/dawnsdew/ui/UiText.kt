package com.zalandunk.dawnsdew.ui

import com.zalandunk.dawnsdew.data.RecipeIngredient
import java.util.Locale
import kotlin.math.roundToInt

internal fun originLabel(origin: String, language: String): String = when (origin) {
    "personal" -> if (language == "zh") "私人酒笺" else "Personal collection"
    "convenience" -> if (language == "zh") "灵感特调" else "Inspired mix"
    "custom" -> if (language == "zh") "我的创作" else "My creation"
    else -> if (language == "zh") "经典配方" else "Classic"
}

internal fun difficultyLabel(value: String, language: String): String = when (value) {
    "hard" -> if (language == "zh") "进阶" else "Advanced"
    "medium" -> if (language == "zh") "适中" else "Medium"
    else -> if (language == "zh") "轻松" else "Easy"
}

internal fun tasteLabel(value: String, language: String): String {
    val zh = mapOf(
        "sweet" to "甜润", "creamy" to "绵柔", "fresh" to "清新", "citrus" to "柑橘",
        "fruity" to "果香", "sparkling" to "气泡", "strong" to "浓烈", "coconut" to "椰香",
        "bitter" to "微苦", "herbal" to "草本", "dry" to "干爽", "spicy" to "辛香", "coffee" to "咖啡"
    )
    return if (language == "zh") zh[value] ?: value else value.replaceFirstChar { it.uppercase() }
}

internal fun categoryLabel(value: String, language: String): String = if (language == "zh") {
    mapOf(
        "spirit" to "基酒", "liqueur" to "利口酒", "wine" to "葡萄酒与气泡酒", "beer" to "啤酒",
        "mixer" to "饮料与苏打", "juice" to "果汁", "dairy" to "乳制品", "syrup" to "糖浆",
        "fruit" to "水果与装饰", "other" to "其他"
    )[value] ?: "其他"
} else {
    value.replaceFirstChar { it.uppercase() }
}

internal fun matchLabel(status: String, missingCount: Int, language: String): String = when (status) {
    "makeable" -> if (language == "zh") "此刻可调" else "Ready to make"
    "almost" -> if (language == "zh") "还差 1 种" else "Missing 1"
    else -> if (language == "zh") "还差 $missingCount 种" else "Missing $missingCount"
}

internal fun amountLabel(item: RecipeIngredient, language: String): String = when {
    item.topUp || item.unit == "top" -> if (language == "zh") "补满" else "Top up"
    item.fillTo != null || item.unit == "fill" -> if (language == "zh") {
        "至 ${((item.fillTo ?: 0.0) * 10).roundToInt()} 分满"
    } else {
        "Fill to ${formatNumber((item.fillTo ?: 0.0) * 10)}/10"
    }
    item.unit == "piece" -> if (language == "zh") {
        "${formatNumber(item.amount ?: 0.0)} 份"
    } else {
        "${formatNumber(item.amount ?: 0.0)} pcs"
    }
    else -> "${formatNumber(item.amount ?: 0.0)} ml"
}

internal fun formatNumber(value: Double, digits: Int = 1): String = if (value % 1.0 == 0.0) {
    value.roundToInt().toString()
} else {
    String.format(Locale.ROOT, "%.${digits}f", value)
}
