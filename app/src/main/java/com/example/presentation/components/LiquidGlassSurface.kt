package com.example.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerWicketGlow

enum class LiquidGlassPulse {
    NONE,
    SIX,
    WICKET
}

@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    tintColor: Color = Color(0xFF131924),
    pulseState: LiquidGlassPulse = LiquidGlassPulse.NONE,
    isInteractive: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // Spring scale for physically responsive touch
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.982f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "glassScale"
    )

    // Pulse animation for Six or Wicket
    val pulseAlpha = remember { Animatable(0f) }
    LaunchedEffect(pulseState) {
        if (pulseState != LiquidGlassPulse.NONE) {
            pulseAlpha.snapTo(0.85f)
            pulseAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
            )
        }
    }

    val pulseColor = when (pulseState) {
        LiquidGlassPulse.SIX -> CrixerSixGlow
        LiquidGlassPulse.WICKET -> CrixerWicketGlow
        LiquidGlassPulse.NONE -> Color.Transparent
    }

    // Dynamic specular refraction border
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            if (pulseState != LiquidGlassPulse.NONE) pulseColor.copy(alpha = 0.8f) else Color(0x66FFFFFF),
            Color(0x1AFFFFFF),
            Color(0x0AFFFFFF),
            if (pulseState != LiquidGlassPulse.NONE) pulseColor.copy(alpha = 0.5f) else Color(0x2838BDF8)
        ),
        start = Offset(0f, 0f),
        end = Offset(400f, 600f)
    )

    // Layered liquid gradient
    val baseGradient = Brush.verticalGradient(
        colors = listOf(
            tintColor.copy(alpha = 0.88f),
            tintColor.copy(alpha = 0.65f),
            Color(0xFF090D14).copy(alpha = 0.92f)
        )
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(baseGradient)
            .drawBehind {
                // Draw inner refractive rim specular sheen
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x25FFFFFF),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.25f, 0f),
                        radius = size.width * 0.7f
                    )
                )

                // Event pulse aura
                if (pulseAlpha.value > 0f) {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                pulseColor.copy(alpha = pulseAlpha.value * 0.35f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.5f),
                            radius = size.width * 0.8f
                        )
                    )
                }
            }
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = shape
            )
            .then(
                if (isInteractive && onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = { onClick() }
                        )
                    }
                } else Modifier
            ),
        content = content
    )
}
