package com.example.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Innings
import com.example.domain.model.Match
import com.example.presentation.components.LiquidGlassPulse
import com.example.presentation.components.LiquidGlassSurface
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerTextTertiary
import com.example.ui.theme.CrixerWhite

@Composable
fun ScorecardScreen(
    match: Match,
    pulseState: LiquidGlassPulse = LiquidGlassPulse.NONE,
    isSimpleMode: Boolean = false,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier
) {
    val inningsList = listOfNotNull(match.innings1, match.innings2)
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        inningsList.forEach { innings ->
            item(key = "header-" + innings.inningsNumber) {
                InningsHeader(match, innings)
            }
            item(key = "bat-header-" + innings.inningsNumber) {
                TableHeader(listOf("BATTER", "R", "B", "4s", "6s", "SR"))
            }
            items(innings.batters, key = { "bat-" + it.id }) { batter ->
                LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                    Row(Modifier.fillMaxWidth().padding(11.dp)) {
                        Text(
                            batter.name + if (batter.isNotOut) "*" else "",
                            color = CrixerWhite, fontSize = 12.sp, modifier = Modifier.weight(2.6f)
                        )
                        Text(batter.runs.toString(), color = CrixerWhite, fontWeight = FontWeight.Bold, modifier = Modifier.weight(.65f), textAlign = TextAlign.End)
                        Text(batter.balls.toString(), color = CrixerTextSecondary, modifier = Modifier.weight(.65f), textAlign = TextAlign.End)
                        Text(batter.fours.toString(), color = CrixerTextSecondary, modifier = Modifier.weight(.55f), textAlign = TextAlign.End)
                        Text(batter.sixes.toString(), color = CrixerTextSecondary, modifier = Modifier.weight(.55f), textAlign = TextAlign.End)
                        Text("%.1f".format(batter.strikeRate), color = CrixerTextSecondary, modifier = Modifier.weight(.85f), textAlign = TextAlign.End)
                    }
                }
            }
            if (innings.bowlers.isNotEmpty()) {
                item(key = "bowl-header-" + innings.inningsNumber) {
                    TableHeader(listOf("BOWLER", "O", "M", "R", "W", "ECON"))
                }
                items(innings.bowlers, key = { "bowl-" + it.id }) { bowler ->
                    LiquidGlassSurface(Modifier.fillMaxWidth(), isInteractive = false) {
                        Row(Modifier.fillMaxWidth().padding(11.dp)) {
                            Text(bowler.name, color = CrixerWhite, fontSize = 12.sp, modifier = Modifier.weight(2.4f))
                            Text(bowler.overs.toString(), color = CrixerTextSecondary, modifier = Modifier.weight(.7f), textAlign = TextAlign.End)
                            Text(bowler.maidens.toString(), color = CrixerTextSecondary, modifier = Modifier.weight(.6f), textAlign = TextAlign.End)
                            Text(bowler.runsConceded.toString(), color = CrixerTextSecondary, modifier = Modifier.weight(.7f), textAlign = TextAlign.End)
                            Text(bowler.wickets.toString(), color = CrixerWhite, fontWeight = FontWeight.Bold, modifier = Modifier.weight(.6f), textAlign = TextAlign.End)
                            Text("%.2f".format(bowler.economy), color = CrixerTextSecondary, modifier = Modifier.weight(.9f), textAlign = TextAlign.End)
                        }
                    }
                }
            }
            item(key = "extra-" + innings.inningsNumber) {
                Spacer(Modifier.height(2.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("EXTRAS", color = CrixerTextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(innings.extras.toString(), color = CrixerTextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun InningsHeader(match: Match, innings: Innings) {
    val team = if (innings.battingTeamId == match.team1.id) match.team1 else match.team2
    LiquidGlassSurface(Modifier.fillMaxWidth(), tintColor = Color(0xFF111A25), isInteractive = false) {
        Column(Modifier.padding(15.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(team.name, color = CrixerWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(innings.runs.toString() + "/" + innings.wickets, color = CrixerWhite, fontSize = 21.sp, fontWeight = FontWeight.Black)
            }
            Text(innings.overs.toString() + " overs", color = CrixerTextSecondary, fontSize = 10.sp)
        }
    }
}
@Composable
private fun TableHeader(labels: List<String>) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        labels.forEachIndexed { index, label ->
            Text(
                label, color = CrixerTextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(if (index == 0) 2.6f else 0.7f),
                textAlign = if (index == 0) TextAlign.Start else TextAlign.End
            )
        }
    }
}
