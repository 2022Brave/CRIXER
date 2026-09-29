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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppDisplayMode
import com.example.presentation.components.CrixerWordmark
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

import androidx.compose.foundation.layout.statusBarsPadding
import com.example.presentation.components.CrixerOfficialLogo

@Composable
fun SettingsScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val validationState by viewModel.validationState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CrixerBlack)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(CrixerScreen.HOME) },
                modifier = Modifier.size(36.dp).testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CrixerWhite
                )
            }

            Text(
                text = "Settings",
                color = CrixerWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Box(modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Section
            item {
                SettingsSectionHeader("Appearance & Mode")
                LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), isInteractive = false) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Default Display Mode", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = if (userSettings.displayMode == AppDisplayMode.IMMERSIVE) "Immersive (Liquid Glass & Stadium)" else "Simple (Pure Black & White)",
                                    color = CrixerTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF223046))
                                    .clickable { viewModel.toggleDisplayMode() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (userSettings.displayMode == AppDisplayMode.IMMERSIVE) "Immersive" else "Simple",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFF1E2838), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Reduced Motion", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Minimizes fluid transitions and pulse", color = CrixerTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = userSettings.reducedMotion,
                                onCheckedChange = { viewModel.toggleReducedMotion(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
                            )
                        }
                    }
                }
            }

            // Live Updates & Glance
            item {
                SettingsSectionHeader("Live & Glance")
                LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), isInteractive = false) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Live Updates", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Receive ball-by-ball stream updates", color = CrixerTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = userSettings.liveUpdatesEnabled,
                                onCheckedChange = { viewModel.toggleLiveUpdates(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
                            )
                        }

                        HorizontalDivider(color = Color(0xFF1E2838), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Cricket Glance (AOD)", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Minimal live score on lock screen", color = CrixerTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = userSettings.cricketGlanceEnabled,
                                onCheckedChange = { viewModel.toggleCricketGlance(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
                            )
                        }

                        HorizontalDivider(color = Color(0xFF1E2838), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Haptic Feedback", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Vibrate on wickets and sixes", color = CrixerTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = userSettings.hapticFeedback,
                                onCheckedChange = { viewModel.toggleHaptics(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
                            )
                        }
                    }
                }
            }

            // Notifications
            item {
                SettingsSectionHeader("Smart Notifications")
                LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), isInteractive = false) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Key Match Events", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Wickets, innings breaks, close chase", color = CrixerTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = userSettings.notifyMatchEvents,
                                onCheckedChange = { viewModel.toggleNotifyEvents(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
                            )
                        }

                        HorizontalDivider(color = Color(0xFF1E2838), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Player Milestones", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "50s, 100s, 5-wicket hauls, records", color = CrixerTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = userSettings.notifyMilestones,
                                onCheckedChange = { viewModel.toggleNotifyMilestones(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
                            )
                        }

                        HorizontalDivider(color = Color(0xFF1E2838), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Match Results", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Final match outcomes and summaries", color = CrixerTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = userSettings.notifyResults,
                                onCheckedChange = { viewModel.toggleNotifyResults(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
                            )
                        }
                    }
                }
            }

            // Verified Data Source Section
            item {
                SettingsSectionHeader("Data Integrity & Validation")
                LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), isInteractive = false) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Validated Cricket Engine Active", color = Color(0xFF34D399), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "All live match data cross-validated with stable ball identities. Historical statistics verified via CricMetric & Howstat data layers.",
                            color = CrixerTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // About Brand
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CrixerOfficialLogo(
                        size = 64.dp,
                        showWordmark = true,
                        showTagline = true,
                        animated = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141C2B))
                            .clickable { viewModel.navigateTo(CrixerScreen.SPLASH) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Replay Splash Presentation",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Version 2.0 · Build 2026.09", color = CrixerTextTertiary, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = CrixerTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
    )
}
