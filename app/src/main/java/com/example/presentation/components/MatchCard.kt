package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppDisplayMode
import com.example.domain.model.Innings
import com.example.domain.model.Match
import com.example.domain.model.MatchStatus
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun MatchCard(
    match: Match,
    displayMode: AppDisplayMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPro = displayMode == AppDisplayMode.IMMERSIVE
    val shape = RoundedCornerShape(if (isPro) 22.dp else 16.dp)
    val current = if (match.currentInningsNumber == 2) match.innings2 else match.innings1

    @Composable
    fun Content() {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(match.title, color = CrixerTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                StatusPill(match.status)
            }
            Spacer(Modifier.size(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamScore(match.team1.shortName, match.team1.flagEmoji, match.innings1, false, match.status == MatchStatus.UPCOMING)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        when (match.status) {
                            MatchStatus.LIVE -> "LIVE"
                            MatchStatus.COMPLETED -> "RESULT"
                            MatchStatus.UPCOMING -> "VS"
                            else -> match.status.name
                        },
                        color = if (match.status == MatchStatus.LIVE) CrixerRed else CrixerTextTertiary,
                        fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                    )
                    if (match.status == MatchStatus.LIVE && current != null) {
                        Text(current.battingTeamId, color = CrixerTextTertiary, fontSize = 9.sp)
                    }
                }
                TeamScore(match.team2.shortName, match.team2.flagEmoji, match.innings2, true, match.status == MatchStatus.UPCOMING)
            }
            Spacer(Modifier.size(10.dp))
            Text(
                text = when {
                    match.status == MatchStatus.UPCOMING -> match.scheduledDateText ?: "Scheduled"
                    !match.resultSummary.isNullOrBlank() -> match.resultSummary!!
                    match.situationSummary.isNotBlank() -> match.situationSummary
                    else -> match.venue.ifBlank { "Match details" }
                },
                color = CrixerTextSecondary, fontSize = 11.sp, maxLines = 2
            )
        }
    }

    if (isPro) {
        LiquidGlassSurface(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            tintColor = Color(0xFF101722),
            onClick = onClick
        ) { Content() }
    } else {
        Column(
            modifier.fillMaxWidth().clip(shape).background(Color(0xFF0B1017))
                .border(1.dp, Color(0xFF1B2634), shape).clickable(onClick = onClick)
        ) { Content() }
    }
}

@Composable
private fun StatusPill(status: MatchStatus) {
    val color = when (status) {
        MatchStatus.LIVE -> CrixerRed
        MatchStatus.COMPLETED -> Color(0xFF64748B)
        MatchStatus.UPCOMING -> Color(0xFF38BDF8)
        else -> Color(0xFF94A3B8)
    }
    Box(
        Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.16f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) { Text(status.name, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
}

@Composable
private fun TeamScore(name: String, emoji: String, innings: Innings?, alignEnd: Boolean, upcoming: Boolean) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!alignEnd) TeamBadge(emoji)
            if (!alignEnd) Spacer(Modifier.size(7.dp))
            Text(name, color = CrixerWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            if (alignEnd) Spacer(Modifier.size(7.dp))
            if (alignEnd) TeamBadge(emoji)
        }
        if (upcoming) {
            Text("—", color = CrixerTextTertiary, fontSize = 22.sp, fontWeight = FontWeight.Light)
        } else if (innings != null) {
            Text(
                innings.runs.toString() + "/" + innings.wickets,
                color = CrixerWhite, fontSize = 25.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text("(" + innings.overs + ")", color = CrixerTextTertiary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun TeamBadge(emoji: String) {
    Box(
        Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF182231)),
        contentAlignment = Alignment.Center
    ) { Text(emoji, fontSize = 18.sp) }
}
