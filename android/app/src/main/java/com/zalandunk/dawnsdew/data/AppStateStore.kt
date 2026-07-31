package com.zalandunk.dawnsdew.data

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AppStateStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun load(): AppState {
        val stored = preferences.getString(STATE_KEY, null)
            ?: LEGACY_STATE_KEYS.firstNotNullOfOrNull { preferences.getString(it, null) }
        if (!stored.isNullOrBlank()) {
            runCatching {
                val migrated = parseState(JSONObject(stored))
                return save(migrated)
            }
        }
        val migrated = AppState(
            settings = AppSettings(language = preferences.getString("language", "zh") ?: "zh"),
            favoriteIds = preferences.getStringSet("favorites", emptySet()).orEmpty().toSet(),
            tonightIds = preferences.getStringSet("tonight", emptySet()).orEmpty().toList()
        )
        save(migrated)
        return migrated
    }

    fun save(state: AppState): AppState {
        val normalized = normalize(state).copy(savedAt = isoNow())
        preferences.edit { putString(STATE_KEY, stateToJson(normalized).toString()) }
        return normalized
    }

    fun importJson(source: String): AppState {
        val root = JSONObject(source)
        val version = root.optString("schemaVersion")
        require(version.isBlank() || version in SUPPORTED_IMPORT_VERSIONS) { "Unsupported schema version" }
        val candidate = root.optJSONObject("state") ?: root
        val imported = parseState(candidate)
        require(imported.customRecipes.all(::isValidCustomRecipe)) { "Invalid custom recipe" }
        preferences.getString(STATE_KEY, null)?.let { current ->
            preferences.edit { putString(BACKUP_KEY, current) }
        }
        return save(imported)
    }

    fun exportJson(state: AppState): String = JSONObject().apply {
        put("app", "Dawn's Dew / 朝露酒笺")
        put("schemaVersion", WEB_COMPAT_SCHEMA_VERSION)
        put("exportedAt", isoNow())
        put("state", stateToJson(normalize(state)))
    }.toString(2)

    fun reset(): AppState {
        preferences.getString(STATE_KEY, null)?.let { current ->
            preferences.edit { putString(BACKUP_KEY, current) }
        }
        val state = AppState()
        return save(state)
    }

    private fun parseState(json: JSONObject): AppState {
        val settingsJson = json.optJSONObject("settings") ?: JSONObject()
        val pantryJson = json.optJSONObject("pantry") ?: JSONObject()
        val pantry = buildMap {
            pantryJson.keys().forEach { id ->
                val item = pantryJson.optJSONObject(id) ?: JSONObject()
                put(
                    id,
                    PantryEntry(
                        owned = item.optBoolean("owned", false),
                        stock = item.stringValue("stock"),
                        price = item.stringValue("price"),
                        packSize = item.stringValue("packSize"),
                        abv = item.stringValue("abv")
                    )
                )
            }
        }
        return normalize(
            AppState(
                settings = AppSettings(
                    language = settingsJson.optString("lang", settingsJson.optString("language", "zh")),
                    glassCapacity = settingsJson.optDouble("glassCapacity", 300.0),
                    icedLiquidCapacity = settingsJson.optDouble("icedLiquidCapacity", 150.0),
                    currency = settingsJson.optString("currency", "¥"),
                    themeMode = settingsJson.optString("themeMode", "light"),
                    accent = settingsJson.optString("accent", "gold"),
                    fontScale = settingsJson.optDouble("fontScale", 1.0).toFloat()
                ),
                pantry = pantry,
                favoriteIds = (json.optJSONArray("favorites") ?: json.optJSONArray("favoriteIds")).stringList().toSet(),
                customRecipes = json.optJSONArray("customRecipes").mapObjects(RecipeJsonCodec::parseRecipe),
                tonightIds = (json.optJSONArray("tonightMenu") ?: json.optJSONArray("tonightIds")).stringList(),
                recentIds = (json.optJSONArray("recent") ?: json.optJSONArray("recentIds")).stringList(),
                partyHistory = json.optJSONArray("partyHistory").stringList(),
                savedAt = json.optString("savedAt")
            )
        )
    }

    private fun stateToJson(state: AppState) = JSONObject().apply {
        put("version", SCHEMA_VERSION)
        put("savedAt", state.savedAt)
        put("settings", JSONObject().apply {
            put("lang", state.settings.language)
            put("glassCapacity", state.settings.glassCapacity)
            put("icedLiquidCapacity", state.settings.icedLiquidCapacity)
            put("currency", state.settings.currency)
            put("themeMode", state.settings.themeMode)
            put("accent", state.settings.accent)
            put("fontScale", state.settings.fontScale.toDouble())
        })
        put("pantry", JSONObject().apply {
            state.pantry.forEach { (id, item) ->
                put(id, JSONObject().apply {
                    put("owned", item.owned)
                    put("stock", item.stock)
                    put("price", item.price)
                    put("packSize", item.packSize)
                    put("abv", item.abv)
                })
            }
        })
        put("favorites", JSONArray(state.favoriteIds.toList()))
        put("customRecipes", JSONArray().apply { state.customRecipes.forEach { put(RecipeJsonCodec.recipeToJson(it)) } })
        put("tonightMenu", JSONArray(state.tonightIds))
        put("recent", JSONArray(state.recentIds))
        put("partyHistory", JSONArray(state.partyHistory))
    }

    private fun normalize(state: AppState): AppState = state.copy(
        settings = state.settings.copy(
            language = if (state.settings.language == "en") "en" else "zh",
            glassCapacity = state.settings.glassCapacity.coerceAtLeast(30.0),
            icedLiquidCapacity = state.settings.icedLiquidCapacity.coerceAtLeast(30.0),
            currency = state.settings.currency.ifBlank { "¥" }.take(4),
            themeMode = state.settings.themeMode.takeIf { it in setOf("light", "dark", "system") } ?: "light",
            accent = state.settings.accent.takeIf { it in setOf("gold", "coral", "sage") } ?: "gold",
            fontScale = state.settings.fontScale.coerceIn(0.9f, 1.3f)
        ),
        pantry = state.pantry.filterKeys(String::isNotBlank),
        favoriteIds = state.favoriteIds.filter(String::isNotBlank).toSet(),
        customRecipes = state.customRecipes.filter(::isValidCustomRecipe),
        tonightIds = state.tonightIds.filter(String::isNotBlank).distinct().takeLast(24),
        recentIds = state.recentIds.filter(String::isNotBlank).distinct().take(8),
        partyHistory = state.partyHistory.filter(String::isNotBlank).distinct().take(12)
    )

    private fun isValidCustomRecipe(recipe: Recipe): Boolean =
        recipe.id.isNotBlank() && recipe.name.zh.isNotBlank() && recipe.name.en.isNotBlank() &&
            recipe.ingredients.isNotEmpty() && recipe.steps.zh.isNotEmpty() && recipe.steps.en.isNotEmpty()

    companion object {
        const val SCHEMA_VERSION = "0.3.3"
        private const val WEB_COMPAT_SCHEMA_VERSION = "0.2"
        private const val PREFERENCES = "dawnsdew.native.preferences"
        private const val STATE_KEY = "state.v0.3.3"
        private const val BACKUP_KEY = "state.backup"
        private val LEGACY_STATE_KEYS = listOf("state.v0.3.2", "state.v0.3.1")
        private val SUPPORTED_IMPORT_VERSIONS = setOf("0.0.1", "0.0.2", "0.2", "0.3", "0.3.1", "0.3.2", SCHEMA_VERSION)
    }
}

private fun JSONObject.stringValue(key: String): String = when (val value = opt(key)) {
    null, JSONObject.NULL -> ""
    else -> value.toString()
}

private fun isoNow(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}.format(Date())
