package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

@Composable
fun CricketGlanceScreen(viewModel: CrixerViewModel, modifier: Modifier = Modifier) {
    val live by viewModel.liveMatches.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    Column(Modifier.fillMaxSize().background(CrixerBlack).statusBarsPadding().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { viewModel.navigateTo(CrixerScreen.HOME) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CrixerWhite)
            }
            Text("CRICKET GLANCE", color = CrixerWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("AOD", color = CrixerTextSecondary, fontSize = 10.sp)
        }
        Spacer(Modifier.height(16.dp))
        SettingRow("Enable Cricket Glance", "Live score on supported system surfaces", settings.cricketGlanceEnabled) {
            viewModel.toggleCricketGlance(it)
        }
        Spacer(Modifier.height(12.dp))
        SettingRow("Live Updates", "Promoted live score notifications when supported", settings.liveUpdatesEnabled) {
            viewModel.toggleLiveUpdates(it)
        }
        Spacer(Modifier.height(24.dp))
        if (live.isNotEmpty()) {
            val m = live.first()
            val current = if (m.currentInningsNumber == 2) m.innings2 else m.innings1
            LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                Column(Modifier.padding(20.dp)) {
                    Text("PREVIEW", color = CrixerTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(m.team1.shortName + "  " + m.innings1.runs + "/" + m.innings1.wickets, color = CrixerWhite, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(
                        if (m.innings2 != null) m.team2.shortName + "  " + m.innings2!!.runs + "/" + m.innings2!!.wickets else m.team2.shortName + "  —",
                        color = CrixerWhite, fontSize = 22.sp, fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        m.situationSummary.ifBlank { current?.battingTeamId ?: "Live state" },
                        color = CrixerTextSecondary, fontSize = 11.sp
                    )
                }
            }
        } else {
            Text("No live match is available for a glance preview.", color = CrixerTextSecondary, fontSize = 12.sp)
        }
    }
}
@Composable
private fun SettingRow(title: String, body: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = CrixerWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(body, color = CrixerTextSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = checked, onCheckedChange = onChecked,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF38BDF8))
            )
        }
    }
}
