package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Match
import com.example.domain.model.PlayerProfile
import com.example.presentation.components.LiquidGlassSurface
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

/**
 * Player insights are rendered only from verified player/match data.
 * Never invent live figures, career totals, milestones, or recent form.
 */
@Composable
fun PlayerInsightsScreen(
    playerProfile: PlayerProfile?,
    match: Match,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D14))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LiquidGlassSurface(
            modifier = Modifier.padding(top = 12.dp),
            isInteractive = false
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (playerProfile == null) "Player insights unavailable" else playerProfile.name,
                    color = CrixerWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (playerProfile == null)
                        "CRIXER will show player figures only when they are received from a verified match source."
                    else
                        "${playerProfile.teamCode} · ${playerProfile.role}",
                    color = CrixerTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
