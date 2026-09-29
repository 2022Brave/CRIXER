package com.example.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.presentation.components.CrixerOfficialLogo
import com.example.ui.theme.CrixerBlack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CRIXER Official Startup Splash Screen:
 *
 * Sequence (~1000ms):
 * 0–200 ms:   Near-black screen; almost invisible cool-white reflection begins.
 * 200–450 ms: Prominent Liquid Glass C mark gradually becomes visible.
 * 450–650 ms: Realistic specular highlight moves across the glass surface;
 *             red cricket ball subtly catches light.
 * 650–850 ms: CRIXER wordmark resolves.
 * 850–1100 ms: Home interface begins appearing; splash transitions naturally into Home.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0-200ms: subtle cool-white reflection emerges behind mark (mark already visible from launch)
    val subtleLight = remember { Animatable(0f) }
    // Liquid Glass C mark begins visible to seamlessly match the Android 12+ system launch screen
    val cMarkAlpha = remember { Animatable(1.0f) }
    // 450-650ms: Specular sweep and red ball catch light
    val specularSweep = remember { Animatable(0f) }
    val ballAlpha = remember { Animatable(0f) }
    // 650-850ms: Wordmark resolves
    val wordmarkAlpha = remember { Animatable(0f) }
    // 850-1050ms: "EVERY BALL COUNTS" tagline appears
    val taglineAlpha = remember { Animatable(0f) }
    // 1050-1300ms: Splash transitions naturally into Home
    val splashAlpha = remember { Animatable(1.0f) }

    LaunchedEffect(Unit) {
        // 0–200 ms: Near-black screen; almost invisible cool-white reflection begins
        launch {
            subtleLight.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
        delay(200)

        // 200–450 ms: The Liquid Glass C mark gradually becomes visible
        launch {
            cMarkAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
        }
        delay(250)

        // 450–650 ms: Realistic specular highlight moves across surface; red cricket ball catches light
        launch {
            specularSweep.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 200, easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f))
            )
        }
        launch {
            ballAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
        delay(200)

        // 650–850 ms: CRIXER wordmark resolves
        launch {
            wordmarkAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
        delay(200)

        // 850–1050 ms: "EVERY BALL COUNTS" tagline appears
        launch {
            taglineAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
        delay(200)

        // 1050–1300 ms: Splash naturally transitions into Home background
        launch {
            splashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
            onSplashFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(splashAlpha.value)
            .background(CrixerBlack)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Responsive tap to skip
                onSplashFinished()
            },
        contentAlignment = Alignment.Center
    ) {
        // 0–200ms: Almost invisible cool-white reflection
        if (subtleLight.value > 0.01f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f - 24.dp.toPx())
                val glowRadius = size.width * 0.7f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE2E8F0).copy(alpha = 0.12f * subtleLight.value),
                            Color(0x6038BDF8).copy(alpha = 0.06f * subtleLight.value),
                            Color.Transparent
                        ),
                        center = center,
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = center
                )
            }
        }

        // Prominent Liquid Glass Crixer Mark with generous breathing space
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 56.dp)
        ) {
            CrixerOfficialLogo(
                size = 156.dp,
                showWordmark = true,
                showTagline = true,
                animated = true,
                cMarkAlpha = cMarkAlpha.value,
                specularSweep = specularSweep.value,
                ballAlpha = ballAlpha.value,
                wordmarkAlpha = wordmarkAlpha.value,
                taglineAlpha = taglineAlpha.value
            )
        }
    }
}
