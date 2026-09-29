package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BallOutcome
import com.example.domain.model.Match
import com.example.domain.model.MatchStatus
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite
import kotlinx.coroutines.delay

/**
 * CRIXER Premium Lean Back / AOD Television-Style Match Display:
 *
 * Symmetrical, balanced, score-first presentation:
 * - Scores ("187/4", "204/8") are treated as ONE indivisible visual unit and NEVER wrap.
 * - Equal visual weight between left team and right team.
 * - Clear vertical hierarchy utilizing available screen space intentionally.
 * - TV broadcast player information strip and current over rail.
 */
@Composable
fun LeanBackScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier
) {
    val match by viewModel.selectedMatch.collectAsState()
    var showControls by remember { mutableStateOf(true) }

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000)
            showControls = false
        }
    }

    if (match == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CrixerBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("No live match selected", color = CrixerTextSecondary)
        }
        return
    }

    val currentMatch = match!!
    val inn1 = currentMatch.innings1
    val inn2 = currentMatch.innings2

    // Team 1 is batting/chasing if innings 2 is underway
    val isTeam1Batting = currentMatch.currentInningsNumber == 2 && inn2 != null
    val team1Innings = if (isTeam1Batting) inn2 else inn1
    val team2Innings = if (isTeam1Batting) inn1 else inn2

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090E17),
                        Color(0xFF030508),
                        Color(0xFF060910)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Main Symmetrical TV Scoreboard Composition
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // =========================================================
            // 1. TOP: MATCH / VENUE HEADER & LIVE BADGE
            // =========================================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = "${currentMatch.title.uppercase()}   ·   ${currentMatch.venue.uppercase()}",
                    color = Color(0xFFA0AAB8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
                if (currentMatch.status == MatchStatus.LIVE) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CrixerRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================
            // 2. MAIN: LARGE TEAM IDENTITY + INDIVISIBLE SCORES
            // Left Team (1f) | Center VS / Situation (0.6f) | Right Team (1f)
            // =========================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x351E2838),
                                Color(0x18101824),
                                Color(0x28090D15)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x50FFFFFF),
                                Color(0x1538BDF8),
                                Color(0x35FFFFFF)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT TEAM SECTION
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Full Name + Flag & Short Name
                    Text(
                        text = currentMatch.team1.name,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentMatch.team1.flagEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentMatch.team1.shortName,
                            color = CrixerWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // CRITICAL: Indivisible Score — MUST NEVER WRAP (e.g. "187/4")
                    val leftRuns = team1Innings?.runs ?: 0
                    val leftWickets = team1Innings?.wickets ?: 0
                    Text(
                        text = "$leftRuns/$leftWickets",
                        color = CrixerWhite,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.5).sp,
                        maxLines = 1,
                        softWrap = false
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${team1Innings?.overs ?: 0.0f} OV",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                // CENTER VS / SITUATION PILL
                Column(
                    modifier = Modifier
                        .weight(0.75f)
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "VS",
                        color = Color(0x60FFFFFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (currentMatch.requiredRuns != null && currentMatch.remainingBalls != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x35EF4444))
                                .border(0.8.dp, Color(0x80EF4444), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NEED ${currentMatch.requiredRuns} IN ${currentMatch.remainingBalls}",
                                color = Color(0xFFFFB612),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // RIGHT TEAM SECTION
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    // Full Name + Flag & Short Name
                    Text(
                        text = currentMatch.team2.name,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = currentMatch.team2.shortName,
                            color = CrixerWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = currentMatch.team2.flagEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // CRITICAL: Indivisible Score — MUST NEVER WRAP (e.g. "204/8")
                    val rightRuns = team2Innings?.runs ?: 0
                    val rightWickets = team2Innings?.wickets ?: 0
                    Text(
                        text = "$rightRuns/$rightWickets",
                        color = CrixerWhite,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.5).sp,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${team2Innings?.overs ?: 0.0f} OV",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================
            // 3. MATCH STATE: Situation Line + Rates
            // =========================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = (currentMatch.resultSummary ?: currentMatch.situationSummary).uppercase(),
                    color = CrixerWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                if (currentMatch.requiredRunRate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "RRR ${currentMatch.requiredRunRate}   •   CRR ${currentMatch.currentRunRate}",
                        color = Color(0xFFFFB612),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // =========================================================
            // 4. PLAYERS: Symmetrical Broad Information Strip
            // Virat Kohli* (62) | Axar Patel (4) | Mitchell Starc (2/36)
            // =========================================================
            val activeBatters = (inn2 ?: inn1).batters.filter { it.isNotOut }
            val activeBowler = (inn2 ?: inn1).bowlers.firstOrNull()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x35141D2B))
                    .border(0.8.dp, Color(0x25FFFFFF), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Striker Batter
                if (activeBatters.isNotEmpty()) {
                    val b1 = activeBatters[0]
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = b1.name + if (b1.isOnStrike) "*" else "",
                            color = CrixerWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${b1.runs} (${b1.balls})",
                            color = Color(0xFFFFB612),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Non-Striker Batter
                if (activeBatters.size > 1) {
                    val b2 = activeBatters[1]
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = b2.name + if (b2.isOnStrike) "*" else "",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${b2.runs} (${b2.balls})",
                            color = CrixerTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Active Bowler
                if (activeBowler != null) {
                    Column(
                        modifier = Modifier.weight(1.1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = activeBowler.name,
                            color = CrixerWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${activeBowler.wickets}/${activeBowler.runsConceded} (${activeBowler.overs})",
                            color = Color(0xFF38BDF8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // =========================================================
            // 5. CURRENT OVER: Circular Ball Sequence (W  6  W  •  •  6)
            // =========================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "THIS OVER",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val activeBalls = (inn2 ?: inn1).balls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayBalls = activeBalls.takeLast(6)
                    displayBalls.forEach { ball ->
                        val (bgColor, textColor, borderStroke) = when {
                            ball.isWicket || ball.outcome == BallOutcome.WICKET ->
                                Triple(CrixerRed, Color.White, Color(0xFFEF4444))
                            ball.isBoundarySix || ball.outcome == BallOutcome.SIX ->
                                Triple(Color(0xFF8B5CF6), Color.White, Color(0xFFA78BFA))
                            ball.isBoundaryFour || ball.outcome == BallOutcome.FOUR ->
                                Triple(Color(0xFF10B981), Color.White, Color(0xFF34D399))
                            ball.outcome == BallOutcome.DOT ->
                                Triple(Color(0x301E293B), Color(0xFF94A3B8), Color(0x35FFFFFF))
                            else ->
                                Triple(Color(0xFF1E293B), CrixerWhite, Color(0x50FFFFFF))
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .border(1.dp, borderStroke, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ball.outcome.label,
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // =========================================================
        // AUTO-FADING CONTROLS OVERLAY (Exit Lean Back)
        // =========================================================
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC000000))
                    .border(0.8.dp, Color(0x40FFFFFF), RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo(CrixerScreen.MATCH_DETAIL) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Exit Lean Back",
                    tint = CrixerWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Exit Lean Back",
                    color = CrixerWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(CrixerScreen.MATCH_DETAIL) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xCC000000))
                    .border(0.8.dp, Color(0x40FFFFFF), CircleShape)
                    .testTag("exit_lean_back_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Lean Back",
                    tint = CrixerWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
