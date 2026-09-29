package com.example.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite
import kotlin.math.abs
import kotlin.math.sin

/**
 * CRIXER UNIFIED LIQUID GLASS SYSTEM
 *
 * Core Principle:
 * "Accurate cricket first. Beautiful interface second. Liquid Glass without compromise."
 *
 * Universal interaction language:
 * PRESS -> DEFORM -> STRETCH -> GLIDE -> REFRACT -> SETTLE
 *
 * Primitives:
 * - LiquidGlassSurface: Physical glass container with touch compression and specular rim
 * - LiquidGlassSelection: Physical movable glass capsule
 * - LiquidGlassHighlight: Specular sweep sheen moving across the glass surface
 * - LiquidGlassBackground: Atmospheric slow ambient glass background (8–20s cycle)
 * - CrixerLiquidGlassSegmentedControl: Reusable segmented pill control for all options
 */

enum class SegmentDensity {
    COMPACT, // Used for toolbars, secondary filters, switches (e.g. 32-34dp height)
    REGULAR  // Used for primary navigation tabs (e.g. 40-42dp height)
}

data class LiquidGlassSegmentItem<T>(
    val key: T,
    val label: String,
    val isLiveDot: Boolean = false,
    val accentColor: Color? = null,
    val testTag: String? = null
)

/**
 * Reusable unified Liquid Glass Segmented Pill Control.
 *
 * Used universally for:
 * - LIVE | COMPLETED | UPCOMING
 * - T20I | ODI | TEST
 * - SIMPLE | IMMERSIVE
 * - MATCH TABS (Overview, Scorecard, Insights, etc.)
 */
