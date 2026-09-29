package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.CrixerLiquidGlassSegmentedControl
import com.example.presentation.components.LiquidGlassSegmentItem
import com.example.presentation.components.SegmentDensity
import com.example.data.repository.AppDisplayMode
import com.example.presentation.components.CrixerOfficialLogo
import com.example.presentation.components.MatchCard
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.presentation.viewmodel.HomeTab
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

@Composable
fun HomeScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier,
    teamCodeFilter: String? = null,
    sectionTitle: String? = null
) {
    val liveMatches by viewModel.liveMatches.collectAsState()
    val completedMatches by viewModel.completedMatches.collectAsState()
    val upcomingMatches by viewModel.upcomingMatches.collectAsState()
    val homeTab by viewModel.homeTab.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val currentMatches = when (homeTab) {
        HomeTab.LIVE -> liveMatches
        HomeTab.COMPLETED -> completedMatches
        HomeTab.UPCOMING -> upcomingMatches
    }.filter { match ->
        (teamCodeFilter == null ||
            match.team1.id == teamCodeFilter ||
            match.team2.id == teamCodeFilter) &&
        (searchQuery.isEmpty() ||
            match.title.contains(searchQuery, ignoreCase = true) ||
            match.team1.name.contains(searchQuery, ignoreCase = true) ||
            match.team2.name.contains(searchQuery, ignoreCase = true))
    }

    val isSimpleMode = userSettings.displayMode == AppDisplayMode.SIMPLE

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        if (sectionTitle != null) {
            Text(
                text = sectionTitle,
                color = CrixerWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.4.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "India men's international cricket",
                color = CrixerTextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Top Header Bar with Official Liquid Glass Logo Mark
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.navigateTo(CrixerScreen.SPLASH) }
            ) {
                // Official Crixer Mark
                CrixerOfficialLogo(
                    size = 32.dp,
                    showWordmark = false,
                    showTagline = false,
                    animated = !isSimpleMode
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CRI",
                        color = CrixerWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.8.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "X",
                        color = CrixerRed,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.8.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "ER",
                        color = CrixerWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.8.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleSearch() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131A26))
                        .testTag("search_button")
                ) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.Close else Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = CrixerWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.navigateTo(CrixerScreen.SETTINGS) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131A26))
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = CrixerWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Search Bar (if active)
        AnimatedVisibility(visible = isSearchActive) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search teams, venues, tournaments...", color = CrixerTextSecondary, fontSize = 13.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrixerRed,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = CrixerWhite,
                    unfocusedTextColor = CrixerWhite,
                    cursorColor = CrixerRed
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Unified Pill-shaped Liquid Glass Segmented Control: LIVE | COMPLETED | UPCOMING
        val homeTabItems = remember {
            listOf(
                LiquidGlassSegmentItem(
                    key = HomeTab.LIVE,
                    label = "Live",
                    isLiveDot = true,
                    accentColor = Color(0xFFFF4D4D),
                    testTag = "home_tab_live"
                ),
                LiquidGlassSegmentItem(
                    key = HomeTab.COMPLETED,
                    label = "Completed",
                    isLiveDot = false,
                    accentColor = Color(0xFF64748B),
                    testTag = "home_tab_completed"
                ),
                LiquidGlassSegmentItem(
                    key = HomeTab.UPCOMING,
                    label = "Upcoming",
                    isLiveDot = false,
                    accentColor = Color(0xFF38BDF8),
                    testTag = "home_tab_upcoming"
                )
            )
        }

        CrixerLiquidGlassSegmentedControl(
            items = homeTabItems,
            selectedKey = homeTab,
            onItemSelected = { viewModel.setHomeTab(it) },
            isSimpleMode = isSimpleMode,
            density = SegmentDensity.REGULAR,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Matches list
        if (currentMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No matches found for '$searchQuery'" else "No matches in this category",
                    color = CrixerTextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = if (isSimpleMode) Arrangement.spacedBy(2.dp) else Arrangement.spacedBy(12.dp)
            ) {
                items(currentMatches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        displayMode = userSettings.displayMode,
                        onClick = { viewModel.onMatchClicked(match) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
