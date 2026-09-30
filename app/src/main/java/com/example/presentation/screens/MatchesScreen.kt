package com.example.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.MatchFormat
import com.example.domain.model.MatchStatus
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.components.MatchCard
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.presentation.viewmodel.MatchesFormatFilter
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun MatchesScreen(viewModel: CrixerViewModel, modifier: Modifier = Modifier) {
    val live by viewModel.liveMatches.collectAsState()
    val completed by viewModel.completedMatches.collectAsState()
    val upcoming by viewModel.upcomingMatches.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val format by viewModel.formatFilter.collectAsState()
    var status by remember { mutableStateOf(MatchStatusFilter.ALL) }
    val all = live + upcoming + completed
    val filtered = all.filter {
        when (status) {
            MatchStatusFilter.ALL -> true
            MatchStatusFilter.LIVE -> it.status == MatchStatus.LIVE
            MatchStatusFilter.UPCOMING -> it.status == MatchStatus.UPCOMING
            MatchStatusFilter.RESULTS -> it.status == MatchStatus.COMPLETED
        }
    }.filter {
        when (format) {
            MatchesFormatFilter.ALL -> true
            MatchesFormatFilter.T20 -> it.format == MatchFormat.T20I
            MatchesFormatFilter.ODI -> it.format == MatchFormat.ODI
            MatchesFormatFilter.TEST -> it.format == MatchFormat.TEST
        }
    }.distinctBy { it.id }

    Column(modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        Text("MATCHES", color = CrixerWhite, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text("Live • Fixtures • Results", color = CrixerTextSecondary, fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(18.dp))
        FilterRow(
            listOf("ALL", "LIVE", "FIXTURES", "RESULTS"),
            when (status) {
                MatchStatusFilter.ALL -> "ALL"
                MatchStatusFilter.LIVE -> "LIVE"
                MatchStatusFilter.UPCOMING -> "FIXTURES"
                MatchStatusFilter.RESULTS -> "RESULTS"
            }
        ) {
            status = when (it) {
                "LIVE" -> MatchStatusFilter.LIVE
                "FIXTURES" -> MatchStatusFilter.UPCOMING
                "RESULTS" -> MatchStatusFilter.RESULTS
                else -> MatchStatusFilter.ALL
            }
        }
        Spacer(Modifier.height(10.dp))
        FilterRow(
            listOf("ALL", "T20", "ODI", "TEST"),
            when (format) {
                MatchesFormatFilter.ALL -> "ALL"
                MatchesFormatFilter.T20 -> "T20"
                MatchesFormatFilter.ODI -> "ODI"
                MatchesFormatFilter.TEST -> "TEST"
            }
        ) {
            viewModel.setFormatFilter(
                when (it) {
                    "T20" -> MatchesFormatFilter.T20
                    "ODI" -> MatchesFormatFilter.ODI
                    "TEST" -> MatchesFormatFilter.TEST
                    else -> MatchesFormatFilter.ALL
                }
            )
        }
        Spacer(Modifier.height(14.dp))
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            item {
                Text(
                    filtered.size.toString() + " MATCHES",
                    color = CrixerTextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp, modifier = Modifier.padding(start = 3.dp, bottom = 2.dp)
                )
            }
            items(filtered, key = { it.id }) { match ->
                MatchCard(match, settings.displayMode, onClick = { viewModel.onMatchClicked(match) })
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

private enum class MatchStatusFilter { ALL, LIVE, UPCOMING, RESULTS }

@Composable
private fun FilterRow(items: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
        items.forEach { label ->
            val active = label == selected
            LiquidGlassSurface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                tintColor = if (active) Color(0xFF162436) else Color(0xFF0C1118),
                onClick = { onSelected(label) }
            ) {
                Text(
                    label, color = if (active) CrixerWhite else CrixerTextSecondary,
                    fontSize = 10.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
