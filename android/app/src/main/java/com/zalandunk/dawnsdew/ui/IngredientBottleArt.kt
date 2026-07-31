package com.zalandunk.dawnsdew.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zalandunk.dawnsdew.data.BottleShape
import com.zalandunk.dawnsdew.data.IngredientProfile

@Composable
internal fun IngredientBottleArt(
    profile: IngredientProfile,
    modifier: Modifier = Modifier
) {
    val accent = Color(profile.accent)
    val outline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val labelColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
    val shadowColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val bottle = bottlePath(profile.shape, w, h)
            drawOval(
                color = shadowColor,
                topLeft = Offset(w * 0.18f, h * 0.89f),
                size = Size(w * 0.64f, h * 0.08f)
            )
            drawPath(
                path = bottle,
                brush = Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.72f), accent, accent.copy(alpha = 0.74f)),
                    startY = h * 0.10f,
                    endY = h * 0.92f
                )
            )
            drawPath(bottle, outline, style = Stroke(width = w * 0.035f, cap = StrokeCap.Round))
            drawRoundRect(
                color = labelColor,
                topLeft = Offset(w * 0.25f, h * 0.52f),
                size = Size(w * 0.50f, h * 0.22f),
                cornerRadius = CornerRadius(w * 0.07f)
            )
            drawLine(
                color = Color.White.copy(alpha = 0.32f),
                start = Offset(w * 0.31f, h * 0.34f),
                end = Offset(w * 0.31f, h * 0.83f),
                strokeWidth = w * 0.035f,
                cap = StrokeCap.Round
            )
        }
        Text(
            profile.monogram,
            modifier = Modifier.offset(y = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black
        )
    }
}

private fun bottlePath(shape: BottleShape, w: Float, h: Float): Path = Path().apply {
    when (shape) {
        BottleShape.ROUND_SHOULDER -> {
            moveTo(w * 0.40f, h * 0.08f)
            lineTo(w * 0.60f, h * 0.08f)
            lineTo(w * 0.61f, h * 0.24f)
            quadraticTo(w * 0.78f, h * 0.30f, w * 0.82f, h * 0.44f)
            lineTo(w * 0.77f, h * 0.88f)
            quadraticTo(w * 0.50f, h * 0.95f, w * 0.23f, h * 0.88f)
            lineTo(w * 0.18f, h * 0.44f)
            quadraticTo(w * 0.22f, h * 0.30f, w * 0.39f, h * 0.24f)
            close()
        }
        BottleShape.SQUARE -> {
            moveTo(w * 0.38f, h * 0.08f)
            lineTo(w * 0.62f, h * 0.08f)
            lineTo(w * 0.63f, h * 0.27f)
            lineTo(w * 0.78f, h * 0.34f)
            lineTo(w * 0.78f, h * 0.91f)
            lineTo(w * 0.22f, h * 0.91f)
            lineTo(w * 0.22f, h * 0.34f)
            lineTo(w * 0.37f, h * 0.27f)
            close()
        }
        BottleShape.TALL -> {
            moveTo(w * 0.39f, h * 0.05f)
            lineTo(w * 0.61f, h * 0.05f)
            lineTo(w * 0.62f, h * 0.31f)
            quadraticTo(w * 0.73f, h * 0.35f, w * 0.75f, h * 0.43f)
            lineTo(w * 0.73f, h * 0.92f)
            lineTo(w * 0.27f, h * 0.92f)
            lineTo(w * 0.25f, h * 0.43f)
            quadraticTo(w * 0.27f, h * 0.35f, w * 0.38f, h * 0.31f)
            close()
        }
        BottleShape.APOTHECARY -> {
            moveTo(w * 0.37f, h * 0.08f)
            lineTo(w * 0.63f, h * 0.08f)
            lineTo(w * 0.62f, h * 0.25f)
            quadraticTo(w * 0.82f, h * 0.33f, w * 0.80f, h * 0.52f)
            lineTo(w * 0.74f, h * 0.90f)
            lineTo(w * 0.26f, h * 0.90f)
            lineTo(w * 0.20f, h * 0.52f)
            quadraticTo(w * 0.18f, h * 0.33f, w * 0.38f, h * 0.25f)
            close()
        }
        BottleShape.CHAMPAGNE -> {
            moveTo(w * 0.42f, h * 0.04f)
            lineTo(w * 0.58f, h * 0.04f)
            lineTo(w * 0.60f, h * 0.30f)
            quadraticTo(w * 0.70f, h * 0.38f, w * 0.73f, h * 0.51f)
            lineTo(w * 0.78f, h * 0.90f)
            quadraticTo(w * 0.50f, h * 0.96f, w * 0.22f, h * 0.90f)
            lineTo(w * 0.27f, h * 0.51f)
            quadraticTo(w * 0.30f, h * 0.38f, w * 0.40f, h * 0.30f)
            close()
        }
        BottleShape.BITTERS -> {
            moveTo(w * 0.40f, h * 0.03f)
            lineTo(w * 0.60f, h * 0.03f)
            lineTo(w * 0.62f, h * 0.39f)
            lineTo(w * 0.75f, h * 0.47f)
            lineTo(w * 0.76f, h * 0.90f)
            lineTo(w * 0.24f, h * 0.90f)
            lineTo(w * 0.25f, h * 0.47f)
            lineTo(w * 0.38f, h * 0.39f)
            close()
        }
        BottleShape.DECANTER -> {
            moveTo(w * 0.39f, h * 0.09f)
            lineTo(w * 0.61f, h * 0.09f)
            lineTo(w * 0.63f, h * 0.27f)
            lineTo(w * 0.84f, h * 0.48f)
            lineTo(w * 0.75f, h * 0.90f)
            lineTo(w * 0.25f, h * 0.90f)
            lineTo(w * 0.16f, h * 0.48f)
            lineTo(w * 0.37f, h * 0.27f)
            close()
        }
        BottleShape.STANDARD -> {
            moveTo(w * 0.39f, h * 0.06f)
            lineTo(w * 0.61f, h * 0.06f)
            lineTo(w * 0.63f, h * 0.27f)
            quadraticTo(w * 0.77f, h * 0.34f, w * 0.78f, h * 0.44f)
            lineTo(w * 0.76f, h * 0.91f)
            lineTo(w * 0.24f, h * 0.91f)
            lineTo(w * 0.22f, h * 0.44f)
            quadraticTo(w * 0.23f, h * 0.34f, w * 0.37f, h * 0.27f)
            close()
        }
    }
}
