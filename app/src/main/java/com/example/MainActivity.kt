package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.presentation.CrixerApp
import com.example.ui.theme.CrixerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable true edge-to-edge
        enableEdgeToEdge()

        // Set status bar and navigation bar icon appearance
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // Crisp white system icons for dark Crixer theme
            isAppearanceLightNavigationBars = false
        }

        setContent {
            CrixerTheme {
                CrixerApp()
            }
        }
    }
}
