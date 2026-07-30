package com.zalandunk.dawnsdew.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zalandunk.dawnsdew.data.AppSettings

object DawnPalette {
    val Ink = Color(0xFF171417)
    val Surface = Color(0xFF211E21)
    val Raised = Color(0xFF2E292E)
    val Paper = Color(0xFFFFF8F1)
    val Muted = Color(0xFFD0C6C8)
    val Gold = Color(0xFFF0C978)
    val Coral = Color(0xFFF18B69)
    val Wine = Color(0xFF7D3149)
    val Sage = Color(0xFFA5D0C0)
    val Plum = Color(0xFF4C3558)
}

private data class AccentColors(
    val primaryLight: Color,
    val onPrimaryLight: Color,
    val primaryDark: Color,
    val onPrimaryDark: Color
)

private fun accentColors(accent: String): AccentColors = when (accent) {
    "coral" -> AccentColors(Color(0xFF9B3F27), Color.White, Color(0xFFFFB59F), Color(0xFF571607))
    "sage" -> AccentColors(Color(0xFF286657), Color.White, Color(0xFFA5D8C8), Color(0xFF07372D))
    else -> AccentColors(Color(0xFF745B12), Color.White, DawnPalette.Gold, Color(0xFF291B05))
}

private fun dawnLightColorScheme(accent: String) = accentColors(accent).let { colors ->
    lightColorScheme(
        primary = colors.primaryLight,
        onPrimary = colors.onPrimaryLight,
        primaryContainer = Color(0xFFFFE7A8),
        onPrimaryContainer = Color(0xFF2A2100),
        secondary = Color(0xFF8B4057),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFD9E2),
        onSecondaryContainer = Color(0xFF3A0A1C),
        tertiary = Color(0xFF316859),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFBDEDDC),
        onTertiaryContainer = Color(0xFF062119),
        background = Color(0xFFF7F6F2),
        onBackground = Color(0xFF242124),
        surface = Color(0xFFFFFBFF),
        onSurface = Color(0xFF242124),
        surfaceVariant = Color(0xFFECE8EA),
        onSurfaceVariant = Color(0xFF625C61),
        outline = Color(0xFF7B7479),
        outlineVariant = Color(0xFFCEC7CC),
        error = Color(0xFFB3261E),
        onError = Color.White
    )
}

private fun dawnDarkColorScheme(accent: String) = accentColors(accent).let { colors ->
    darkColorScheme(
        primary = colors.primaryDark,
        onPrimary = colors.onPrimaryDark,
        primaryContainer = Color(0xFF51401F),
        onPrimaryContainer = Color(0xFFFFE7AE),
        secondary = DawnPalette.Coral,
        onSecondary = Color(0xFF351006),
        secondaryContainer = Color(0xFF653126),
        onSecondaryContainer = Color(0xFFFFDDD2),
        tertiary = DawnPalette.Sage,
        onTertiary = Color(0xFF0B2920),
        tertiaryContainer = Color(0xFF244D42),
        onTertiaryContainer = Color(0xFFC2F0DF),
        background = DawnPalette.Ink,
        onBackground = DawnPalette.Paper,
        surface = DawnPalette.Surface,
        onSurface = DawnPalette.Paper,
        surfaceVariant = DawnPalette.Raised,
        onSurfaceVariant = DawnPalette.Muted,
        outline = Color(0xFFA3979D),
        outlineVariant = Color(0xFF514A50),
        error = Color(0xFFFF8C92),
        onError = Color(0xFF3B070C)
    )
}

private val baseTypography = Typography()

private fun dawnTypography(scale: Float) = Typography(
    displayLarge = baseTypography.displayLarge.dawnTitle(52f * scale),
    displayMedium = baseTypography.displayMedium.dawnTitle(42f * scale),
    headlineLarge = baseTypography.headlineLarge.dawnTitle(34f * scale),
    headlineMedium = baseTypography.headlineMedium.dawnTitle(28f * scale),
    headlineSmall = baseTypography.headlineSmall.dawnTitle(23f * scale),
    titleLarge = baseTypography.titleLarge.scaled(scale, FontWeight.Bold),
    titleMedium = baseTypography.titleMedium.scaled(scale, FontWeight.SemiBold),
    titleSmall = baseTypography.titleSmall.scaled(scale, FontWeight.SemiBold),
    bodyLarge = baseTypography.bodyLarge.scaled(scale, lineHeight = 25f),
    bodyMedium = baseTypography.bodyMedium.scaled(scale, lineHeight = 22f),
    bodySmall = baseTypography.bodySmall.scaled(scale, lineHeight = 19f),
    labelLarge = baseTypography.labelLarge.scaled(scale, FontWeight.Bold),
    labelMedium = baseTypography.labelMedium.scaled(scale, FontWeight.SemiBold),
    labelSmall = baseTypography.labelSmall.scaled(scale)
)

private fun TextStyle.dawnTitle(size: Float): TextStyle = copy(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.SemiBold,
    fontSize = size.sp,
    letterSpacing = 0.sp
)

private fun TextStyle.scaled(
    scale: Float,
    weight: FontWeight? = null,
    lineHeight: Float? = null
): TextStyle = copy(
    fontSize = (fontSize.value * scale).sp,
    lineHeight = lineHeight?.let { (it * scale).sp } ?: this.lineHeight,
    fontWeight = weight ?: fontWeight,
    letterSpacing = 0.sp
)

private val DawnShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun DawnsDewTheme(
    settings: AppSettings = AppSettings(),
    content: @Composable () -> Unit
) {
    val darkTheme = when (settings.themeMode) {
        "dark" -> true
        "system" -> isSystemInDarkTheme()
        else -> false
    }
    MaterialTheme(
        colorScheme = if (darkTheme) dawnDarkColorScheme(settings.accent) else dawnLightColorScheme(settings.accent),
        typography = dawnTypography(settings.fontScale),
        shapes = DawnShapes,
        content = content
    )
}
