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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.presentation.components.BallRail
import com.example.presentation.components.LiquidGlassSurface
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

@Composable
fun LeanBackScreen(viewModel: CrixerViewModel, modifier: Modifier = Modifier) {
    val match by viewModel.selectedMatch.collectAsState()
    if (match == null) {
        Box(Modifier.fillMaxSize().background(CrixerBlack), contentAlignment = Alignment.Center) {
            Text("No match selected", color = CrixerTextSecondary)
        }
        return
    }
    val m = match!!
    val current = if (m.currentInningsNumber == 2) m.innings2 else m.innings1
    Box(Modifier.fillMaxSize().background(Color(0xFF05080C))) {
        Column(Modifier.fillMaxSize().padding(28.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(CrixerScreen.MATCH_DETAIL) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CrixerWhite)
                }
                Text("LEAN BACK", color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                Text(m.format.name, color = CrixerTextSecondary, fontSize = 11.sp)
            }
            Spacer(Modifier.height(36.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                LeanTeam(m.team1.name, m.team1.flagEmoji, m.innings1.runs.toString() + "/" + m.innings1.wickets, m.innings1.overs.toString())
                Text("VS", color = Color(0xFF4B5563), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LeanTeam(
                    m.team2.name, m.team2.flagEmoji,
                    if (m.innings2 != null) m.innings2!!.runs.toString() + "/" + m.innings2!!.wickets else "—",
                    if (m.innings2 != null) m.innings2!!.overs.toString() else ""
                )
            }
            Spacer(Modifier.height(42.dp))
            LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                Column(Modifier.padding(20.dp)) {
                    Text("CURRENT", color = CrixerTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        m.situationSummary.ifBlank { m.resultSummary ?: m.scheduledDateText ?: "Waiting for verified update" },
                        color = CrixerWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
            Text("THIS OVER", color = CrixerTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Spacer(Modifier.height(8.dp))
            BallRail(balls = current?.balls ?: emptyList())
        }
    }
}
@Composable
private fun LeanTeam(name: String, emoji: String, score: String, overs: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 34.sp)
        Text(name, color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(score, color = CrixerWhite, fontSize = 42.sp, fontWeight = FontWeight.Black)
        if (overs.isNotBlank()) Text("(" + overs + ")", color = CrixerTextSecondary, fontSize = 12.sp)
    }
}
