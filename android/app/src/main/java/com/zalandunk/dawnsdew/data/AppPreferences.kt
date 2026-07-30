package com.zalandunk.dawnsdew.data

import android.content.Context
import androidx.core.content.edit

class AppPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("dawnsdew.native.preferences", Context.MODE_PRIVATE)

    var language: String
        get() = preferences.getString("language", "zh") ?: "zh"
        set(value) { preferences.edit { putString("language", value) } }

    fun favoriteIds(): Set<String> = preferences.getStringSet("favorites", emptySet()).orEmpty().toSet()
    fun tonightIds(): Set<String> = preferences.getStringSet("tonight", emptySet()).orEmpty().toSet()

    fun saveFavoriteIds(ids: Set<String>) {
        preferences.edit { putStringSet("favorites", ids) }
    }

    fun saveTonightIds(ids: Set<String>) {
        preferences.edit { putStringSet("tonight", ids) }
    }
}