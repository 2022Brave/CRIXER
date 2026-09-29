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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.presentation.components.MatchCard
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

data class FollowableTeam(
    val code: String,
    val name: String,
    val flag: String,
    val ranking: String
)

data class FollowablePlayer(
    val id: String,
    val name: String,
    val team: String,
    val role: String,
    val stat: String
)

@Composable
fun FollowingScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier
) {
    val followedTeams by viewModel.followedTeams.collectAsState()
    val followedPlayers by viewModel.followedPlayers.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val allMatches = viewModel.getAllMatches()

    val availableTeams = remember {
        listOf(
            FollowableTeam("IND", "India", "🇮🇳", "Rank #1"),
            FollowableTeam("AUS", "Australia", "🇦🇺", "Rank #2"),
            FollowableTeam("ENG", "England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "Rank #3"),
            FollowableTeam("SA", "South Africa", "🇿🇦", "Rank #4"),
            FollowableTeam("NZ", "New Zealand", "🇳🇿", "Rank #5"),
            FollowableTeam("PAK", "Pakistan", "🇵🇰", "Rank #6"),
            FollowableTeam("WI", "West Indies", "🌴", "Rank #7")
        )
    }

    val availablePlayers = remember {
        listOf(
            FollowablePlayer("ind-3", "Virat Kohli", "IND", "Batter", "15,986 ODI runs"),
            FollowablePlayer("ind-1", "Rohit Sharma", "IND", "Batter", "10,866 ODI runs"),
            FollowablePlayer("aus-1", "Travis Head", "AUS", "All-Rounder", "SR 141.2 T20"),
            FollowablePlayer("ind-b1", "Jasprit Bumrah", "IND", "Fast Bowler", "152 Wkts (Econ 4.6)"),
            FollowablePlayer("aus-7", "Pat Cummins", "AUS", "Pacer / Capt", "148 ODI Wkts"),
            FollowablePlayer("sa-1", "Heinrich Klaasen", "SA", "Wk-Batter", "SR 178.4 T20")
        )
    }

    val personalizedMatches = allMatches.filter { match ->
        followedTeams.contains(match.team1.shortName) ||
            followedTeams.contains(match.team1.id) ||
            followedTeams.contains(match.team2.shortName) ||
            followedTeams.contains(match.team2.id) ||
            match.isFollowed
    }

    var selectedSection by remember { mutableStateOf("FEED") } // FEED, TEAMS, PLAYERS, ALERTS

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FOLLOWING",
                    color = CrixerWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "${followedTeams.size} Teams • ${followedPlayers.size} Players Tracked",
                    color = CrixerTextSecondary,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF161F2E))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alerts",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Live Alerts ON",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section Tabs: FEED, TEAMS, PLAYERS, ALERTS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0D121B))
                .border(0.5.dp, Color(0xFF1E2838), RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("FEED", "TEAMS", "PLAYERS", "ALERTS").forEach { tab ->
                val isSelected = selectedSection == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isSelected) Color(0xFF1E293B) else Color.Transparent)
                        .clickable { selectedSection = tab }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) CrixerWhite else CrixerTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedSection) {
            "FEED" -> {
                if (personalizedMatches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Follow teams or players to get a personalized live match feed",
                            color = CrixerTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "MATCHES FOR YOUR TEAMS",
                                color = CrixerTextTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        items(personalizedMatches, key = { it.id }) { match ->
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

            "TEAMS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "INTERNATIONAL SQUADS",
                            color = CrixerTextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    items(availableTeams, key = { it.code }) { team ->
                        val isFollowed = followedTeams.contains(team.code)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F141E))
                                .border(0.6.dp, if (isFollowed) Color(0x6038BDF8) else Color(0xFF1E2838), RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleFollowTeam(team.code) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = team.flag, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = team.name,
                                        color = CrixerWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${team.code} • ${team.ranking}",
                                        color = CrixerTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isFollowed) Color(0xFF0284C7) else Color(0xFF1E293B))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isFollowed) "Following" else "+ Follow",
                                    color = CrixerWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            "PLAYERS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "MARQUEE PLAYERS",
                            color = CrixerTextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    items(availablePlayers, key = { it.id }) { player ->
                        val isFollowed = followedPlayers.contains(player.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F141E))
                                .border(0.6.dp, if (isFollowed) Color(0x6038BDF8) else Color(0xFF1E2838), RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleFollowPlayer(player.id) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1A2332)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = player.name,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = player.name,
                                        color = CrixerWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${player.team} • ${player.role} • ${player.stat}",
                                        color = CrixerTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isFollowed) Color(0xFF0284C7) else Color(0xFF1E293B))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isFollowed) "Following" else "+ Follow",
                                    color = CrixerWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            "ALERTS" -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "LIVE MATCH NOTIFICATION TRIGGERS",
                        color = CrixerTextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    AlertToggleRow(
                        title = "Fall of Wicket",
                        subtitle = "Instant alert when a wicket falls for your followed teams",
                        checked = userSettings.notifyMatchEvents,
                        onCheckedChange = { viewModel.toggleNotifyEvents(it) }
                    )

                    AlertToggleRow(
                        title = "Player Milestones (50 / 100)",
                        subtitle = "Alert when a followed batter reaches 50 or 100",
                        checked = userSettings.notifyMilestones,
                        onCheckedChange = { viewModel.toggleNotifyMilestones(it) }
                    )

                    AlertToggleRow(
                        title = "Final Match Result",
                        subtitle = "Match conclusion, winning team, and player of the match",
                        checked = userSettings.notifyResults,
                        onCheckedChange = { viewModel.toggleNotifyResults(it) }
                    )

                    AlertToggleRow(
                        title = "Ball-by-ball Haptic Pulse",
                        subtitle = "Tactile confirmation on 4s, 6s and dismissals",
                        checked = userSettings.hapticFeedback,
                        onCheckedChange = { viewModel.toggleHaptics(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F141E))
            .border(0.5.dp, Color(0xFF1E2838), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                color = CrixerWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = CrixerTextSecondary,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CrixerWhite,
                checkedTrackColor = CrixerRed,
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}
