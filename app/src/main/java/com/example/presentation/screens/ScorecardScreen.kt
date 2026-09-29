package com.example.presentation.screens

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BatterScore
import com.example.domain.model.BowlerFigures
import com.example.presentation.components.CrixerLiquidGlassSegmentedControl
import com.example.presentation.components.LiquidGlassSegmentItem
import com.example.presentation.components.SegmentDensity
import com.example.domain.model.Match
import com.example.presentation.components.LiquidGlassPulse
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * CRIXER Full Match Scorecard with Rich Liquid Glass Background Environment.
 * Reference: liquidglassdesign.com/gallery/liquid-glass-concept
 *
 * Layered Composition:
 * - BACKGROUND: Near-black nocturnal stadium atmosphere
 * - LAYER 1: Subtle blurred stadium lighting
 * - LAYER 2: Large translucent Liquid Glass floating optical forms
 * - LAYER 3: Subtle team-color internal refraction (Saffron/Green & Gold/Navy)
 * - LAYER 4: Soft ambient highlights drifting slowly (8-20s period)
 * - FOREGROUND: Perfectly readable professional cricket scorecard
 *
 * Responsive to Match State:
 * - SIX/Boundary: brief radiant brightness/refraction flare
 * - WICKET: brief controlled depth/refraction response
 */
