package com.zalandunk.dawnsdew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zalandunk.dawnsdew.ui.DawnsDewApp
import com.zalandunk.dawnsdew.ui.theme.DawnsDewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DawnsDewTheme {
                DawnsDewApp()
            }
        }
    }
}