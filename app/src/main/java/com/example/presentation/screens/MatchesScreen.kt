package com.example.presentation.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.CrixerLiquidGlassSegmentedControl
import com.example.presentation.components.LiquidGlassSegmentItem
import com.example.presentation.components.SegmentDensity
import com.example.data.repository.AppDisplayMode
import com.example.domain.model.MatchFormat
import com.example.domain.model.MatchStatus
import com.example.presentation.components.MatchCard
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.presentation.viewmodel.MatchesFormatFilter
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

enum class MatchStatusFilter {
    ALL,
    LIVE,
    UPCOMING,
    RESULTS
}

@Composable
fun MatchesScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier
) {
    val liveMatches by viewModel.liveMatches.collectAsState()
    val completedMatches by viewModel.completedMatches.collectAsState()
    val upcomingMatches by viewModel.upcomingMatches.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val formatFilter by viewModel.formatFilter.collectAsState()

    var statusFilter by remember { mutableStateOf(MatchStatusFilter.ALL) }
    var localSearch by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val allMatches = liveMatches + upcomingMatches + completedMatches

    val filteredMatches = allMatches.filter { match ->
        // Format filter
        val formatMatches = when (formatFilter) {
            MatchesFormatFilter.ALL -> true
            MatchesFormatFilter.T20 -> match.format == MatchFormat.T20I
            MatchesFormatFilter.ODI -> match.format == MatchFormat.ODI
            MatchesFormatFilter.TEST -> match.format == MatchFormat.TEST
        }

        // Status filter
        val statusMatches = when (statusFilter) {
            MatchStatusFilter.ALL -> true
            MatchStatusFilter.LIVE -> match.status == MatchStatus.LIVE
            MatchStatusFilter.UPCOMING -> match.status == MatchStatus.UPCOMING
            MatchStatusFilter.RESULTS -> match.status == MatchStatus.COMPLETED
        }

        // Search text
        val searchMatches = localSearch.isEmpty() ||
            match.title.contains(localSearch, ignoreCase = true) ||
            match.venue.contains(localSearch, ignoreCase = true) ||
            match.team1.name.contains(localSearch, ignoreCase = true) ||
            match.team2.name.contains(localSearch, ignoreCase = true)

        formatMatches && statusMatches && searchMatches
    }

    val isSimpleMode = userSettings.displayMode == AppDisplayMode.SIMPLE

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MATCHES",
                    color = CrixerWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "International Fixtures & Series",
                    color = CrixerTextSecondary,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = {
                    isSearchExpanded = !isSearchExpanded
                    if (!isSearchExpanded) localSearch = ""
                },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF141A24))
                    .testTag("matches_search_button")
            ) {
                Icon(
                    imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Outlined.Search,
                    contentDescription = "Search Matches",
                    tint = CrixerWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Search Field
        if (isSearchExpanded) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = localSearch,
                onValueChange = { localSearch = it },
                placeholder = { Text("Search by team, series, venue...", color = CrixerTextTertiary, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("matches_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF141A24),
                    unfocusedContainerColor = Color(0xFF0F141D),
                    focusedBorderColor = CrixerRed,
                    unfocusedBorderColor = Color(0xFF222C3D),
                    focusedTextColor = CrixerWhite,
                    unfocusedTextColor = CrixerWhite
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // PRIMARY FILTER: [ ALL   LIVE   UPCOMING   RESULTS ]
        val statusFilterItems = remember {
            listOf(
                LiquidGlassSegmentItem(
                    key = MatchStatusFilter.ALL,
                    label = "All",
                    isLiveDot = false,
                    accentColor = Color(0xFF64748B),
                    testTag = "matches_status_all"
                ),
                LiquidGlassSegmentItem(
                    key = MatchStatusFilter.LIVE,
                    label = "Live",
                    isLiveDot = true,
                    accentColor = Color(0xFFFF4D4D),
                    testTag = "matches_status_live"
                ),
                LiquidGlassSegmentItem(
                    key = MatchStatusFilter.UPCOMING,
                    label = "Upcoming",
                    isLiveDot = false,
                    accentColor = Color(0xFF38BDF8),
                    testTag = "matches_status_upcoming"
                ),
                LiquidGlassSegmentItem(
                    key = MatchStatusFilter.RESULTS,
                    label = "Results",
                    isLiveDot = false,
                    accentColor = Color(0xFF94A3B8),
                    testTag = "matches_status_results"
                )
            )
        }

        CrixerLiquidGlassSegmentedControl(
            items = statusFilterItems,
            selectedKey = statusFilter,
            onItemSelected = { statusFilter = it },
            isSimpleMode = isSimpleMode,
            density = SegmentDensity.REGULAR,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // SECONDARY FILTER: [ ALL   T20I   ODI   TEST ]
        val formatFilterItems = remember {
            listOf(
                LiquidGlassSegmentItem(
                    key = MatchesFormatFilter.ALL,
                    label = "All",
                    accentColor = Color(0xFF64748B),
                    testTag = "matches_format_all"
                ),
                LiquidGlassSegmentItem(
                    key = MatchesFormatFilter.T20,
                    label = "T20I",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "matches_format_t20"
                ),
                LiquidGlassSegmentItem(
                    key = MatchesFormatFilter.ODI,
                    label = "ODI",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "matches_format_odi"
                ),
                LiquidGlassSegmentItem(
                    key = MatchesFormatFilter.TEST,
                    label = "Test",
                    accentColor = Color(0xFF38BDF8),
                    testTag = "matches_format_test"
                )
            )
        }

        CrixerLiquidGlassSegmentedControl(
            items = formatFilterItems,
            selectedKey = formatFilter,
            onItemSelected = { viewModel.setFormatFilter(it) },
            isSimpleMode = isSimpleMode,
            density = SegmentDensity.COMPACT,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Match List
        if (filteredMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matches match current filter",
                    color = CrixerTextTertiary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMatches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        displayMode = userSettings.displayMode,
                        onClick = { viewModel.onMatchClicked(match) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
