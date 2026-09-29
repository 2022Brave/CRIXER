package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CrixerDarkColorScheme = darkColorScheme(
    primary = CrixerRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2A1215),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = CrixerSixGlow,
    onTertiary = Color.Black,
    background = CrixerBlack,
    onBackground = CrixerWhite,
    surface = CrixerDeepDark,
    onSurface = CrixerWhite,
    surfaceVariant = CrixerSurfaceDark,
    onSurfaceVariant = CrixerTextSecondary,
    outline = CrixerBorder,
    outlineVariant = Color(0x33CBD5E1)
)

@Composable
fun CrixerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Crixer is inherently a premium dark editorial cricket experience per brand spec
    MaterialTheme(
        colorScheme = CrixerDarkColorScheme,
        typography = Typography,
        content = content
    )
}
