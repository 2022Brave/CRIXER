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
import com.example.presentation.components.LiquidGlassSurface
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

/**
 * Advanced match analytics are shown only when derived from verified ball-by-ball
 * data. Do not fabricate win probability, momentum, key moments, or RRR values.
 */
@Composable
fun MatchInsightsScreen(
    match: Match,
    modifier: Modifier = Modifier
) {
    val hasBallData = match.innings1.balls.isNotEmpty() || match.innings2?.balls?.isNotEmpty() == true
    val hasVerifiedRates = match.currentRunRate > 0f || match.requiredRunRate != null

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
                    text = "Match analytics",
                    color = CrixerWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (hasBallData)
                        "Ball-by-ball analytics will appear here from verified deliveries."
                    else
                        "Advanced analytics are unavailable for this match because verified ball-by-ball data has not been received.",
                    color = CrixerTextSecondary,
                    fontSize = 12.sp
                )
                if (hasVerifiedRates) {
                    Text(
                        text = buildString {
                            append("CRR ")
                            append(match.currentRunRate)
                            match.requiredRunRate?.let { append(" · RRR ").append(it) }
                        },
                        color = CrixerWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
