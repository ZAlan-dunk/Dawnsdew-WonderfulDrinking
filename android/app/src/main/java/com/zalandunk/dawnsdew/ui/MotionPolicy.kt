package com.zalandunk.dawnsdew.ui

import android.app.ActivityManager
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

data class MotionPolicy(
    val enabled: Boolean,
    val ambientEnabled: Boolean
)

@Composable
fun rememberMotionPolicy(): MotionPolicy {
    val context = LocalContext.current
    return remember(context) {
        val enabled = runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
        }.getOrDefault(true)
        val lowRam = (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.isLowRamDevice == true
        MotionPolicy(enabled = enabled, ambientEnabled = enabled && !lowRam)
    }
}

internal fun motionDuration(policy: MotionPolicy, duration: Int): Int = if (policy.enabled) duration else 0
