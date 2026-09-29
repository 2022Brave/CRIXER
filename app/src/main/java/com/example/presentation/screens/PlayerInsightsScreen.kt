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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Match
import com.example.domain.model.PlayerProfile
import com.example.presentation.components.LiquidGlassSurface
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun PlayerInsightsScreen(
    playerProfile: PlayerProfile?,
    match: Match,
    modifier: Modifier = Modifier
) {
    var selectedPlayerId by remember { mutableStateOf("ind-3") }

    val isKohli = selectedPlayerId == "ind-3"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D14))
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Player Switcher Pill (Kohli vs Bumrah)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131924))
                        .padding(3.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isKohli) CrixerWhite else Color.Transparent)
                                .clickable { selectedPlayerId = "ind-3" }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Virat Kohli",
                                color = if (isKohli) Color.Black else CrixerTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isKohli) CrixerWhite else Color.Transparent)
                                .clickable { selectedPlayerId = "ind-b1" }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Jasprit Bumrah",
                                color = if (!isKohli) Color.Black else CrixerTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Hero Player Card with Liquid Glass
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Portrait Avatar
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF9933), Color(0xFF138808))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isKohli) "VK" else "JB",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = if (isKohli) "Virat Kohli" else "Jasprit Bumrah",
                                color = CrixerWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🇮🇳 IND · ", color = CrixerTextSecondary, fontSize = 12.sp)
                                Text(
                                    text = if (isKohli) "Right Hand Batter" else "Right Arm Fast",
                                    color = CrixerTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Match figures
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isKohli) "62* (45)" else "3/44 (8.2)",
                                color = CrixerWhite,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (isKohli) "SR 137.8" else "Econ 5.28",
                                color = CrixerTextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF223046))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isKohli) "ON STRIKE" else "SPELL ACTIVE",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Contextual Milestone Progress
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                tintColor = Color(0xFF141926),
                isInteractive = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isKohli) "Needs 14 runs to reach 16,000 ODI runs" else "Needs 1 more wicket to reach 150 ODI wickets",
                        color = Color(0xFFFFB612),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (isKohli) 0.99f else 0.993f)
                                .height(6.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFFFB612), Color(0xFFFFE082))
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isKohli) "15,986" else "149", color = CrixerTextSecondary, fontSize = 11.sp)
                        Text(text = if (isKohli) "16,000" else "150", color = CrixerTextSecondary, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Source: CricMetric & Howstat Historical Database",
                        color = CrixerTextTertiary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Career Statistics
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Career in ODIs",
                        color = CrixerWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CareerStatItem(label = "Matches", value = if (isKohli) "294" else "89")
                        CareerStatItem(label = if (isKohli) "Runs" else "Wickets", value = if (isKohli) "15,986" else "149")
                        CareerStatItem(label = if (isKohli) "Average" else "Economy", value = if (isKohli) "59.1" else "4.63")
                        CareerStatItem(label = if (isKohli) "100s" else "4w", value = if (isKohli) "50" else "6")
                        CareerStatItem(label = if (isKohli) "50s" else "5w", value = if (isKohli) "73" else "2")
                    }
                }
            }
        }

        // Recent Form
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recent Form",
                        color = CrixerWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val formScores = if (isKohli) listOf("122", "8", "45", "74", "51") else listOf("3", "2", "4", "1", "2")
                        formScores.forEach { score ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF161F2E))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = score,
                                    color = if (score.toIntOrNull() ?: 0 >= 50) Color(0xFFFFB612) else CrixerWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Key Moments & Milestones
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Key Moments (ODIs)",
                        color = CrixerWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val milestones = if (isKohli) listOf(
                        "16,000 ODI runs" to "14 runs away",
                        "Most ODI centuries" to "50 (World Record)",
                        "Run Chase Average" to "88.3 (Highest in history)"
                    ) else listOf(
                        "150 ODI wickets" to "1 wicket away",
                        "Best ODI Figures" to "6/19 vs England",
                        "Death-overs Economy" to "5.28 in T20/ODIs"
                    )

                    milestones.forEach { (title, subtitle) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = title, color = CrixerWhite, fontSize = 12.sp)
                            Text(text = subtitle, color = Color(0xFFFFB612), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CareerStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = CrixerTextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
