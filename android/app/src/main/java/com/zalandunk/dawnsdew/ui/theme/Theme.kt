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

private val DawnColorScheme = darkColorScheme(
    primary = Color(0xFFD8B26E),
    onPrimary = Color(0xFF21170A),
    primaryContainer = Color(0xFF4A3920),
    onPrimaryContainer = Color(0xFFF7DEAD),
    secondary = Color(0xFFE98252),
    onSecondary = Color(0xFF2E1208),
    secondaryContainer = Color(0xFF542B22),
    onSecondaryContainer = Color(0xFFFFD9C8),
    tertiary = Color(0xFFBDA3D6),
    onTertiary = Color(0xFF291A36),
    background = Color(0xFF090A0D),
    onBackground = Color(0xFFF5EFE3),
    surface = Color(0xFF121116),
    onSurface = Color(0xFFF5EFE3),
    surfaceVariant = Color(0xFF211C20),
    onSurfaceVariant = Color(0xFFCAC1B8),
    outline = Color(0xFF655A52),
    error = Color(0xFFD66C73)
)

private val baseTypography = Typography()

private val DawnTypography = Typography(
    displayLarge = baseTypography.displayLarge.dawnTitle(56.sp),
    displayMedium = baseTypography.displayMedium.dawnTitle(45.sp),
    headlineLarge = baseTypography.headlineLarge.dawnTitle(34.sp),
    headlineMedium = baseTypography.headlineMedium.dawnTitle(29.sp),
    headlineSmall = baseTypography.headlineSmall.dawnTitle(24.sp),
    titleLarge = baseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
    titleMedium = baseTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = baseTypography.bodyLarge.copy(lineHeight = 25.sp),
    bodyMedium = baseTypography.bodyMedium.copy(lineHeight = 22.sp),
    labelLarge = baseTypography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
)

private fun TextStyle.dawnTitle(size: androidx.compose.ui.unit.TextUnit): TextStyle = copy(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Medium,
    fontSize = size,
    letterSpacing = (-0.3).sp
)

private val DawnShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
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
