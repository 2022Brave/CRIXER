package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Match
import com.example.presentation.components.BallRail
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.components.LiquidGlassSegmentItem
import com.example.presentation.components.CrixerLiquidGlassSegmentedControl
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.presentation.viewmodel.MatchDetailTab
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun LiveMatchScreen(viewModel: CrixerViewModel, modifier: Modifier = Modifier) {
    val match by viewModel.selectedMatch.collectAsState()
    val tab by viewModel.matchDetailTab.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    if (match == null) {
        Box(Modifier.fillMaxSize().background(CrixerBlack), contentAlignment = Alignment.Center) {
            Text("Match data unavailable", color = CrixerTextSecondary)
        }
        return
    }

    val m = match!!
    val current = if (m.currentInningsNumber == 2) m.innings2 else m.innings1
    val isLive = m.status.name == "LIVE"

    Column(
        modifier.fillMaxSize().background(CrixerBlack).statusBarsPadding().padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.navigateTo(CrixerScreen.MATCHES) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CrixerWhite)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(m.title, color = CrixerWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (isLive) "LIVE NOW" else m.status.name,
                    color = if (isLive) CrixerRed else CrixerTextTertiary,
                    fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp
                )
            }
            IconButton(onClick = { viewModel.navigateTo(CrixerScreen.LEAN_BACK) }) {
                Icon(Icons.Filled.Tv, "Lean back", tint = CrixerWhite)
            }
        }

        Spacer(Modifier.height(8.dp))
        ScoreHero(m)
        Spacer(Modifier.height(14.dp))

        val tabs = listOf(
            LiquidGlassSegmentItem(MatchDetailTab.OVERVIEW, "OVERVIEW", accentColor = CrixerWhite),
            LiquidGlassSegmentItem(MatchDetailTab.COMMENTARY, "BALLS", accentColor = CrixerRed),
            LiquidGlassSegmentItem(MatchDetailTab.SCORECARD, "SCORECARD", accentColor = Color(0xFF38BDF8)),
            LiquidGlassSegmentItem(MatchDetailTab.STATS, "STATS", accentColor = Color(0xFF94A3B8))
        )
        CrixerLiquidGlassSegmentedControl(
            items = tabs,
            selectedKey = tab,
            onItemSelected = viewModel::setMatchDetailTab,
            isSimpleMode = settings.displayMode.name == "LITE",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        when (tab) {
            MatchDetailTab.SCORECARD -> ScorecardScreen(m, isSimpleMode = settings.displayMode.name == "SIMPLE")
            MatchDetailTab.COMMENTARY -> BallByBall(m)
            MatchDetailTab.STATS -> MatchStats(m)
            else -> Overview(m)
        }
    }
}

@Composable
private fun ScoreHero(m: Match) {
    LiquidGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        tintColor = Color(0xFF101722),
        isInteractive = false
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TeamHero(m.team1.shortName, m.team1.flagEmoji, m.innings1.runs.toString() + "/" + m.innings1.wickets, m.innings1.overs.takeIf { it > 0f }?.let { formatOvers(it) })
                Text("VS", color = CrixerTextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                val second = m.innings2
                TeamHero(
                    m.team2.shortName, m.team2.flagEmoji,
                    if (second != null) second.runs.toString() + "/" + second.wickets else "—",
                    if (second != null) second.overs.takeIf { it > 0f }?.let { formatOvers(it) } else null
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                m.situationSummary.ifBlank { m.resultSummary ?: m.scheduledDateText ?: "Match information" },
                color = CrixerTextSecondary, fontSize = 12.sp
            )
        }
    }
}

private fun formatOvers(overs: Float): String {
    val whole = overs.toInt()
    val balls = ((overs - whole) * 10f).toInt().coerceIn(0, 5)
    return "$whole.$balls"
}

@Composable
private fun TeamHero(name: String, emoji: String, score: String, overs: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 24.sp)
        Text(name, color = CrixerWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(score, color = CrixerWhite, fontSize = 25.sp, fontWeight = FontWeight.Black)
        Text(overs?.let { "($it)" } ?: "Overs unavailable", color = CrixerTextTertiary, fontSize = 10.sp)
    }
}
