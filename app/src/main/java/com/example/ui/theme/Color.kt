package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// CRIXER Dark Palette
val CrixerBlack = Color(0xFF06080C)
val CrixerDeepDark = Color(0xFF090D13)
val CrixerSurfaceDark = Color(0xFF10141D)
val CrixerCardDark = Color(0xFF161C27)
val CrixerGlassBorder = Color(0x33CBD5E1)
val CrixerGlassSurface = Color(0x281E293B)
val CrixerGlassHighlight = Color(0x3DFFFFFF)

// Accent Colors
val CrixerRed = Color(0xFFEF4444)
val CrixerDarkRed = Color(0xFFDC2626)
val CrixerLightRed = Color(0xFFF87171)

// Metallic & Neutral
val CrixerWhite = Color(0xFFFFFFFF)
val CrixerTextSecondary = Color(0xFF94A3B8)
val CrixerTextTertiary = Color(0xFF64748B)
val CrixerBorder = Color(0xFF1E293B)

// Cricket specific
val CrixerSixGlow = Color(0xFF38BDF8)
val CrixerWicketGlow = Color(0xFFEF4444)
val IndiaSaffron = Color(0xFFFF9933)
val IndiaGreen = Color(0xFF138808)
val AustraliaBlue = Color(0xFF002B7F)
val AustraliaGold = Color(0xFFFFCD00)

// Liquid Glass Brush Gradients
val LiquidGlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x35334155),
        Color(0x181E293B),
        Color(0x250F172A)
    )
)

val LiquidGlassBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x66FFFFFF),
        Color(0x15FFFFFF),
        Color(0x05FFFFFF),
        Color(0x2538BDF8)
    )
)

val LiquidGlassActiveBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x99EF4444),
        Color(0x33EF4444),
        Color(0x2238BDF8),
        Color(0x8838BDF8)
    )
)
