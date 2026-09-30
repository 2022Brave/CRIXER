package com.example.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppDisplayMode
import com.example.presentation.components.*
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.presentation.viewmodel.HomeTab
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun HomeScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier,
    teamCodeFilter: String? = null,
    sectionTitle: String? = null,
    fixedTab: HomeTab? = null
) {
    val live by viewModel.liveMatches.collectAsState()
    val completed by viewModel.completedMatches.collectAsState()
    val upcoming by viewModel.upcomingMatches.collectAsState()
    val tab by viewModel.homeTab.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val isPro = settings.displayMode == AppDisplayMode.PRO

    fun filter(list: List<com.example.domain.model.Match>) = list.filter {
        teamCodeFilter == null || it.team1.id == teamCodeFilter || it.team2.id == teamCodeFilter
    }
    val liveList = filter(live)
    val completedList = filter(completed)
    val upcomingList = filter(upcoming)
    val activeTab = fixedTab ?: tab

    LiquidGlassBackground(isSimpleMode = !isPro, modifier = modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CrixerOfficialLogo(size = 48.dp, showWordmark = true, showTagline = false, animated = false)
                LiquidGlassSurface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                    tintColor = if (isPro) Color(0xFF102033) else Color(0xFF11161E),
                    onClick = viewModel::toggleDisplayMode
                ) {
                    Text(
                        if (isPro) "PRO" else "LITE",
                        color = if (isPro) Color(0xFF7DD3FC) else CrixerTextSecondary,
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(sectionTitle ?: "Cricket, right now.", color = CrixerWhite, fontSize = 27.sp, fontWeight = FontWeight.Black)
            Text(
                if (sectionTitle != null) "India men's international cricket" else "Score first. Context when you need it.",
                color = CrixerTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(Modifier.height(16.dp))

            if (fixedTab == null) {
                CrixerLiquidGlassSegmentedControl(
                    items = listOf(
                        LiquidGlassSegmentItem(HomeTab.LIVE, "LIVE", liveList.isNotEmpty(), CrixerRed),
                        LiquidGlassSegmentItem(HomeTab.UPCOMING, "FIXTURES", accentColor = Color(0xFF38BDF8)),
                        LiquidGlassSegmentItem(HomeTab.COMPLETED, "RESULTS", accentColor = Color(0xFF64748B))
                    ),
                    selectedKey = activeTab,
                    onItemSelected = viewModel::setHomeTab,
                    isSimpleMode = !isPro,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            LazyColumn(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (fixedTab != null || activeTab != HomeTab.LIVE) {
                    val list = when (activeTab) {
                        HomeTab.LIVE -> liveList
                        HomeTab.UPCOMING -> upcomingList
                        HomeTab.COMPLETED -> completedList
                    }
                    item { SectionLabel(if (activeTab == HomeTab.UPCOMING) "NEXT UP" else if (activeTab == HomeTab.COMPLETED) "RECENT RESULTS" else "LIVE NOW") }
                    if (list.isEmpty()) item { EmptyState(activeTab) }
                    else items(list, key = { it.id }) { match ->
                        MatchCard(match, settings.displayMode, onClick = { viewModel.onMatchClicked(match) })
                    }
                } else {
                    item { SectionLabel(if (liveList.isEmpty()) "NO LIVE MATCHES" else "${liveList.size} LIVE ${if (liveList.size == 1) "MATCH" else "MATCHES"}") }
                    if (liveList.isEmpty()) item { EmptyState(HomeTab.LIVE) }
                    else items(liveList.take(3), key = { it.id }) { match ->
                        MatchCard(match, settings.displayMode, featured = true, onClick = { viewModel.onMatchClicked(match) })
                    }

                    if (upcomingList.isNotEmpty()) {
                        item { Spacer(Modifier.height(8.dp)); SectionLabel("NEXT UP") }
                        items(upcomingList.take(3), key = { "up-${it.id}" }) { match ->
                            MatchCard(match, settings.displayMode, onClick = { viewModel.onMatchClicked(match) })
                        }
                    }
                    if (completedList.isNotEmpty()) {
                        item { Spacer(Modifier.height(8.dp)); SectionLabel("RECENT RESULTS") }
                        items(completedList.take(3), key = { "done-${it.id}" }) { match ->
                            MatchCard(match, settings.displayMode, onClick = { viewModel.onMatchClicked(match) })
                        }
                    }
                    item { Spacer(Modifier.height(28.dp)) }
                }
            }
        }
    }
}

@Composable private fun SectionLabel(text: String) {
    Text(text, color = CrixerTextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp,
        modifier = Modifier.padding(start = 3.dp, bottom = 1.dp))
}

@Composable private fun EmptyState(tab: HomeTab) {
    LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false, tintColor = Color(0xFF0E141D)) {
        Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                when (tab) {
                    HomeTab.LIVE -> "Nothing live right now"
                    HomeTab.UPCOMING -> "No upcoming fixtures"
                    HomeTab.COMPLETED -> "No recent results"
                },
                color = CrixerWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text("CRIXER will show verified cricket data when a source is available.", color = CrixerTextSecondary, fontSize = 12.sp)
        }
    }
}
