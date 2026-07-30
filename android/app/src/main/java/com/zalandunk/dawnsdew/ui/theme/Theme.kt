package com.zalandunk.dawnsdew.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

object DawnPalette {
    val Ink = Color(0xFF0B0A0D)
    val Surface = Color(0xFF17151A)
    val Raised = Color(0xFF242027)
    val Paper = Color(0xFFFFF8ED)
    val Muted = Color(0xFFD8CEC4)
    val Gold = Color(0xFFF0C978)
    val Coral = Color(0xFFF18B69)
    val Wine = Color(0xFF7D3149)
    val Sage = Color(0xFFA5D0C0)
    val Plum = Color(0xFF4C3558)
}

private val DawnColorScheme = darkColorScheme(
    primary = DawnPalette.Gold,
    onPrimary = Color(0xFF291B05),
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
    outline = Color(0xFF9A8D83),
    outlineVariant = Color(0xFF4C444C),
    error = Color(0xFFFF8C92),
    onError = Color(0xFF3B070C)
)

private val baseTypography = Typography()

private val DawnTypography = Typography(
    displayLarge = baseTypography.displayLarge.dawnTitle(52.sp),
    displayMedium = baseTypography.displayMedium.dawnTitle(42.sp),
    headlineLarge = baseTypography.headlineLarge.dawnTitle(34.sp),
    headlineMedium = baseTypography.headlineMedium.dawnTitle(28.sp),
    headlineSmall = baseTypography.headlineSmall.dawnTitle(23.sp),
    titleLarge = baseTypography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.sp),
    titleMedium = baseTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
    titleSmall = baseTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
    bodyLarge = baseTypography.bodyLarge.copy(lineHeight = 25.sp, letterSpacing = 0.sp),
    bodyMedium = baseTypography.bodyMedium.copy(lineHeight = 22.sp, letterSpacing = 0.sp),
    bodySmall = baseTypography.bodySmall.copy(lineHeight = 19.sp, letterSpacing = 0.sp),
    labelLarge = baseTypography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.sp),
    labelMedium = baseTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
    labelSmall = baseTypography.labelSmall.copy(letterSpacing = 0.sp)
)

private fun TextStyle.dawnTitle(size: androidx.compose.ui.unit.TextUnit): TextStyle = copy(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.SemiBold,
    fontSize = size,
    letterSpacing = 0.sp
)

private val DawnShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(18.dp)
)

@Composable
fun DawnsDewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DawnColorScheme,
        typography = DawnTypography,
        shapes = DawnShapes,
        content = content
    )
}
