package com.example.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Match
import com.example.presentation.components.LiquidGlassPulse
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

import androidx.compose.foundation.layout.statusBarsPadding

enum class GlanceVisualState {
    DEFAULT_AOD,
    MOMENT_SIX,
    MOMENT_WICKET,
    LOCK_SCREEN_PILL
}

@Composable
fun CricketGlanceScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier
) {
    val liveMatches by viewModel.liveMatches.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    var glanceState by remember { mutableStateOf(GlanceVisualState.DEFAULT_AOD) }
    var selectedMatchIndex by remember { mutableStateOf(0) }

    val currentMatch = if (liveMatches.isNotEmpty()) liveMatches[selectedMatchIndex % liveMatches.size] else null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CrixerBlack)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(CrixerScreen.HOME) },
                modifier = Modifier.size(36.dp).testTag("glance_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CrixerWhite
                )
            }

            Text(
                text = "Cricket Glance & AOD",
                color = CrixerWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // State Simulator Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF131924))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                GlanceVisualState.DEFAULT_AOD to "Default",
                GlanceVisualState.MOMENT_SIX to "SIX! Event",
                GlanceVisualState.MOMENT_WICKET to "Wicket!",
                GlanceVisualState.LOCK_SCREEN_PILL to "Widget Pill"
            ).forEach { (state, title) ->
                val isSelected = glanceState == state
                Box(
                    modifier = Modifier
                        .testTag("glance_tab_${title.lowercase()}")
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF273549) else Color.Transparent)
                        .clickable { glanceState = state }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) CrixerWhite else CrixerTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simulated Phone Device Frame (Always-On Display / Lock Screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF030406))
                .border(2.dp, Color(0xFF1E2838), RoundedCornerShape(28.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentMatch != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Clock
                    Text(
                        text = "5:42",
                        color = Color(0xFFE2E8F0),
                        fontSize = 54.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tue, 23 Sep",
                        color = CrixerTextSecondary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Glance Display Content based on State
                    when (glanceState) {
                        GlanceVisualState.DEFAULT_AOD -> {
                            DefaultAodContent(match = currentMatch)
                        }
                        GlanceVisualState.MOMENT_SIX -> {
                            KeyMomentSixContent(match = currentMatch)
                        }
                        GlanceVisualState.MOMENT_WICKET -> {
                            KeyMomentWicketContent(match = currentMatch)
                        }
                        GlanceVisualState.LOCK_SCREEN_PILL -> {
                            LockScreenPillContent(
                                match = currentMatch,
                                onClick = { viewModel.navigateTo(CrixerScreen.MATCH_DETAIL) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Multi-match indicator dots
                    if (liveMatches.size > 1) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.clickable { selectedMatchIndex++ }
                        ) {
                            liveMatches.forEachIndexed { idx, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (idx == selectedMatchIndex % liveMatches.size) Color(0xFF38BDF8) else Color(0xFF334155))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Tap dots to switch live matches", color = CrixerTextTertiary, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Glance Settings Controls (per spec screenshot 04/08)
        LiquidGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            isInteractive = false
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Cricket Glance", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Show live match scores on Always-On Display", color = CrixerTextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = userSettings.cricketGlanceEnabled,
                        onCheckedChange = { viewModel.toggleCricketGlance(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CrixerRed
                        )
                    )
                }

                HorizontalDivider(color = Color(0xFF1E2838), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Update on key moments", color = CrixerWhite, fontSize = 13.sp)
                    Switch(
                        checked = userSettings.notifyMatchEvents,
                        onCheckedChange = { viewModel.toggleNotifyEvents(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CrixerRed
                        )
                    )
                }

                HorizontalDivider(color = Color(0xFF1E2838), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Haptic feedback", color = CrixerWhite, fontSize = 13.sp)
                    Switch(
                        checked = userSettings.hapticFeedback,
                        onCheckedChange = { viewModel.toggleHaptics(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CrixerRed
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DefaultAodContent(match: Match) {
    val inn = match.innings2 ?: match.innings1
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "${match.team1.shortName} vs ${match.team2.shortName} · ${match.title}", color = CrixerTextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = match.team1.flagEmoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = match.team1.shortName, color = CrixerWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = "${inn.runs}/${inn.wickets}",
            color = CrixerWhite,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black
        )
        Text(text = "${inn.overs} OV", color = CrixerTextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = match.situationSummary,
            color = Color(0xFFFFB612),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun KeyMomentSixContent(match: Match) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF071424))
            .border(1.5.dp, CrixerSixGlow.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CrixerSixGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "6", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "SIX!", color = CrixerSixGlow, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Text(text = "Kohli over long-on", color = CrixerWhite, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "IND 193/4", color = CrixerWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(text = "18.3 OV", color = CrixerTextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Need 12 from 9", color = Color(0xFFFFB612), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun KeyMomentWicketContent(match: Match) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E0E12))
            .border(1.5.dp, CrixerRed.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CrixerRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "W", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "WICKET!", color = CrixerRed, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Text(text = "SKY out. 31 (16)", color = CrixerWhite, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "IND 193/5", color = CrixerWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(text = "18.4 OV", color = CrixerTextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Need 12 from 8", color = Color(0xFFFFB612), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LockScreenPillContent(
    match: Match,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x99101622))
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = match.team1.flagEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${match.team1.shortName} 193/5 (18.4)", color = CrixerWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Need 12 from 8", color = Color(0xFFFFB612), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
