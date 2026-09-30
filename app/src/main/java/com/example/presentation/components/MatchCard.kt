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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppDisplayMode
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (displayMode == AppDisplayMode.SIMPLE) {
        SimpleMatchCard(match = match, modifier = modifier, onClick = onClick)
    } else {
        ImmersiveMatchCard(match = match, modifier = modifier, onClick = onClick)
    }
}

@Composable
private fun SimpleMatchCard(
    match: Match,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        // Minimal header line: Title and restrained live badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = match.title.uppercase(),
                color = Color(0xFF8E9AA8),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            if (match.status == MatchStatus.LIVE) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CrixerRed)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LIVE",
                        color = CrixerRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // High-contrast, typography-first score layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Team 1 score block
            Column {
                Text(
                    text = match.team1.shortName,
                    color = Color(0xFFA0AAB8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                val inn1 = match.innings1
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${inn1.runs}/${inn1.wickets}",
                        color = CrixerWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${inn1.overs} OV)",
                        color = Color(0xFF717D8F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            // Team 2 score block
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = match.team2.shortName,
                    color = Color(0xFFA0AAB8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                val inn2 = match.innings2
                if (inn2 != null && (inn2.overs > 0f || inn2.runs > 0 || inn2.wickets > 0)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${inn2.runs}/${inn2.wickets}",
                            color = CrixerWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${inn2.overs} OV)",
                            color = Color(0xFF717D8F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                } else {
                    Text(
                        text = when (match.status) {
                            MatchStatus.UPCOMING -> match.scheduledDateText ?: "Upcoming"
                            else -> "Yet to bat"
                        },
                        color = Color(0xFF717D8F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Match situation line: Score -> overs -> situation -> players
        if (match.situationSummary.isNotEmpty() || match.resultSummary != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (match.resultSummary ?: match.situationSummary).uppercase(),
                    color = CrixerWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                if (match.requiredRunRate != null) {
                    Text(
                        text = "RRR ${match.requiredRunRate}",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        // Thin minimalist divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(Color(0xFF1E2633))
        )
    }
}

@Composable
private fun ImmersiveMatchCard(
    match: Match,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    LiquidGlassSurface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.title,
                    color = CrixerTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (match.status == MatchStatus.LIVE) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CrixerRed)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scores layout with flag circles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1
                Column(horizontalAlignment = Alignment.Start) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = match.team1.flagEmoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = match.team1.shortName,
                        color = CrixerTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val inn = match.innings1
                    Text(
                        text = "${inn.runs}/${inn.wickets}",
                        color = CrixerWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "(${inn.overs})",
                        color = CrixerTextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Cross indicator
                Text(
                    text = "✕",
                    color = Color(0x66FFFFFF),
                    fontSize = 16.sp
                )

                // Team 2
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = match.team2.flagEmoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = match.team2.shortName,
                        color = CrixerTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val inn2 = match.innings2
                    if (inn2 != null && (inn2.overs > 0f || inn2.runs > 0 || inn2.wickets > 0)) {
                        Text(
                            text = "${inn2.runs}/${inn2.wickets}",
                            color = CrixerWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "(${inn2.overs})",
                            color = CrixerTextSecondary,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = when (match.status) {
                                MatchStatus.UPCOMING -> match.scheduledDateText ?: "Upcoming"
                                else -> "Yet to bat"
                            },
                            color = CrixerTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Situation Banner
            if (match.situationSummary.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = match.situationSummary,
                        color = CrixerWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (match.requiredRunRate != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "RRR ${match.requiredRunRate}",
                            color = Color(0xFFFFB612),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else if (match.resultSummary != null) {
                Text(
                    text = match.resultSummary,
                    color = Color(0xFF34D399),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
