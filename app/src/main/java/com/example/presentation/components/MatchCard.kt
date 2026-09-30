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
                    if (match.status == MatchStatus.UPCOMING) {
                        Text(
                            text = match.scheduledDateText ?: "Upcoming",
                            color = CrixerTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
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