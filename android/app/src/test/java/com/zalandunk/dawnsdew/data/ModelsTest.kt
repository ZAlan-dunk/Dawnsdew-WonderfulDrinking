package com.zalandunk.dawnsdew.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ModelsTest {
    @Test
    fun localizedTextFallsBackToAvailableLanguage() {
        val chineseOnly = LocalizedText(zh = "朝露", en = "")
        val englishOnly = LocalizedText(zh = "", en = "Dawn")

        assertEquals("朝露", chineseOnly.value("en"))
        assertEquals("Dawn", englishOnly.value("zh"))
    }

    @Test
    fun localizedTextReturnsSecondaryLanguage() {
        val text = LocalizedText(zh = "今夜酒单", en = "Tonight's Menu")

        assertEquals("Tonight's Menu", text.secondary("zh"))
        assertEquals("今夜酒单", text.secondary("en"))
    }

    @Test
    fun localizedListFallsBackWhenRequestedListIsEmpty() {
        val list = LocalizedList(zh = listOf("加冰"), en = emptyList())

        assertEquals(listOf("加冰"), list.value("en"))
    }
}
