package com.example.presentation.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Match
import com.example.presentation.components.LiquidGlassSurface
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerSixGlow
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun MatchInsightsScreen(
    match: Match,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D14))
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Win Probability Card with Graph
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Win Probability",
                        color = CrixerWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = match.team1.flagEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${match.team1.shortName} ${match.winProbabilityTeam1}%",
                                color = CrixerWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${match.team2.shortName} ${match.winProbabilityTeam2}%",
                                color = CrixerTextSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = match.team2.flagEmoji, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Line Graph over overs 0-50
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        val path = Path()
                        val points = listOf(
                            Offset(0f, size.height * 0.5f),
                            Offset(size.width * 0.15f, size.height * 0.6f),
                            Offset(size.width * 0.35f, size.height * 0.45f),
                            Offset(size.width * 0.55f, size.height * 0.7f),
                            Offset(size.width * 0.75f, size.height * 0.4f),
                            Offset(size.width * 0.90f, size.height * 0.55f),
                            Offset(size.width, size.height * (1f - (match.winProbabilityTeam1 / 100f)))
                        )

                        points.forEachIndexed { index, pt ->
                            if (index == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
                        }

                        // Gradient stroke
                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF38BDF8), Color(0xFFFF9933))
                            ),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Latest point highlight
                        val lastPoint = points.last()
                        drawCircle(
                            color = Color.White,
                            radius = 4.dp.toPx(),
                            center = lastPoint
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "0 OV", color = CrixerTextTertiary, fontSize = 10.sp)
                        Text(text = "10 OV", color = CrixerTextTertiary, fontSize = 10.sp)
                        Text(text = "20 OV", color = CrixerTextTertiary, fontSize = 10.sp)
                    }
                }
            }
        }

        // Required Run Rate vs Current Run Rate
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // RRR Card
                LiquidGlassSurface(
                    modifier = Modifier.weight(1f),
                    isInteractive = false
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Required Run Rate", color = CrixerTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${match.requiredRunRate ?: 10.8}",
                            color = Color(0xFFFFB612),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // CRR Card
                LiquidGlassSurface(
                    modifier = Modifier.weight(1f),
                    isInteractive = false
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Current Run Rate", color = CrixerTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${match.currentRunRate}",
                            color = Color(0xFF38BDF8),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Match Momentum Bar Chart
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Match Momentum", color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF9933)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "IND Favouring", color = CrixerTextSecondary, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simplified vertical bar chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val momentumBars = listOf(14, 28, -12, 34, 42, -20, 18, 55, -15, 65, 48, 70)
                        momentumBars.forEach { value ->
                            val isPositive = value >= 0
                            val barHeightRatio = (Math.abs(value).toFloat() / 70f).coerceIn(0.15f, 1f)
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(50.dp * barHeightRatio)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isPositive) Color(0xFFFF9933) else Color(0xFF002B7F))
                            )
                        }
                    }
                }
            }
        }

        // Key Moments Timeline
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isInteractive = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Key Moments",
                        color = CrixerWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val moments = listOf(
                        Triple("18.2", "Kohli hits majestic six into the stands", true),
                        Triple("18.1", "Pandya c Marsh b Starc (18 off 12)", false),
                        Triple("16.1", "Rahul c Marsh b Starc (31 off 16)", false),
                        Triple("13.4", "Gill c Maxwell b Hazlewood (31 off 16)", false),
                        Triple("8.2", "Sharma lbw b Starc (24 off 18)", false)
                    )

                    moments.forEach { (over, desc, isSix) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSix) CrixerSixGlow else CrixerRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = over,
                                    color = if (isSix) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = desc,
                                color = CrixerWhite,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
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
