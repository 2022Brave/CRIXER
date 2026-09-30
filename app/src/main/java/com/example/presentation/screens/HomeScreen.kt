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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppDisplayMode
import com.example.presentation.components.CrixerLiquidGlassSegmentedControl
import com.example.presentation.components.CrixerOfficialLogo
import com.example.presentation.components.LiquidGlassSegmentItem
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.components.MatchCard
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.presentation.viewmodel.HomeTab
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val activeTab = fixedTab ?: tab
    val matches = when (activeTab) {
        HomeTab.LIVE -> live
        HomeTab.COMPLETED -> completed
        HomeTab.UPCOMING -> upcoming
    }.filter { match ->
        teamCodeFilter == null || match.team1.id == teamCodeFilter || match.team2.id == teamCodeFilter
    }
    val isPro = settings.displayMode == AppDisplayMode.IMMERSIVE

    Box(
        modifier.fillMaxSize().background(
            Brush.verticalGradient(
                if (isPro) listOf(Color(0xFF08111F), Color(0xFF05080D), CrixerBlack)
                else listOf(CrixerBlack, CrixerBlack)
            )
        )
    ) {
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CrixerOfficialLogo(size = 50.dp, showWordmark = true, showTagline = false, animated = false)
                ModePill(isPro, viewModel::toggleDisplayMode)
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = sectionTitle ?: if (activeTab == HomeTab.LIVE) "Cricket, right now." else "Cricket, without the noise.",
                color = CrixerWhite, fontSize = 26.sp, fontWeight = FontWeight.Black
            )
            Text(
                text = if (sectionTitle != null) "India men's international cricket"
                else LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, d MMM")),
                color = CrixerTextSecondary, fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(Modifier.height(16.dp))
            val tabs = listOf(
                LiquidGlassSegmentItem(HomeTab.LIVE, "LIVE", accentColor = CrixerRed),
                LiquidGlassSegmentItem(HomeTab.UPCOMING, "FIXTURES", accentColor = Color(0xFF38BDF8)),
                LiquidGlassSegmentItem(HomeTab.COMPLETED, "RESULTS", accentColor = Color(0xFF64748B))
            )
            CrixerLiquidGlassSegmentedControl(
                items = tabs, selectedKey = activeTab,
                onItemSelected = { if (fixedTab == null) viewModel.setHomeTab(it) },
                isSimpleMode = !isPro, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            if (matches.isEmpty()) {
                EmptyState(
                    title = when (activeTab) {
                        HomeTab.LIVE -> "Nothing live"
                        HomeTab.UPCOMING -> "No fixtures available"
                        HomeTab.COMPLETED -> "No recent results"
                    },
                    body = "CRIXER will show verified cricket data here when a source is available."
                )
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(if (isPro) 12.dp else 8.dp)
                ) {
                    item {
                        SectionLabel(
                            if (activeTab == HomeTab.LIVE)
                                matches.size.toString() + " MATCH" + if (matches.size == 1) "" else "ES" + " LIVE"
                            else if (activeTab == HomeTab.UPCOMING) "NEXT UP" else "LATEST RESULTS"
                        )
                    }
                    items(matches, key = { it.id }) { match ->
                        MatchCard(match, settings.displayMode, onClick = { viewModel.onMatchClicked(match) })
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ModePill(isPro: Boolean, onClick: () -> Unit) {
    LiquidGlassSurface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        tintColor = if (isPro) Color(0xFF102033) else Color(0xFF11161E),
        onClick = onClick
    ) {
        Text(
            text = if (isPro) "PRO" else "LITE",
            color = if (isPro) Color(0xFF7DD3FC) else CrixerTextSecondary,
            fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text, color = CrixerTextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 1.3.sp, modifier = Modifier.padding(start = 3.dp, bottom = 2.dp)
    )
}

@Composable
private fun EmptyState(title: String, body: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = CrixerWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                Text(body, color = CrixerTextSecondary, fontSize = 12.sp)
            }
        }
    }
}
