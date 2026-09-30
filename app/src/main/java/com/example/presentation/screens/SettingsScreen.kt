package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.data.repository.AppDisplayMode
import com.example.presentation.components.CrixerOfficialLogo
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun SettingsScreen(viewModel: CrixerViewModel, modifier: Modifier = Modifier) {
    val userSettings by viewModel.userSettings.collectAsState()

    Column(
        modifier.fillMaxSize().background(CrixerBlack).statusBarsPadding().padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(CrixerScreen.HOME) }, Modifier.size(36.dp).testTag("settings_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CrixerWhite)
            }
            Text("Settings", color = CrixerWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(36.dp))
        }

        Spacer(Modifier.height(14.dp))

        LazyColumn(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSectionHeader("Appearance & Mode")
                LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                    Column(Modifier.padding(14.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Default Display Mode", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (userSettings.displayMode == AppDisplayMode.PRO)
                                        "PRO — Liquid Glass, depth & richer match context"
                                    else
                                        "LITE — score-first, minimal & fast",
                                    color = CrixerTextSecondary, fontSize = 11.sp
                                )
                            }
                            Box(
                                Modifier.clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF223046))
                                    .clickable { viewModel.toggleDisplayMode() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    if (userSettings.displayMode == AppDisplayMode.PRO) "PRO" else "LITE",
                                    color = Color(0xFF7DD3FC), fontSize = 12.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(Color(0xFF1E2838), 0.5.dp, Modifier.padding(vertical = 10.dp))
                        SettingSwitchRow(
                            "Reduced Motion",
                            "Minimizes fluid transitions and pulse",
                            userSettings.reducedMotion,
                            viewModel::toggleReducedMotion
                        )
                    }
                }
            }

            item {
                SettingsSectionHeader("Live & Glance")
                LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                    Column(Modifier.padding(14.dp)) {
                        SettingSwitchRow("Live Updates", "Receive ball-by-ball stream updates", userSettings.liveUpdatesEnabled, viewModel::toggleLiveUpdates)
                        HorizontalDivider(Color(0xFF1E2838), 0.5.dp, Modifier.padding(vertical = 10.dp))
                        SettingSwitchRow("Cricket Glance", "Minimal live score on the lock screen", userSettings.cricketGlanceEnabled, viewModel::toggleCricketGlance)
                        HorizontalDivider(Color(0xFF1E2838), 0.5.dp, Modifier.padding(vertical = 10.dp))
                        SettingSwitchRow("Haptic Feedback", "Vibrate on verified wickets and sixes", userSettings.hapticFeedback, viewModel::toggleHaptics)
                    }
                }
            }

            item {
                SettingsSectionHeader("Smart Notifications")
                LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                    Column(Modifier.padding(14.dp)) {
                        SettingSwitchRow("Key Match Events", "Wickets, innings breaks, close chase", userSettings.notifyMatchEvents, viewModel::toggleNotifyEvents)
                        HorizontalDivider(Color(0xFF1E2838), 0.5.dp, Modifier.padding(vertical = 10.dp))
                        SettingSwitchRow("Player Milestones", "50s, 100s, 5-wicket hauls, verified records", userSettings.notifyMilestones, viewModel::toggleNotifyMilestones)
                        HorizontalDivider(Color(0xFF1E2838), 0.5.dp, Modifier.padding(vertical = 10.dp))
                        SettingSwitchRow("Match Results", "Final match outcomes and summaries", userSettings.notifyResults, viewModel::toggleNotifyResults)
                    }
                }
            }

            item {
                SettingsSectionHeader("Data Integrity")
                LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.CheckCircle, "Verified", tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Validated Cricket Engine Active", color = Color(0xFF34D399), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "CRIXER hides unverified numbers instead of inventing placeholders. Live data and ball identities are validated before presentation.",
                                color = CrixerTextSecondary, fontSize = 11.sp, lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            item {
                Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CrixerOfficialLogo(64.dp, showWordmark = true, showTagline = true, animated = true)
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141C2B))
                            .clickable { viewModel.navigateTo(CrixerScreen.SPLASH) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Replay Splash Presentation", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Version 2.0 · Build 2026.10", color = CrixerTextTertiary, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = CrixerTextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CrixerRed)
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(title, color = CrixerTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
}
