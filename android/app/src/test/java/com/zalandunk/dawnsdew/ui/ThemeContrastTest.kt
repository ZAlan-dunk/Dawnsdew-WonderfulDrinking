package com.zalandunk.dawnsdew.ui

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ThemeContrastTest {
    @Test
    fun primaryReadingPairsMeetWcagAa() {
        assertTrue(contrast(0xFFF0C978, 0xFF291B05) >= 4.5)
        assertTrue(contrast(0xFFFFF8F1, 0xFF171417) >= 4.5)
        assertTrue(contrast(0xFFD0C6C8, 0xFF211E21) >= 4.5)
        assertTrue(contrast(0xFFA5D0C0, 0xFF10271F) >= 4.5)
    }

    @Test
    fun lightThemeReadingAndAccentPairsMeetWcagAa() {
        assertTrue(contrast(0xFF242124, 0xFFF7F6F2) >= 4.5)
        assertTrue(contrast(0xFF625C61, 0xFFFFFBFF) >= 4.5)
        assertTrue(contrast(0xFFFFFFFF, 0xFF745B12) >= 4.5)
        assertTrue(contrast(0xFFFFFFFF, 0xFF9B3F27) >= 4.5)
        assertTrue(contrast(0xFFFFFFFF, 0xFF286657) >= 4.5)
    }

    private fun contrast(foreground: Long, background: Long): Double {
        val first = luminance(foreground)
        val second = luminance(background)
        return (max(first, second) + 0.05) / (min(first, second) + 0.05)
    }

    private fun luminance(color: Long): Double {
        fun channel(shift: Int): Double {
            val value = ((color shr shift) and 0xFF).toDouble() / 255.0
            return if (value <= 0.03928) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(16) + 0.7152 * channel(8) + 0.0722 * channel(0)
    }
}
