package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

/**
 * Renders the official CRIXER Liquid Glass Mark:
 * Sculpted chrome/glass C, speed trails, glowing red cricket ball with seam,
 * enclosed in a refractive squircle lens, with metallic wordmark and tagline.
 */
@Composable
fun CrixerOfficialLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    showWordmark: Boolean = true,
    showTagline: Boolean = true,
    animated: Boolean = true,
    cMarkAlpha: Float = 1f,
    specularSweep: Float = 1f,
    ballAlpha: Float = 1f,
    wordmarkAlpha: Float = 1f,
    taglineAlpha: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val pulseGlow by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ballGlow"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(1f) }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Liquid Glass Squircle Container
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.28f))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x351E293B),
                            Color(0x150F172A),
                            Color(0x95030508)
                        ),
                        radius = size.value * 2f
                    )
                )
                .border(
                    width = 1.6.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xE6FFFFFF),
                            Color(0x9938BDF8),
                            Color(0x20FFFFFF),
                            Color(0x6038BDF8),
                            Color(0xD0FFFFFF)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.value * 3, size.value * 3)
                    ),
                    shape = RoundedCornerShape(size * 0.28f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.12f)) {
                drawCrixerLogoMarkContent(
                    cMarkAlpha = cMarkAlpha,
                    specularSweep = specularSweep,
                    ballAlpha = ballAlpha,
                    ballGlowMultiplier = pulseGlow,
                    refractionGlintAlpha = 1f
                )
            }
        }

        if (showWordmark) {
            Spacer(modifier = Modifier.height(size * 0.14f))

            // Metallic Chrome Wordmark: CRI [X] ER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(wordmarkAlpha)
            ) {
                Text(
                    text = "CRI",
                    color = CrixerWhite,
                    fontSize = (size.value * 0.26f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "X",
                    color = CrixerRed,
                    fontSize = (size.value * 0.26f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "ER",
                    color = CrixerWhite,
                    fontSize = (size.value * 0.26f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "EVERY  BALL  COUNTS",
                color = CrixerTextSecondary,
                fontSize = (size.value * 0.095f).sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.5.sp,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }
    }
}

/**
 * Shared drawing implementation of the official CRIXER mark.
 * Renders the sculpted chrome C, speed trails, and glowing red cricket ball with seam.
 */
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCrixerLogoMarkContent(
    cMarkAlpha: Float = 1f,
    specularSweep: Float = 1f,
    ballAlpha: Float = 1f,
    ballGlowMultiplier: Float = 1f,
    refractionGlintAlpha: Float = 0.8f
) {
    val w = this.size.width
    val h = this.size.height

    // Specular corner refraction glints
    if (refractionGlintAlpha > 0.01f) {
        drawLine(
            color = Color.White.copy(alpha = (0.85f * refractionGlintAlpha).coerceIn(0f, 1f)),
            start = Offset(w * 0.05f, h * 0.15f),
            end = Offset(w * 0.15f, h * 0.05f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF38BDF8).copy(alpha = (0.75f * refractionGlintAlpha).coerceIn(0f, 1f)),
            start = Offset(w * 0.85f, h * 0.95f),
            end = Offset(w * 0.95f, h * 0.85f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Speed lines trailing the ball
    val streakPaintColor = Color(0xFFEF4444)
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, streakPaintColor.copy(alpha = (0.85f * ballAlpha).coerceIn(0f, 1f)), Color.White.copy(alpha = ballAlpha.coerceIn(0f, 1f))),
            start = Offset(w * 0.35f, h * 0.52f),
            end = Offset(w * 0.72f, h * 0.44f)
        ),
        start = Offset(w * 0.35f, h * 0.52f),
        end = Offset(w * 0.72f, h * 0.44f),
        strokeWidth = 2.2.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color(0xFFFF6B6B).copy(alpha = ballAlpha.coerceIn(0f, 1f)), Color.White.copy(alpha = ballAlpha.coerceIn(0f, 1f))),
            start = Offset(w * 0.40f, h * 0.56f),
            end = Offset(w * 0.74f, h * 0.50f)
        ),
        start = Offset(w * 0.40f, h * 0.56f),
        end = Offset(w * 0.74f, h * 0.50f),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, streakPaintColor.copy(alpha = (0.6f * ballAlpha).coerceIn(0f, 1f))),
            start = Offset(w * 0.33f, h * 0.59f),
            end = Offset(w * 0.72f, h * 0.57f)
        ),
        start = Offset(w * 0.33f, h * 0.59f),
        end = Offset(w * 0.72f, h * 0.57f),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Sculpted Chrome "C"
    val cPath = Path().apply {
        moveTo(w * 0.82f, h * 0.28f)
        cubicTo(w * 0.68f, h * 0.16f, w * 0.45f, h * 0.16f, w * 0.30f, h * 0.28f)
        cubicTo(w * 0.14f, h * 0.40f, w * 0.12f, h * 0.68f, w * 0.28f, h * 0.82f)
        cubicTo(w * 0.42f, h * 0.94f, w * 0.66f, h * 0.95f, w * 0.82f, h * 0.84f)
        lineTo(w * 0.72f, h * 0.74f)
        cubicTo(w * 0.61f, h * 0.82f, w * 0.44f, h * 0.81f, w * 0.36f, h * 0.72f)
        cubicTo(w * 0.24f, h * 0.60f, w * 0.26f, h * 0.44f, w * 0.37f, h * 0.36f)
        cubicTo(w * 0.47f, h * 0.27f, w * 0.63f, h * 0.29f, w * 0.72f, h * 0.36f)
        close()
    }

    // Chrome metallic gradient fill
    if (cMarkAlpha > 0.01f) {
        val sweepShift = (specularSweep - 0.5f) * 0.4f
        drawPath(
            path = cPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFFFFF).copy(alpha = cMarkAlpha),
                    Color(0xFFCBD5E1).copy(alpha = cMarkAlpha),
                    Color(0xFF64748B).copy(alpha = cMarkAlpha),
                    Color(0xFFE2E8F0).copy(alpha = cMarkAlpha),
                    Color(0xFF93C5FD).copy(alpha = cMarkAlpha),
                    Color(0xFF334155).copy(alpha = cMarkAlpha)
                ),
                start = Offset(w * (0.15f + sweepShift), h * 0.15f),
                end = Offset(w * (0.85f + sweepShift), h * 0.85f)
            )
        )

        // Bevel specular edge along C outer rim
        drawPath(
            path = cPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = (0.95f * cMarkAlpha).coerceIn(0f, 1f)),
                    Color(0x9938BDF8).copy(alpha = (0.7f * cMarkAlpha).coerceIn(0f, 1f)),
                    Color.Transparent
                ),
                start = Offset(w * 0.2f * specularSweep, h * 0.2f),
                end = Offset(w * 0.8f * specularSweep, h * 0.8f)
            ),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }

    // Glowing Red Cricket Ball
    val ballCenter = Offset(w * 0.77f, h * 0.49f)
    val ballRadius = w * 0.13f

    if (ballAlpha > 0.01f) {
        // Outer radial glow
        val effGlow = (0.7f * ballGlowMultiplier * ballAlpha).coerceIn(0f, 1f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xB0EF4444).copy(alpha = effGlow),
                    Color(0x40EF4444).copy(alpha = (0.4f * ballAlpha).coerceIn(0f, 1f)),
                    Color.Transparent
                ),
                center = ballCenter,
                radius = ballRadius * 2.2f
            ),
            radius = ballRadius * 2.2f,
            center = ballCenter
        )

        // Ball sphere body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF7B82).copy(alpha = ballAlpha),
                    Color(0xFFEF4444).copy(alpha = ballAlpha),
                    Color(0xFF991B1B).copy(alpha = ballAlpha),
                    Color(0xFF5B0E0E).copy(alpha = ballAlpha)
                ),
                center = Offset(ballCenter.x - ballRadius * 0.25f, ballCenter.y - ballRadius * 0.25f),
                radius = ballRadius * 1.2f
            ),
            radius = ballRadius,
            center = ballCenter
        )

        // Curved Seam (White double stitched line)
        val seamPath = Path().apply {
            moveTo(ballCenter.x - ballRadius * 0.55f, ballCenter.y - ballRadius * 0.65f)
            quadraticBezierTo(
                ballCenter.x + ballRadius * 0.15f, ballCenter.y,
                ballCenter.x - ballRadius * 0.25f, ballCenter.y + ballRadius * 0.75f
            )
        }
        drawPath(
            path = seamPath,
            color = Color.White.copy(alpha = ballAlpha),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
        // Specular sheen dot
        drawCircle(
            color = Color.White.copy(alpha = 0.85f * ballAlpha),
            radius = ballRadius * 0.18f,
            center = Offset(ballCenter.x - ballRadius * 0.35f, ballCenter.y - ballRadius * 0.35f)
        )
    }
}