@Composable
fun <T> CrixerLiquidGlassSegmentedControl(
    items: List<LiquidGlassSegmentItem<T>>,
    selectedKey: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    isSimpleMode: Boolean = false,
    density: SegmentDensity = SegmentDensity.REGULAR
) {
    val densityScope = LocalDensity.current
    val itemPositions = remember { mutableStateMapOf<T, Pair<Float, Float>>() } // Pair<OffsetPx, WidthPx>

    // Touch press state for physical compression
    var isPressed by remember { mutableStateOf(false) }

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.968f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "segmentPressScale"
    )

    // Find target bounds for currently selected item
    val selectedBounds = itemPositions[selectedKey] ?: Pair(0f, 0f)
    val targetOffsetX = selectedBounds.first
    val targetWidth = selectedBounds.second

    // Spring glide animation for X position
    val animatedOffsetX by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = spring(
            dampingRatio = 0.76f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "segmentGlideX"
    )

    // Spring glide animation for Width adapting smoothly to destination label
    val animatedWidth by animateFloatAsState(
        targetValue = targetWidth,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "segmentGlideWidth"
    )

    // Calculate mid-flight stretch along X: stretches at midpoint of flight
    val flightDelta = abs(targetOffsetX - animatedOffsetX)
    val isMoving = flightDelta > 1.5f
    val stretchFactor by animateFloatAsState(
        targetValue = if (isMoving && !isSimpleMode) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "segmentStretch"
    )

    // Specular highlight traveling across the capsule during movement
    val highlightSweep = remember { Animatable(0f) }
    LaunchedEffect(selectedKey) {
        if (!isSimpleMode) {
            highlightSweep.snapTo(0f)
            highlightSweep.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )
        }
    }

    val controlHeight = when (density) {
        SegmentDensity.COMPACT -> 34.dp
        SegmentDensity.REGULAR -> 40.dp
    }
    val cornerRadius = controlHeight / 2
    val innerPadding = 3.dp

    // Unified pill container
    Box(
        modifier = modifier
            .scale(pressScale)
            .height(controlHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                if (isSimpleMode) {
                    SolidColor(Color(0xFF0F141E))
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x351E2838),
                            Color(0x18101824),
                            Color(0x30090D15)
                        )
                    )
                }
            )
            .border(
                width = 0.8.dp,
                brush = if (isSimpleMode) {
                    SolidColor(Color(0xFF1E2838))
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x50FFFFFF),
                            Color(0x1538BDF8),
                            Color(0x10FFFFFF),
                            Color(0x3538BDF8)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(400f, 80f)
                    )
                },
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(innerPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        // MOVING LIQUID GLASS SELECTION CAPSULE
        if (targetWidth > 0f) {
            val capsuleOffsetDp = with(densityScope) { animatedOffsetX.toDp() }
            val capsuleWidthDp = with(densityScope) { animatedWidth.toDp() }
            val capsuleCorner = (controlHeight - innerPadding * 2) / 2

            // Selected Item Accent Color (e.g. subtle CRIXER red dot or cool tint)
            val currentItem = items.find { it.key == selectedKey }
            val itemAccent = currentItem?.accentColor ?: Color(0xFF38BDF8)

            Box(
                modifier = Modifier
                    .offset(x = capsuleOffsetDp)
                    .scale(scaleX = stretchFactor, scaleY = 1.0f)
                    .width(capsuleWidthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(capsuleCorner))
                    .background(
                        if (isSimpleMode) {
                            SolidColor(Color(0xFF263242))
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E2C42).copy(alpha = 0.92f),
                                    Color(0xFF101927).copy(alpha = 0.95f),
                                    Color(0xFF0B111A).copy(alpha = 0.98f)
                                )
                            )
                        }
                    )
                    .border(
                        width = 0.8.dp,
                        brush = if (isSimpleMode) {
                            SolidColor(Color(0xFF3B4D66))
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    itemAccent.copy(alpha = 0.60f),
                                    Color(0x20FFFFFF),
                                    Color.White.copy(alpha = 0.50f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(200f, 60f)
                            )
                        },
                        shape = RoundedCornerShape(capsuleCorner)
                    )
                    .drawBehind {
                        if (!isSimpleMode) {
                            // Liquid Glass Specular Top Highlight moving across the capsule
                            val sweepX = size.width * highlightSweep.value
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.85f),
                                        itemAccent.copy(alpha = 0.40f),
                                        Color.Transparent
                                    ),
                                    startX = sweepX - size.width * 0.4f,
                                    endX = sweepX + size.width * 0.4f
                                ),
                                start = Offset(0f, 1f),
                                end = Offset(size.width, 1f),
                                strokeWidth = 1.5.dp.toPx()
                            )

                            // Subtle internal optical refraction glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        itemAccent.copy(alpha = 0.16f),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width * 0.5f, size.height * 0.4f),
                                    radius = size.width * 0.6f
                                ),
                                radius = size.width * 0.6f,
                                center = Offset(size.width * 0.5f, size.height * 0.4f),
                                blendMode = BlendMode.Screen
                            )
                        }
                    }
            )
        }

        // SEGMENT LABELS
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.key == selectedKey

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .onGloballyPositioned { coordinates ->
                            val pos = coordinates.positionInParent()
                            val w = coordinates.size.width.toFloat()
                            itemPositions[item.key] = Pair(pos.x, w)
                        }
                        .pointerInput(item.key) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    tryAwaitRelease()
                                    isPressed = false
                                },
                                onTap = { onItemSelected(item.key) }
                            )
                        }
                        .then(
                            if (item.testTag != null) Modifier.testTag(item.testTag) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Subtle LIVE red dot indicator (respecting rule: do NOT make whole pill red)
                        if (item.isLiveDot) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) {
                                            if (isSimpleMode) CrixerRed else Color(0xFFFF4D4D)
                                        } else {
                                            Color(0xFF882020)
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = item.label,
                            color = when {
                                isSelected -> CrixerWhite
                                isSimpleMode -> Color(0xFF8E9BAE)
                                else -> CrixerTextSecondary
                            },
                            fontSize = when (density) {
                                SegmentDensity.COMPACT -> 11.5.sp
                                SegmentDensity.REGULAR -> 12.5.sp
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Atmospheric Liquid Glass Background primitive (Section 9):
 * - Extremely subtle depth
 * - Slow ambient movement (8–20s cycle, default 14s)
 * - Soft refraction, restrained lighting
 * - Cricket information remains dominant
 */
@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    isSimpleMode: Boolean = false,
    cycleDurationMillis: Int = 14000,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glassBackgroundDrift")
    val driftPhase by if (!isSimpleMode) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = cycleDurationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "driftPhase"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CrixerBlack)
    ) {
        if (!isSimpleMode) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val rad = driftPhase * 2f * Math.PI.toFloat()

                // Base nocturnal stadium atmosphere
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF070C15),
                            Color(0xFF04070D),
                            Color(0xFF030509)
                        ),
                        startY = 0f,
                        endY = h
                    )
                )

                // Slow subtle ambient light orb 1 (Deep celestial blue)
                val cx1 = (w * 0.25f) + (w * 0.10f * sin(rad))
                val cy1 = (h * 0.18f) + (h * 0.05f * kotlin.math.cos(rad))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x181E3A8A),
                            Color(0x080F172A),
                            Color.Transparent
                        ),
                        center = Offset(cx1, cy1),
                        radius = w * 0.75f
                    ),
                    radius = w * 0.75f,
                    center = Offset(cx1, cy1),
                    blendMode = BlendMode.Screen
                )

                // Slow subtle ambient light orb 2 (Deep jade/emerald turf glow at base)
                val cx2 = (w * 0.75f) - (w * 0.08f * sin(rad))
                val cy2 = (h * 0.82f) - (h * 0.04f * kotlin.math.cos(rad))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x120E3A20),
                            Color(0x0506160D),
                            Color.Transparent
                        ),
                        center = Offset(cx2, cy2),
                        radius = w * 0.70f
                    ),
                    radius = w * 0.70f,
                    center = Offset(cx2, cy2),
                    blendMode = BlendMode.Screen
                )
            }
        }

        content()
    }
}
