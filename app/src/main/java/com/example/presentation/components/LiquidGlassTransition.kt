package com.example.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.unit.dp
import com.example.domain.model.Match
import com.example.ui.theme.CrixerBlack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CRIXER Official Live Match Opening Transition:
 *
 * Sequence:
 * LIVE MATCH CARD
 *       ↓
 * STEP 1: CARD TOUCH RESPONSE (~100-120ms)
 * Immediately responds to touch, slight card compression, subtle liquid ripple.
 *       ↓
 * STEP 2: CRIXER LOGO EMERGES (~100-280ms)
 * Official CRIXER logo physically forms out of the card's liquid glass material.
 * Spring-based motion: scale 0.70 -> 1.0, opacity 0 -> 1.0, defocused -> sharp.
 *       ↓
 * STEP 3: LOGO ACTIVATION (~260-400ms)
 * Specular highlight sweeps across glass, C-shaped chrome catches light,
 * red cricket ball subtly brightens, subtle refraction halo appears.
 *       ↓
 * STEP 4: LOGO BECOMES THE PORTAL (~400-600ms)
 * Logo gently scales: 1.00 -> 1.08 -> 1.20 -> 1.60 -> 2.8f+.
 * Liquid glass depth and refraction increase, background disappears into the material.
 *       ↓
 * STEP 5: CAMERA PASSES THROUGH (~580-680ms)
 * Camera traverses through the CRIXER logo aperture into dark Liquid Glass environment,
 * subtle directional light sweeps across, and Live Match screen resolves seamlessly.
 */
