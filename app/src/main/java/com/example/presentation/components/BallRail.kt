package com.example.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BallOutcome
import com.example.domain.model.BallState
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

@Composable
fun BallRail(
    balls: List<BallState>,
    modifier: Modifier = Modifier,
    onBallClicked: ((BallState) -> Unit)? = null
) {
    var selectedBallForPopup by remember { mutableStateOf<BallState?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "This Over",
                color = CrixerTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            if (selectedBallForPopup != null) {
                Text(
                    text = "${selectedBallForPopup?.speedKph} km/h · ${selectedBallForPopup?.commentary?.take(28)}...",
                    color = CrixerWhite,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Standard over has 6 slots: show current completed balls or fill placeholders
            val displayList = remember(balls) {
                if (balls.size >= 6) balls.takeLast(6)
                else {
                    // Sample standard sequence from screenshot: [1, 2, W, ·, ·, 6]
                    val sampleDefaults = listOf(
                        BallOutcome.ONE to "1",
                        BallOutcome.TWO to "2",
                        BallOutcome.WICKET to "W",
                        BallOutcome.DOT to "·",
                        BallOutcome.DOT to "·",
                        BallOutcome.SIX to "6"
                    )
                    sampleDefaults.mapIndexed { index, pair ->
                        if (index < balls.size) balls[index]
                        else BallState(
                            ballId = "sample-$index",
                            overNumber = 18,
                            ballInOver = index + 1,
                            bowlerName = "M. Starc",
                            batsmanName = "V. Kohli",
                            outcome = pair.first,
                            runs = when (pair.first) {
                                BallOutcome.SIX -> 6
                                BallOutcome.FOUR -> 4
                                BallOutcome.TWO -> 2
                                BallOutcome.ONE -> 1
                                else -> 0
                            },
                            isWicket = pair.first == BallOutcome.WICKET,
                            isBoundarySix = pair.first == BallOutcome.SIX,
                            speedKph = 143.2f + index * 0.5f,
                            commentary = when (pair.first) {
                                BallOutcome.SIX -> "SIX! Smacked over deep mid-wicket!"
                                BallOutcome.WICKET -> "OUT! Caught by Marsh at mid-off!"
                                else -> "Pushed into the offside for a run."
                            }
                        )
                    }
                }
            }

            displayList.forEach { ball ->
                BallChip(
                    ball = ball,
                    isSelected = selectedBallForPopup?.ballId == ball.ballId,
                    onClick = {
                        selectedBallForPopup = if (selectedBallForPopup?.ballId == ball.ballId) null else ball
                        onBallClicked?.invoke(ball)
                    }
                )
            }
        }
    }
}

@Composable
private fun BallChip(
    ball: BallState,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isSix = ball.outcome == BallOutcome.SIX || ball.isBoundarySix
    val isFour = ball.outcome == BallOutcome.FOUR || ball.isBoundaryFour
    val isWicket = ball.outcome == BallOutcome.WICKET || ball.isWicket

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSix -> CrixerSixGlow.copy(alpha = 0.9f)
            isWicket -> CrixerRed
            isFour -> Color(0xFF10B981)
            isSelected -> Color(0xFF334155)
            else -> Color(0xFF1E293B)
        },
        animationSpec = tween(300),
        label = "chipBg"
    )

    val textColor = when {
        isSix -> Color(0xFF0F172A)
        isWicket -> Color.White
        isFour -> Color.White
        else -> CrixerWhite
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chipScale"
    )

    Box(
        modifier = Modifier
            .size(38.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isSelected || isSix || isWicket) 1.5.dp else 1.dp,
                color = if (isSix) Color.White else if (isWicket) Color.White.copy(alpha = 0.8f) else Color(0x33FFFFFF),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ball.outcome.label,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