@Composable
fun ScorecardScreen(
    match: Match,
    pulseState: LiquidGlassPulse = LiquidGlassPulse.NONE,
    isSimpleMode: Boolean = false,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedTeamId by remember { mutableStateOf(match.team1.id) }

    val innings = if (selectedTeamId == match.team1.id) {
        match.innings2 ?: match.innings1
    } else {
        match.innings1
    }

    val teamName = if (selectedTeamId == match.team1.id) match.team1.name else match.team2.name
    val isTeam1Selected = selectedTeamId == match.team1.id

    // Ambient 14s slow drift for Liquid Glass background
    val infiniteTransition = rememberInfiniteTransition(label = "scorecardGlassAmbient")
    val ambientPhase by if (!reducedMotion && !isSimpleMode) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 14000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ambientGlassDrift"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    // Match event response (Boundary / Wicket)
    val eventPulse = remember { Animatable(0f) }
    LaunchedEffect(pulseState) {
        if (pulseState != LiquidGlassPulse.NONE && !reducedMotion) {
            eventPulse.snapTo(1f)
            eventPulse.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CrixerBlack)
    ) {
        // CONTINUOUS LAYERED LIQUID GLASS BACKGROUND ENVIRONMENT
        if (!isSimpleMode) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val phaseRad = ambientPhase * 2f * Math.PI.toFloat()
                val pulse = eventPulse.value

                // LAYER 0: Deep nocturnal stadium bowl gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF060B14),
                            Color(0xFF04070D),
                            Color(0xFF040A10)
                        ),
                        startY = 0f,
                        endY = h
                    )
                )

                // LAYER 1: Subtle blurred stadium floodlight glow
                val driftX1 = w * 0.08f * cos(phaseRad)
                val driftY1 = h * 0.04f * sin(phaseRad)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x2838BDF8).copy(alpha = (0.25f + pulse * 0.45f).coerceIn(0f, 1f)),
                            Color(0x0C1E293B),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.25f + driftX1, h * 0.12f + driftY1),
                        radius = w * 0.85f
                    ),
                    radius = w * 0.85f,
                    center = Offset(w * 0.25f + driftX1, h * 0.12f + driftY1),
                    blendMode = BlendMode.Screen
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x2038BDF8).copy(alpha = (0.20f + pulse * 0.35f).coerceIn(0f, 1f)),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.85f - driftX1, h * 0.18f - driftY1),
                        radius = w * 0.75f
                    ),
                    radius = w * 0.75f,
                    center = Offset(w * 0.85f - driftX1, h * 0.18f - driftY1),
                    blendMode = BlendMode.Screen
                )

                // LAYER 2 & 3: Large translucent Liquid Glass floating optical forms & team tints
                val glassPath1 = Path().apply {
                    val startY = h * 0.28f + (15f * sin(phaseRad))
                    moveTo(0f, startY)
                    cubicTo(
                        w * 0.35f, startY - 45f,
                        w * 0.65f, startY + 65f,
                        w, startY - 20f
                    )
                    lineTo(w, h * 0.68f)
                    cubicTo(
                        w * 0.70f, h * 0.72f,
                        w * 0.30f, h * 0.64f,
                        0f, h * 0.70f
                    )
                    close()
                }

                // Internal team refraction (Saffron/Emerald for IND, Gold/Navy for AUS)
                val primaryTint = if (pulseState == LiquidGlassPulse.WICKET) {
                    CrixerRed.copy(alpha = 0.35f * pulse)
                } else if (pulseState == LiquidGlassPulse.SIX) {
                    CrixerSixGlow.copy(alpha = 0.40f * pulse)
                } else {
                    Color(0x18FF9933) // Saffron warmth
                }

                val secondaryTint = Color(0x18002B7F) // Navy/Gold coolness

                drawPath(
                    path = glassPath1,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryTint,
                            Color(0x10FFFFFF),
                            secondaryTint,
                            Color.Transparent
                        ),
                        start = Offset(0f, h * 0.25f),
                        end = Offset(w, h * 0.70f)
                    ),
                    blendMode = BlendMode.Screen
                )

                // LAYER 4: Soft ambient refractive highlights along glass contour
                drawPath(
                    path = glassPath1,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.35f + pulse * 0.4f),
                            Color(0x6038BDF8),
                            Color.Transparent
                        ),
                        start = Offset(driftX1, h * 0.30f),
                        end = Offset(w + driftX1, h * 0.65f)
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f)
                )
            }
        }

        // FOREGROUND: HIGHLY READABLE SCORECARD CONTENT
        Column(modifier = Modifier.fillMaxSize()) {
            // TEAM SELECTOR SWITCH: Physical movable Liquid Glass pill
            ScorecardTeamSwitch(
                team1ShortName = match.team1.shortName,
                team2ShortName = match.team2.shortName,
                isTeam1Selected = isTeam1Selected,
                isSimpleMode = isSimpleMode,
                onSelectTeam = { isTeam1 ->
                    selectedTeamId = if (isTeam1) match.team1.id else match.team2.id
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Summary Card (Liquid Glass with crisp score hierarchy)
                item {
                    val computedRunRate = if (innings.overs > 0f) (innings.runs / innings.overs) else 0f
                    ScorecardSummaryHeader(
                        teamName = teamName,
                        runs = innings.runs,
                        wickets = innings.wickets,
                        overs = innings.overs,
                        runRate = computedRunRate,
                        isSimpleMode = isSimpleMode
                    )
                }

                // Batting Table Section
                item {
                    Text(
                        text = "Batting".uppercase(),
                        color = Color(0xFFA0AAB8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )

                    // Batting Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(if (isSimpleMode) Color(0xFF131A26) else Color(0x70131E2D))
                            .border(
                                width = 0.6.dp,
                                color = if (isSimpleMode) Color(0xFF222F42) else Color(0x35FFFFFF),
                                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Batter", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2.4f))
                        Text(text = "R", color = CrixerWhite, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                        Text(text = "B", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                        Text(text = "4s", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                        Text(text = "6s", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                        Text(text = "SR", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    }
                }

                items(innings.batters, key = { it.id }) { batter ->
                    BattingRow(batter = batter, isSimpleMode = isSimpleMode)
                }

                // Extras & Total Pod (Liquid Glass Surface)
                item {
                    ScorecardExtrasTotalPod(
                        extras = innings.extras,
                        runs = innings.runs,
                        wickets = innings.wickets,
                        overs = innings.overs,
                        isSimpleMode = isSimpleMode
                    )
                }

                // Fall of Wickets
                if (innings.fallOfWickets.isNotEmpty()) {
                    item {
                        Text(
                            text = "Fall of Wickets".uppercase(),
                            color = Color(0xFFA0AAB8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSimpleMode) Color(0xFF101520) else Color(0x600F1724))
                                .border(
                                    width = 0.6.dp,
                                    color = if (isSimpleMode) Color(0xFF1E2838) else Color(0x25FFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            innings.fallOfWickets.forEach { fow ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${fow.score}/${fow.wicketNumber}",
                                        color = Color(0xFFFFB612),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "${fow.over} ov",
                                        color = CrixerTextSecondary,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = fow.playerOut,
                                        color = CrixerTextTertiary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Bowling Table Section
                if (innings.bowlers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bowling".uppercase(),
                            color = Color(0xFFA0AAB8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                .background(if (isSimpleMode) Color(0xFF131A26) else Color(0x70131E2D))
                                .border(
                                    width = 0.6.dp,
                                    color = if (isSimpleMode) Color(0xFF222F42) else Color(0x35FFFFFF),
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Bowler", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2.4f))
                            Text(text = "O", color = CrixerWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text(text = "M", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                            Text(text = "R", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text(text = "W", color = Color(0xFFFFB612), fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text(text = "Econ", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                        }
                    }

                    items(innings.bowlers, key = { it.id }) { bowler ->
                        BowlingRow(bowler = bowler, isSimpleMode = isSimpleMode)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Movable Liquid Glass Team Selector Switch (IND vs AUS):
 * Powered by CrixerLiquidGlassSegmentedControl for unified physical interaction language.
 */
@Composable
private fun ScorecardTeamSwitch(
    team1ShortName: String,
    team2ShortName: String,
    isTeam1Selected: Boolean,
    isSimpleMode: Boolean,
    onSelectTeam: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(team1ShortName, team2ShortName) {
        listOf(
            LiquidGlassSegmentItem(
                key = true,
                label = team1ShortName,
                accentColor = Color(0xFFFF9933),
                testTag = "scorecard_team_1"
            ),
            LiquidGlassSegmentItem(
                key = false,
                label = team2ShortName,
                accentColor = Color(0xFF38BDF8),
                testTag = "scorecard_team_2"
            )
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CrixerLiquidGlassSegmentedControl(
            items = items,
            selectedKey = isTeam1Selected,
            onItemSelected = onSelectTeam,
            isSimpleMode = isSimpleMode,
            density = SegmentDensity.COMPACT,
            modifier = Modifier.width(204.dp)
        )
    }
}

@Composable
private fun ScorecardSummaryHeader(
    teamName: String,
    runs: Int,
    wickets: Int,
    overs: Float,
    runRate: Float,
    isSimpleMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSimpleMode) Color(0xFF0F1522)
                else Color(0x600C1624)
            )
            .border(
                width = 0.8.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x60FFFFFF),
                        Color(0x2038BDF8),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = teamName,
                    color = Color(0xFFA0AAB8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$runs/$wickets",
                        color = CrixerWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "($overs OV)",
                        color = Color(0xFF717D8F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "RUN RATE",
                    color = Color(0xFFA0AAB8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$runRate",
                    color = Color(0xFFFFB612),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun BattingRow(batter: BatterScore, isSimpleMode: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSimpleMode) Color(0xFF0C1018) else Color(0x400C131E))
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(2.4f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = batter.name,
                        color = CrixerWhite,
                        fontSize = 13.sp,
                        fontWeight = if (batter.isNotOut) FontWeight.Bold else FontWeight.Normal
                    )
                    if (batter.isOnStrike) {
                        Text(text = " *", color = Color(0xFFFFB612), fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                }
                if (batter.dismissalInfo != null) {
                    Text(
                        text = batter.dismissalInfo,
                        color = CrixerTextTertiary,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            Text(text = "${batter.runs}", color = CrixerWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
            Text(text = "${batter.balls}", color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
            Text(text = "${batter.fours}", color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
            Text(text = "${batter.sixes}", color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
            Text(text = "${batter.strikeRate}", color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
        }
        HorizontalDivider(color = Color(0xFF141C28), thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ScorecardExtrasTotalPod(
    extras: Int,
    runs: Int,
    wickets: Int,
    overs: Float,
    isSimpleMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSimpleMode) Color(0xFF101520) else Color(0x600F1726))
            .border(
                width = 0.6.dp,
                color = if (isSimpleMode) Color(0xFF1E2838) else Color(0x25FFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Extras", color = CrixerTextSecondary, fontSize = 13.sp)
            Text(text = "$extras", color = CrixerWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = Color(0xFF192230), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "TOTAL", color = Color(0xFFA0AAB8), fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(
                text = "$runs/$wickets ($overs)",
                color = CrixerWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun BowlingRow(bowler: BowlerFigures, isSimpleMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSimpleMode) Color(0xFF0C1018) else Color(0x400C131E))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = bowler.name, color = CrixerWhite, fontSize = 13.sp, fontWeight = FontWeight.Normal, modifier = Modifier.weight(2.4f))
        Text(text = "${bowler.overs}", color = CrixerWhite, fontSize = 12.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
        Text(text = "${bowler.maidens}", color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
        Text(text = "${bowler.runsConceded}", color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
        Text(text = "${bowler.wickets}", color = Color(0xFFFFB612), fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
        Text(text = "${bowler.economy}", color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
    }
}
