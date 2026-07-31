package com.zalandunk.dawnsdew.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal data class TapParticleBurst(val id: Long, val position: Offset)

internal fun Modifier.tapParticleEmitter(
    enabled: Boolean,
    onTap: (Offset) -> Unit
): Modifier = pointerInput(enabled) {
    if (!enabled) return@pointerInput
    awaitPointerEventScope {
        var down: Offset? = null
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Final)
            val change = event.changes.firstOrNull() ?: continue
            if (change.pressed && !change.previousPressed) down = change.position
            val start = down
            if (start != null && (change.position - start).getDistance() > 24f) down = null
            if (!change.pressed && change.previousPressed) {
                down?.let { onTap(change.position) }
                down = null
            }
        }
    }
}

@Composable
internal fun TapParticleOverlay(burst: TapParticleBurst?, modifier: Modifier = Modifier) {
    val progress = androidx.compose.runtime.remember { Animatable(1f) }
    LaunchedEffect(burst?.id) {
        if (burst != null) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(430, easing = LinearEasing))
        }
    }
    Canvas(modifier) {
        val active = burst ?: return@Canvas
        if (progress.value >= 1f) return@Canvas
        val fade = 1f - progress.value
        repeat(8) { index ->
            val angle = index * (PI.toFloat() / 4f) + 0.18f
            val distance = 7f + progress.value * (24f + (index % 3) * 3f)
            val direction = Offset(cos(angle), sin(angle))
            val center = active.position + direction * distance
            val color = if (index % 2 == 0) Color(0xFFF0C978) else Color(0xFFF18B69)
            drawCircle(color.copy(alpha = fade * 0.82f), radius = 1.7f + fade * 1.2f, center = center)
            if (index % 2 == 0) {
                drawLine(
                    color.copy(alpha = fade * 0.54f),
                    start = center - direction * 4f,
                    end = center + direction * 3f,
                    strokeWidth = 1.2f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
internal fun HeroParticleField(motion: MotionPolicy, modifier: Modifier = Modifier) {
    if (!motion.ambientEnabled) return
    val transition = rememberInfiniteTransition(label = "heroParticles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heroParticlePhase"
    )
    Canvas(modifier) {
        repeat(13) { index ->
            val baseX = ((index * 37) % 101) / 100f * size.width * 0.72f
            val baseY = ((index * 61 + 13) % 101) / 100f * size.height
            val wave = sin((phase * PI.toFloat() * 2f) + index * 0.72f)
            val x = baseX + wave * (5f + index % 3)
            val y = (baseY - phase * (18f + index % 4 * 4f) + size.height) % size.height
            val alpha = 0.18f + ((index % 4) * 0.055f)
            val color = if (index % 3 == 0) Color(0xFFF18B69) else Color(0xFFF0C978)
            drawCircle(color.copy(alpha = alpha), radius = 1.2f + index % 2, center = Offset(x, y))
            if (index % 4 == 0) {
                drawLine(
                    color.copy(alpha = alpha * 0.58f),
                    start = Offset(x - 3f, y),
                    end = Offset(x + 4f, y),
                    strokeWidth = 1f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