@Composable
fun LiquidGlassTransition(
    match: Match,
    isRepeatVisit: Boolean = false,
    onTransitionFinished: () -> Unit
) {
    // Touch & compression animatables
    val cardCompression = remember { Animatable(1.0f) }
    val touchRippleProgress = remember { Animatable(0f) }

    // Backdrop & depth dimming
    val backdropDim = remember { Animatable(0f) }

    // Logo emergence & spring physics
    val logoScale = remember { Animatable(if (isRepeatVisit) 0.85f else 0.70f) }
    val logoAlpha = remember { Animatable(0f) }

    // Logo activation: specular sweep, C catch light, ball brightens
    val specularSweep = remember { Animatable(0f) }
    val ballBrighten = remember { Animatable(1.0f) }
    val refractionGlint = remember { Animatable(0f) }

    // Portal expansion & camera pass-through
    val cameraTravel = remember { Animatable(0f) }
    val lightSweep = remember { Animatable(0f) }

    LaunchedEffect(isRepeatVisit) {
        if (isRepeatVisit) {
            // Snappy repeat visit (~360ms total)
            launch {
                cardCompression.animateTo(
                    targetValue = 0.98f,
                    animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
                )
                cardCompression.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
                )
            }
            launch {
                backdropDim.animateTo(
                    targetValue = 0.92f,
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                )
            }
            launch {
                logoAlpha.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
                )
            }
            launch {
                logoScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                // Expand into portal
                logoScale.animateTo(
                    targetValue = 3.2f,
                    animationSpec = tween(durationMillis = 200, easing = CubicBezierEasing(0.35f, 0f, 0.2f, 1f))
                )
            }
            launch {
                delay(60)
                specularSweep.animateTo(1.0f, animationSpec = tween(160, easing = LinearEasing))
            }
            launch {
                delay(120)
                cameraTravel.animateTo(1.0f, animationSpec = tween(220, easing = FastOutSlowInEasing))
            }
            delay(350)
            onTransitionFinished()
        } else {
            // Full CRIXER Logo Portal Sequence (~650ms total)
            // STEP 1: Card Touch Response (~100-120ms)
            launch {
                cardCompression.animateTo(
                    targetValue = 0.97f,
                    animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing)
                )
                cardCompression.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing)
                )
            }
            launch {
                touchRippleProgress.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                )
            }

            // Backdrop dimming
            launch {
                backdropDim.animateTo(
                    targetValue = 0.40f,
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
                )
                delay(120)
                backdropDim.animateTo(
                    targetValue = 0.88f,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                )
                delay(100)
                backdropDim.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                )
            }

            // STEP 2: CRIXER Logo Emerges (~100-280ms)
            launch {
                delay(80)
                logoAlpha.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
                )
            }
            launch {
                delay(60)
                // Spring from 0.70 to 1.0 (physically forming out of the glass)
                logoScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )

                // STEP 3: Subtle activation rest & slight expansion (1.0 -> 1.08)
                logoScale.animateTo(
                    targetValue = 1.08f,
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
                )

                // STEP 4: Portal expansion (1.08 -> 1.20 -> 1.60 -> 3.2f)
                logoScale.animateTo(
                    targetValue = 3.2f,
                    animationSpec = tween(durationMillis = 240, easing = CubicBezierEasing(0.35f, 0f, 0.15f, 1f))
                )
            }

            // STEP 3: Logo Activation (~260-400ms)
            launch {
                delay(220)
                // Specular highlight sweep across the glass and C-mark
                launch {
                    specularSweep.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(durationMillis = 180, easing = LinearEasing)
                    )
                }
                // Red cricket ball subtly brightens
                launch {
                    ballBrighten.animateTo(
                        targetValue = 1.45f,
                        animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
                    )
                    ballBrighten.animateTo(
                        targetValue = 1.15f,
                        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
                    )
                }
                // Refraction glint catches the edges
                launch {
                    refractionGlint.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    )
                }
            }

            // STEP 5: Camera Passes Through the Logo (~480-660ms)
            launch {
                delay(460)
                launch {
                    cameraTravel.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(durationMillis = 200, easing = CubicBezierEasing(0.3f, 0f, 0.2f, 1f))
                    )
                }
                launch {
                    lightSweep.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(durationMillis = 190, easing = LinearEasing)
                    )
                }
            }

            // Complete transition smoothly into Live Match Screen
            delay(640)
            onTransitionFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Responsive tap to skip
                onTransitionFinished()
            },
        contentAlignment = Alignment.Center
    ) {
        val bDim = backdropDim.value
        val rip = touchRippleProgress.value
        val lScale = logoScale.value
        val lAlpha = logoAlpha.value
        val sSweep = specularSweep.value
        val bGlow = ballBrighten.value
        val rGlint = refractionGlint.value
        val cam = cameraTravel.value
        val sweep = lightSweep.value

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)

            // 1. SURROUNDING BACKDROP: Darkens smoothly as card enters glass mode
            if (bDim > 0.01f) {
                drawRect(
                    color = CrixerBlack.copy(alpha = (bDim * 0.95f).coerceIn(0f, 0.96f))
                )
            }

            // 2. TOUCH RESPONSE: Subtle liquid glass compression ripple on card
            if (rip > 0.01f && rip < 0.99f) {
                val ripRadius = 55.dp.toPx() * rip
                val ripAlpha = ((1f - rip) * 0.45f).coerceIn(0f, 1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = ripAlpha),
                            Color(0x6038BDF8).copy(alpha = ripAlpha * 0.6f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = ripRadius
                    ),
                    radius = ripRadius,
                    center = center
                )
            }

            // 3. CRIXER LOGO SQUIRCLE & CHROME / GLASS PORTAL
            if (lAlpha > 0.01f) {
                val baseLogoSize = 108.dp.toPx()
                val currentSize = baseLogoSize * lScale
                val cornerRadius = currentSize * 0.28f

                val left = center.x - (currentSize / 2f)
                val top = center.y - (currentSize / 2f)

                val squircleRect = RoundRect(
                    left = left,
                    top = top,
                    right = left + currentSize,
                    bottom = top + currentSize,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
                val squirclePath = Path().apply { addRoundRect(squircleRect) }

                // Glass lens body (Physical translucent depth)
                val bodyOpacity = (lAlpha * (1f - (cam * 0.5f))).coerceIn(0f, 1f)
                drawPath(
                    path = squirclePath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x351E293B).copy(alpha = bodyOpacity),
                            Color(0x180F172A).copy(alpha = bodyOpacity),
                            Color(0x95030508).copy(alpha = bodyOpacity)
                        ),
                        center = center,
                        radius = currentSize * 1.8f
                    ),
                    style = Fill
                )

                // Refraction halo around logo squircle during activation & portal scale
                if (rGlint > 0.05f && cam < 0.85f) {
                    val haloAlpha = (rGlint * 0.55f * (1f - cam)).coerceIn(0f, 1f)
                    drawPath(
                        path = squirclePath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x8038BDF8).copy(alpha = haloAlpha),
                                Color.Transparent,
                                Color(0x6038BDF8).copy(alpha = haloAlpha * 0.7f)
                            ),
                            start = Offset(left, top),
                            end = Offset(left + currentSize, top + currentSize)
                        ),
                        style = Stroke(width = 3.dp.toPx() * (1f + cam * 0.5f))
                    )
                }

                // Dual-rim specular border (True Liquid Glass edge)
                val borderAlpha = (lAlpha * (1f - (cam * 0.4f))).coerceIn(0f, 1f)
                drawPath(
                    path = squirclePath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xE6FFFFFF).copy(alpha = borderAlpha),
                            Color(0x9938BDF8).copy(alpha = borderAlpha),
                            Color(0x20FFFFFF).copy(alpha = borderAlpha),
                            Color(0x6038BDF8).copy(alpha = borderAlpha),
                            Color(0xD0FFFFFF).copy(alpha = borderAlpha)
                        ),
                        start = Offset(left, top),
                        end = Offset(left + currentSize * 1.4f, top + currentSize * 1.4f)
                    ),
                    style = Stroke(width = (1.6.dp.toPx() * lScale.coerceAtMost(1.8f)).coerceAtLeast(1.2f))
                )

                // Render the official CRIXER mark inside the squircle
                // Scaled and centered with exact brand geometry
                val padding = currentSize * 0.12f
                val contentLeft = left + padding
                val contentTop = top + padding
                val contentSize = currentSize - (padding * 2f)

                if (contentSize > 10f) {
                    val contentAlpha = (lAlpha * (1f - (cam * 0.75f))).coerceIn(0f, 1f)

                    // Draw the CRIXER logo mark using official shared geometry
                    inset(
                        left = contentLeft,
                        top = contentTop,
                        right = (w - (contentLeft + contentSize)).coerceAtLeast(0f),
                        bottom = (h - (contentTop + contentSize)).coerceAtLeast(0f)
                    ) {
                        drawCrixerLogoMarkContent(
                            cMarkAlpha = contentAlpha,
                            specularSweep = sSweep,
                            ballAlpha = contentAlpha,
                            ballGlowMultiplier = bGlow,
                            refractionGlintAlpha = (lAlpha * (0.8f + 0.2f * rGlint)).coerceIn(0f, 1f)
                        )
                    }
                }
            }

            // 4. STEP 5: CAMERA PASSES THROUGH INTO LIVE MATCH
            // Screen becomes dark Liquid Glass environment with subtle light passing across
            if (cam > 0.05f) {
                val tunnelAlpha = (cam * 0.85f).coerceIn(0f, 0.90f)

                // Deep atmospheric Liquid Glass environment
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x200F2744).copy(alpha = tunnelAlpha),
                            Color(0x05060A14).copy(alpha = tunnelAlpha * 0.6f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = w * 0.85f
                    ),
                    blendMode = BlendMode.Screen
                )

                // Subtle light beam sweep across screen as camera penetrates glass
                if (sweep > 0.02f) {
                    val beamX = w * (sweep * 1.3f - 0.15f)
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x4038BDF8).copy(alpha = (0.5f * (1f - sweep) * tunnelAlpha).coerceIn(0f, 1f)),
                                Color.White.copy(alpha = (0.4f * (1f - sweep) * tunnelAlpha).coerceIn(0f, 1f)),
                                Color.Transparent
                            ),
                            start = Offset(beamX - 60.dp.toPx(), 0f),
                            end = Offset(beamX + 60.dp.toPx(), h)
                        ),
                        start = Offset(beamX - 60.dp.toPx(), 0f),
                        end = Offset(beamX + 60.dp.toPx(), h),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        blendMode = BlendMode.Screen
                    )
                }
            }
        }
    }
}
