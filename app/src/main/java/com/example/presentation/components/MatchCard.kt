package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppDisplayMode
import com.example.domain.model.Match
import com.example.domain.model.MatchStatus
import com.example.presentation.model.CrixerMatchPresentation
import com.example.presentation.model.TeamPresentation
import com.example.presentation.model.toCrixerPresentation
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun MatchCard(
    match: Match,
    displayMode: AppDisplayMode,
    featured: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val data = match.toCrixerPresentation()
    val isPro = displayMode == AppDisplayMode.PRO
    val shape = RoundedCornerShape(if (featured) 24.dp else 18.dp)

    @Composable
    fun Content() {
        Column(
            Modifier.fillMaxWidth().padding(
                horizontal = if (featured) 18.dp else 16.dp,
                vertical = if (featured) 17.dp else 14.dp
            )
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(data.seriesName, color = CrixerTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        data.matchLabel,
                        color = CrixerTextTertiary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    )
                }
                StatusPill(data.statusLabel, match.status)
            }

            Spacer(Modifier.height(if (featured) 18.dp else 13.dp))

            ScorePair(
                team1 = data.team1,
                team2 = data.team2,
                featured = featured,
                status = match.status
            )

            val footer = data.situation ?: data.result ?: data.schedule ?: data.venueLabel
            if (!footer.isNullOrBlank()) {
                Spacer(Modifier.height(if (featured) 14.dp else 10.dp))
                Text(
                    footer,
                    color = if (match.status == MatchStatus.LIVE) CrixerWhite else CrixerTextSecondary,
                    fontSize = if (featured) 12.sp else 11.sp,
                    fontWeight = if (match.status == MatchStatus.LIVE) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 2
                )
            }
        }
    }

    if (isPro) {
        LiquidGlassSurface(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            tintColor = if (featured) Color(0xFF121D2A) else Color(0xFF0F151E),
            onClick = onClick
        ) { Content() }
    } else {
        Column(
            modifier.fillMaxWidth()
                .background(Color(0xFF0B1017), shape)
                .border(1.dp, Color(0xFF1B2634), shape)
                .clickable(onClick = onClick)
        ) { Content() }
    }
}

@Composable
private fun ScorePair(
    team1: TeamPresentation,
    team2: TeamPresentation,
    featured: Boolean,
    status: MatchStatus
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TeamBlock(team1, Alignment.Start, featured, status)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                when (status) {
                    MatchStatus.LIVE -> "LIVE"
                    MatchStatus.COMPLETED -> "FINAL"
                    MatchStatus.UPCOMING -> "VS"
                    else -> status.name
                },
                color = if (status == MatchStatus.LIVE) CrixerRed else CrixerTextTertiary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        TeamBlock(team2, Alignment.End, featured, status)
    }
}

@Composable
private fun TeamBlock(
    team: TeamPresentation,
    alignment: Alignment.Horizontal,
    featured: Boolean,
    status: MatchStatus
) {
    Column(horizontalAlignment = alignment, modifier = Modifier.widthIn(min = 96.dp).weight(1f)) {
        Text(team.emoji, fontSize = if (featured) 22.sp else 18.sp)
        Spacer(Modifier.height(3.dp))
        Text(team.shortName, color = CrixerWhite, fontSize = if (featured) 14.sp else 13.sp, fontWeight = FontWeight.Bold)
        if (team.score != null) {
            Text(team.score, color = CrixerWhite, fontSize = if (featured) 27.sp else 23.sp, fontWeight = FontWeight.Black)
            Text(
                team.overs?.let { "($it)" } ?: "Overs unavailable",
                color = CrixerTextTertiary,
                fontSize = 9.sp
            )
        } else if (status == MatchStatus.UPCOMING) {
            Text("—", color = CrixerTextTertiary, fontSize = 23.sp)
        } else {
            Text("Score unavailable", color = CrixerTextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun StatusPill(label: String, status: MatchStatus) {
    val color = when (status) {
        MatchStatus.LIVE -> CrixerRed
        MatchStatus.COMPLETED -> Color(0xFF94A3B8)
        MatchStatus.UPCOMING -> Color(0xFF38BDF8)
        MatchStatus.DELAYED -> Color(0xFFF59E0B)
        MatchStatus.ABANDONED -> Color(0xFFEF4444)
    }
    Box(
        Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(7.dp))
            .border(1.dp, color.copy(alpha = 0.28f), RoundedCornerShape(7.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}
