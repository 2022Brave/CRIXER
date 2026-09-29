package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppDisplayMode
import com.example.domain.model.BallOutcome
import com.example.domain.model.Match
import com.example.domain.model.MatchStatus
import com.example.presentation.components.BallRail
import com.example.presentation.components.ContextualMilestoneCard
import com.example.presentation.components.LiquidGlassPulse
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.components.CrixerLiquidGlassSegmentedControl
import com.example.presentation.components.LiquidGlassSegmentItem
import com.example.presentation.components.SegmentDensity
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.Tv
import com.example.presentation.viewmodel.MatchDetailTab
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun LiveMatchScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier
) {
    val match by viewModel.selectedMatch.collectAsState()
    val matchDetailTab by viewModel.matchDetailTab.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val pulseState by viewModel.liquidGlassPulse.collectAsState()

    if (match == null) {
        Box(
            modifier = modifier.fillMaxSize().background(CrixerBlack),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Match data unavailable", color = CrixerTextSecondary)
        }
        return
    }

    val currentMatch = match!!
    val isSimpleMode = userSettings.displayMode == AppDisplayMode.SIMPLE

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CrixerBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(CrixerScreen.HOME) },
                modifier = Modifier.size(36.dp).testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CrixerWhite
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMatch.title,
                    color = CrixerWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentMatch.venue,
                    color = CrixerTextSecondary,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Lean Back Button (per V2: match-level entry point)
                Box(
                    modifier = Modifier
                        .testTag("match_lean_back_button")
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF162132))
                        .border(0.8.dp, Color(0x4038BDF8), RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(CrixerScreen.LEAN_BACK) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Lean Back",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lean Back",
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (currentMatch.status == MatchStatus.LIVE) {
                    Spacer(modifier = Modifier.width(6.dp))
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
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Score Section (Wrapped in LiquidGlassSurface if Immersive Mode)
        if (isSimpleMode) {
            SimpleScoreHeader(match = currentMatch)
        } else {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                pulseState = pulseState,
                isInteractive = false
            ) {
                ImmersiveScoreHeader(
                    match = currentMatch,
                    onPlayerClick = {
                        viewModel.loadPlayerProfile(it)
                        viewModel.setMatchDetailTab(MatchDetailTab.INSIGHTS)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub Tabs: Overview, Commentary, Scorecard, Stats, Insights (Unified Liquid Glass control)
        val matchTabItems = remember {
            listOf(
                LiquidGlassSegmentItem(
                    key = MatchDetailTab.OVERVIEW,
                    label = "Overview",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "match_tab_overview"
                ),
                LiquidGlassSegmentItem(
                    key = MatchDetailTab.COMMENTARY,
                    label = "Commentary",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "match_tab_commentary"
                ),
                LiquidGlassSegmentItem(
                    key = MatchDetailTab.SCORECARD,
                    label = "Scorecard",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "match_tab_scorecard"
                ),
                LiquidGlassSegmentItem(
                    key = MatchDetailTab.STATS,
                    label = "Stats",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "match_tab_stats"
                ),
                LiquidGlassSegmentItem(
                    key = MatchDetailTab.INSIGHTS,
                    label = "Insights",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "match_tab_insights"
                )
            )
        }

        CrixerLiquidGlassSegmentedControl(
            items = matchTabItems,
            selectedKey = matchDetailTab,
            onItemSelected = { viewModel.setMatchDetailTab(it) },
            isSimpleMode = isSimpleMode,
            density = SegmentDensity.COMPACT,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (matchDetailTab) {
                MatchDetailTab.OVERVIEW -> {
                    OverviewTabContent(
                        match = currentMatch,
                        viewModel = viewModel
                    )
                }
                MatchDetailTab.COMMENTARY -> {
                    CommentaryTabContent(match = currentMatch)
                }
                MatchDetailTab.SCORECARD -> {
                    ScorecardTabContent(match = currentMatch)
                }
                MatchDetailTab.STATS -> {
                    StatsTabContent(match = currentMatch)
                }
                MatchDetailTab.INSIGHTS -> {
                    InsightsTabContent(
                        match = currentMatch,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Live Simulation Action Bar (to test every ball counts, six pulse, and wicket response)
        LiveEventSimulationBar(onOutcome = { outcome -> viewModel.simulateDelivery(outcome) })
    }
}

@Composable
private fun SimpleScoreHeader(match: Match) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val inn1 = match.innings1
        val inn2 = match.innings2 ?: match.innings1

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = match.team1.shortName,
                    color = Color(0xFFA0AAB8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${inn2.runs}/${inn2.wickets}",
                        color = CrixerWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${inn2.overs} OV)",
                        color = Color(0xFF717D8F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = match.team2.shortName,
                    color = Color(0xFFA0AAB8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${inn1.runs}/${inn1.wickets}",
                        color = Color(0xFF94A3B8),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${inn1.overs} OV)",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = match.situationSummary.uppercase(),
                color = CrixerWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            if (match.requiredRunRate != null) {
                Text(
                    text = "RRR ${match.requiredRunRate} · CRR ${match.currentRunRate}",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(Color(0xFF1E2633))
        )
    }
}

@Composable
private fun ImmersiveScoreHeader(
    match: Match,
    onPlayerClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val inn1 = match.innings1
        val inn2 = match.innings2 ?: match.innings1

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team 1 (Chasing)
            Column(horizontalAlignment = Alignment.Start) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = match.team1.flagEmoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = match.team1.shortName, color = CrixerTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${inn2.runs}/${inn2.wickets}",
                    color = CrixerWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "(${inn2.overs} OV)",
                    color = CrixerTextSecondary,
                    fontSize = 12.sp
                )
            }

            // Team 2 (First Innings)
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = match.team2.flagEmoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = match.team2.shortName, color = CrixerTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${inn1.runs}/${inn1.wickets}",
                    color = CrixerWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "(${inn1.overs} OV)",
                    color = CrixerTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Match situation & run rates
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = match.situationSummary,
                color = CrixerWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (match.requiredRunRate != null) {
                    Text(
                        text = "RRR ${match.requiredRunRate}",
                        color = Color(0xFFFFB612),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "CRR ${match.currentRunRate}",
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Current Batters summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            inn2.batters.filter { it.isNotOut }.take(2).forEach { batter ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPlayerClick(batter.id) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF273549)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏏", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = batter.name,
                                color = CrixerWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (batter.isOnStrike) {
                                Text(text = "*", color = Color(0xFFFFB612), fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "${batter.runs} (${batter.balls}) · SR ${batter.strikeRate}",
                            color = CrixerTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTabContent(
    match: Match,
    viewModel: CrixerViewModel
) {
    val inn2 = match.innings2 ?: match.innings1

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Milestone Card (Kohli 16,000 ODI runs)
        if (match.activeInsight != null) {
            item {
                ContextualMilestoneCard(
                    insight = match.activeInsight,
                    onClick = {
                        viewModel.loadPlayerProfile(match.activeInsight.playerId)
                        viewModel.setMatchDetailTab(MatchDetailTab.INSIGHTS)
                    }
                )
            }
        }

        // Ball Rail (This Over)
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Box(modifier = Modifier.padding(14.dp)) {
                    BallRail(balls = inn2.balls)
                }
            }
        }

        // Match Moment Card
        if (match.activeMoment != null) {
            item {
                LiquidGlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    tintColor = if (match.activeMoment.isSix) Color(0xFF0C1929) else Color(0xFF1E1012),
                    isInteractive = false
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${match.activeMoment.overText} — ${match.activeMoment.title}",
                                color = if (match.activeMoment.isSix) CrixerSixGlow else CrixerRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Match Moment", color = CrixerTextSecondary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = match.activeMoment.description,
                            color = CrixerWhite,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Win Probability Bar
        item {
            WinProbabilityCard(match = match)
        }
    }
}

@Composable
private fun WinProbabilityCard(match: Match) {
    LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), isInteractive = false) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Win Probability", color = CrixerTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${match.team1.shortName} ${match.winProbabilityTeam1}%", color = Color(0xFFFF9933), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "${match.team2.shortName} ${match.winProbabilityTeam2}%", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(match.winProbabilityTeam1.toFloat())
                        .background(Color(0xFFFF9933))
                )
                Box(
                    modifier = Modifier
                        .weight(match.winProbabilityTeam2.toFloat())
                        .background(Color(0xFF002B7F))
                )
            }
        }
    }
}

@Composable
private fun CommentaryTabContent(match: Match) {
    val inn2 = match.innings2 ?: match.innings1
    val balls = inn2.balls.reversed()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(balls) { ball ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0E131C))
                    .padding(12.dp)
            ) {
                Text(
                    text = "${ball.overNumber}.${ball.ballInOver}",
                    color = Color(0xFFFFB612),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${ball.bowlerName} to ${ball.batsmanName}",
                            color = CrixerTextSecondary,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (ball.outcome) {
                                        BallOutcome.SIX -> CrixerSixGlow
                                        BallOutcome.WICKET -> CrixerRed
                                        BallOutcome.FOUR -> Color(0xFF10B981)
                                        else -> Color(0xFF1E293B)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = ball.outcome.label,
                                color = if (ball.outcome == BallOutcome.SIX) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ball.commentary,
                        color = CrixerWhite,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScorecardTabContent(match: Match) {
    ScorecardScreen(match = match)
}

@Composable
private fun StatsTabContent(match: Match) {
    MatchInsightsScreen(match = match)
}

@Composable
private fun InsightsTabContent(
    match: Match,
    viewModel: CrixerViewModel
) {
    val playerProfile by viewModel.selectedPlayerProfile.collectAsState()
    val insightsSubTab by viewModel.insightsSubTab.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val isSimpleMode = userSettings.displayMode == AppDisplayMode.SIMPLE

    Column(modifier = Modifier.fillMaxSize()) {
        val insightsTabItems = remember {
            listOf(
                LiquidGlassSegmentItem(
                    key = com.example.presentation.viewmodel.InsightsSubTab.PLAYER_INSIGHTS,
                    label = "Player Insights",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "insights_tab_player"
                ),
                LiquidGlassSegmentItem(
                    key = com.example.presentation.viewmodel.InsightsSubTab.MATCH_INSIGHTS,
                    label = "Match Insights",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "insights_tab_match"
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            CrixerLiquidGlassSegmentedControl(
                items = insightsTabItems,
                selectedKey = insightsSubTab,
                onItemSelected = { viewModel.setInsightsSubTab(it) },
                isSimpleMode = isSimpleMode,
                density = SegmentDensity.COMPACT,
                modifier = Modifier.width(280.dp)
            )
        }

        if (insightsSubTab == com.example.presentation.viewmodel.InsightsSubTab.PLAYER_INSIGHTS) {
            PlayerInsightsScreen(playerProfile = playerProfile, match = match)
        } else {
            MatchInsightsScreen(match = match)
        }
    }
}

@Composable
private fun LiveEventSimulationBar(
    onOutcome: (BallOutcome) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF101520))
            .border(1.dp, Color(0xFF1E2838), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Simulate:", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        listOf(
            BallOutcome.DOT to "·",
            BallOutcome.ONE to "1",
            BallOutcome.TWO to "2",
            BallOutcome.FOUR to "4",
            BallOutcome.SIX to "6",
            BallOutcome.WICKET to "W"
        ).forEach { (outcome, label) ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        when (outcome) {
                            BallOutcome.SIX -> CrixerSixGlow
                            BallOutcome.WICKET -> CrixerRed
                            BallOutcome.FOUR -> Color(0xFF10B981)
                            else -> Color(0xFF1E293B)
                        }
                    )
                    .clickable { onOutcome(outcome) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    color = if (outcome == BallOutcome.SIX) Color.Black else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
