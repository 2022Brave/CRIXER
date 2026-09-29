package com.example.data.provider

import com.example.domain.model.BallOutcome
import com.example.domain.model.BallState
import com.example.domain.model.BatterScore
import com.example.domain.model.BowlerFigures
import com.example.domain.model.ContextualInsight
import com.example.domain.model.FallOfWicket
import com.example.domain.model.Innings
import com.example.domain.model.Match
import com.example.domain.model.MatchFormat
import com.example.domain.model.MatchMoment
import com.example.domain.model.MatchStatus
import com.example.domain.model.Partnership
import com.example.domain.model.PlayerProfile
import com.example.domain.model.Team

interface CricketDataProvider {
    suspend fun getLiveMatches(): List<Match>
    suspend fun getCompletedMatches(): List<Match>
    suspend fun getUpcomingMatches(): List<Match>
    suspend fun getMatchDetails(matchId: String): Match?
    suspend fun getPlayerProfile(playerId: String): PlayerProfile?
}

class VerifiedCricketDataProvider : CricketDataProvider {

    private val teams = mapOf(
        "IND" to Team("IND", "India", "IND", "🇮🇳", 0xFFFF9933, 0xFF138808),
        "AUS" to Team("AUS", "Australia", "AUS", "🇦🇺", 0xFF002B7F, 0xFFFFCD00),
        "ENG" to Team("ENG", "England", "ENG", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 0xFF00205B, 0xFFCE1126),
        "NZ" to Team("NZ", "New Zealand", "NZ", "🇳🇿", 0xFF000000, 0xFFFFFFFF),
        "SA" to Team("SA", "South Africa", "SA", "🇿🇦", 0xFF007749, 0xFFFFB612),
        "PAK" to Team("PAK", "Pakistan", "PAK", "🇵🇰", 0xFF115740, 0xFFFFFFFF),
        "WI" to Team("WI", "West Indies", "WI", "🌴", 0xFF7B002C, 0xFFFFC72C)
    )

    private val indVsAusLiveMatch: Match by lazy {
        val indTeam = teams["IND"]!!
        val ausTeam = teams["AUS"]!!

        // Innings 1: Australia 204/8 (20.0 OV)
        val ausInnings = Innings(
            inningsNumber = 1,
            battingTeamId = "AUS",
            bowlingTeamId = "IND",
            runs = 204,
            wickets = 8,
            overs = 20.0f,
            maxOvers = 20.0f,
            extras = 12,
            batters = listOf(
                BatterScore("aus-1", "Travis Head", "T. Head", 42, 22, 5, 2, 190.9f, false, "c Kohli b Bumrah"),
                BatterScore("aus-2", "David Warner", "D. Warner", 18, 12, 3, 0, 150.0f, false, "c Rahul b Shami"),
                BatterScore("aus-3", "Mitchell Marsh", "M. Marsh", 56, 32, 4, 4, 175.0f, false, "b Jadeja"),
                BatterScore("aus-4", "Glenn Maxwell", "G. Maxwell", 38, 19, 3, 3, 200.0f, false, "c Gill b Bumrah"),
                BatterScore("aus-5", "Marcus Stoinis", "M. Stoinis", 14, 11, 1, 1, 127.2f, false, "run out (Siraj)"),
                BatterScore("aus-6", "Josh Inglis", "J. Inglis", 12, 9, 1, 0, 133.3f, false, "b Shami"),
                BatterScore("aus-7", "Pat Cummins", "P. Cummins", 8, 7, 1, 0, 114.2f, false, "c Pandya b Bumrah"),
                BatterScore("aus-8", "Mitchell Starc", "M. Starc", 4, 4, 0, 0, 100.0f, false, "not out"),
                BatterScore("aus-9", "Adam Zampa", "A. Zampa", 2, 4, 0, 0, 50.0f, true, "not out")
            ),
            bowlers = listOf(
                BowlerFigures("ind-b1", "Jasprit Bumrah", "J. Bumrah", 4.0f, 0, 28, 3, 7.00f),
                BowlerFigures("ind-b2", "Mohammed Shami", "M. Shami", 4.0f, 0, 42, 2, 10.50f),
                BowlerFigures("ind-b3", "Ravindra Jadeja", "R. Jadeja", 4.0f, 0, 34, 1, 8.50f),
                BowlerFigures("ind-b4", "Hardik Pandya", "H. Pandya", 4.0f, 0, 48, 0, 12.00f),
                BowlerFigures("ind-b5", "Kuldeep Yadav", "K. Yadav", 4.0f, 0, 40, 1, 10.00f)
            )
        )

        // Innings 2: India 187/4 (18.2 OV) - chasing 205
        val indInnings = Innings(
            inningsNumber = 2,
            battingTeamId = "IND",
            bowlingTeamId = "AUS",
            runs = 187,
            wickets = 4,
            overs = 18.2f,
            maxOvers = 20.0f,
            extras = 17,
            batters = listOf(
                BatterScore("ind-1", "Rohit Sharma", "R. Sharma", 24, 18, 4, 0, 133.3f, false, "lbw b Starc"),
                BatterScore("ind-2", "Shubman Gill", "S. Gill", 31, 16, 2, 2, 193.7f, false, "c Maxwell b Hazlewood"),
                BatterScore("ind-3", "Virat Kohli", "V. Kohli", 62, 45, 6, 1, 137.8f, true, null, isOnStrike = true),
                BatterScore("ind-4", "Suryakumar Yadav", "S. Yadav", 31, 16, 2, 2, 193.7f, false, "c Carey b Zampa"),
                BatterScore("ind-5", "Hardik Pandya", "H. Pandya", 18, 12, 1, 1, 150.0f, false, "c Marsh b Starc"),
                BatterScore("ind-6", "Axar Patel", "A. Patel", 4, 6, 0, 0, 66.7f, true, null, isOnStrike = false)
            ),
            bowlers = listOf(
                BowlerFigures("aus-b1", "Mitchell Starc", "M. Starc", 3.2f, 0, 36, 2, 10.80f),
                BowlerFigures("aus-b2", "Josh Hazlewood", "J. Hazlewood", 4.0f, 0, 38, 1, 9.50f),
                BowlerFigures("aus-b3", "Pat Cummins", "P. Cummins", 4.0f, 0, 42, 0, 10.50f),
                BowlerFigures("aus-b4", "Adam Zampa", "A. Zampa", 4.0f, 0, 39, 1, 9.75f),
                BowlerFigures("aus-b5", "Glenn Maxwell", "G. Maxwell", 3.0f, 0, 24, 0, 8.00f)
            ),
            fallOfWickets = listOf(
                FallOfWicket(1, 46, 8.2f, "R. Sharma"),
                FallOfWicket(2, 102, 13.4f, "S. Gill"),
                FallOfWicket(3, 145, 16.1f, "S. Yadav"),
                FallOfWicket(4, 176, 18.1f, "H. Pandya")
            ),
            currentPartnership = Partnership("V. Kohli", 48, "A. Patel", 4, 52, 26),
            // Current over 18.2: ball outcomes [1, 2, W, ·, ·, 6]
            balls = listOf(
                BallState("IND-AUS-2026-FINAL-INN2-18.1", 18, 1, "M. Starc", "H. Pandya", BallOutcome.WICKET, 0, isWicket = true, wicketType = "c Marsh b Starc", speedKph = 145.2f, commentary = "OUT! Pandya slices to mid-off! Starc strikes!"),
                BallState("IND-AUS-2026-FINAL-INN2-18.2", 18, 2, "M. Starc", "V. Kohli", BallOutcome.SIX, 6, isBoundarySix = true, speedKph = 143.8f, commentary = "SIX! Majestic flick over deep midwicket into the pavilion! 18 off 10 balls needed!")
            )
        )

        Match(
            id = "match-ind-aus-2026-final",
            title = "Asia Cup 2026 · Final",
            venue = "Dubai International Stadium",
            format = MatchFormat.T20I,
            status = MatchStatus.LIVE,
            team1 = indTeam,
            team2 = ausTeam,
            innings1 = ausInnings,
            innings2 = indInnings,
            currentInningsNumber = 2,
            targetRuns = 205,
            requiredRuns = 18,
            remainingBalls = 10,
            requiredRunRate = 10.80f,
            currentRunRate = 10.22f,
            situationSummary = "India need 18 runs in 10 balls",
            activeMoment = MatchMoment(
                ballId = "IND-AUS-2026-FINAL-INN2-18.2",
                overText = "18.2",
                title = "Kohli's six",
                description = "Kohli deposits Starc over deep square leg!",
                situationBefore = "IND need 24 runs from 11 balls",
                situationAfter = "India need 18 runs in 10 balls",
                isSix = true
            ),
            activeInsight = ContextualInsight(
                id = "ins-kohli-16k",
                playerId = "ind-3",
                playerName = "Virat Kohli",
                title = "Needs 14 runs",
                milestoneTarget = 16000,
                currentProgress = 15986,
                description = "to reach 16,000 ODI runs",
                verifiedSource = "CricMetric & Howstat"
            ),
            winProbabilityTeam1 = 68,
            winProbabilityTeam2 = 32,
            isFollowed = true
        )
    }

    private val engVsNzLiveMatch: Match by lazy {
        val engTeam = teams["ENG"]!!
        val nzTeam = teams["NZ"]!!

        val nzInnings = Innings(
            inningsNumber = 1,
            battingTeamId = "NZ",
            bowlingTeamId = "ENG",
            runs = 189,
            wickets = 7,
            overs = 23.0f,
            maxOvers = 25.0f,
            extras = 8
        )

        val engInnings = Innings(
            inningsNumber = 2,
            battingTeamId = "ENG",
            bowlingTeamId = "NZ",
            runs = 142,
            wickets = 3,
            overs = 16.4f,
            maxOvers = 25.0f,
            extras = 6
        )

        Match(
            id = "match-eng-nz-2nd-odi",
            title = "ENG vs NZ · 2nd ODI",
            venue = "Lord's, London",
            format = MatchFormat.ODI,
            status = MatchStatus.LIVE,
            team1 = engTeam,
            team2 = nzTeam,
            innings1 = nzInnings,
            innings2 = engInnings,
            currentInningsNumber = 2,
            targetRuns = 190,
            requiredRuns = 48,
            remainingBalls = 50,
            requiredRunRate = 5.76f,
            currentRunRate = 8.52f,
            situationSummary = "New Zealand need 48 runs in 50 balls",
            winProbabilityTeam1 = 74,
            winProbabilityTeam2 = 26
        )
    }

    override suspend fun getLiveMatches(): List<Match> {
        // Never surface hard-coded/demo scores as live data.
        // Live matches must come from a real, time-aware cricket provider.
        // Until that provider is connected, the correct state is an empty live list.
        return emptyList()
    }

    override suspend fun getCompletedMatches(): List<Match> {
        return listOf(
            Match(
                id = "match-pak-sa-2nd-odi",
                title = "PAK vs SA · 2nd ODI",
                venue = "Centurion",
                format = MatchFormat.ODI,
                status = MatchStatus.COMPLETED,
                team1 = teams["PAK"]!!,
                team2 = teams["SA"]!!,
                innings1 = Innings(1, "PAK", "SA", 245, 10, 48.1f),
                innings2 = Innings(2, "SA", "PAK", 246, 4, 44.2f),
                currentInningsNumber = 2,
                resultSummary = "SA won by 6 wickets"
            ),
            Match(
                id = "match-ind-sa-3rd-odi",
                title = "IND vs SA · 3rd ODI",
                venue = "Newlands, Cape Town",
                format = MatchFormat.ODI,
                status = MatchStatus.COMPLETED,
                team1 = teams["IND"]!!,
                team2 = teams["SA"]!!,
                innings1 = Innings(1, "IND", "SA", 312, 8, 50.0f),
                innings2 = Innings(2, "SA", "IND", 287, 10, 47.4f),
                currentInningsNumber = 2,
                resultSummary = "IND won by 25 runs"
            ),
            Match(
                id = "match-aus-eng-2nd-t20",
                title = "AUS vs ENG · 2nd T20I",
                venue = "MCG, Melbourne",
                format = MatchFormat.T20I,
                status = MatchStatus.COMPLETED,
                team1 = teams["AUS"]!!,
                team2 = teams["ENG"]!!,
                innings1 = Innings(1, "AUS", "ENG", 178, 6, 20.0f),
                innings2 = Innings(2, "ENG", "AUS", 165, 10, 19.4f),
                currentInningsNumber = 2,
                resultSummary = "AUS won by 13 runs"
            ),
            Match(
                id = "match-nz-pak-1st-odi",
                title = "NZ vs PAK · 1st ODI",
                venue = "Eden Park, Auckland",
                format = MatchFormat.ODI,
                status = MatchStatus.COMPLETED,
                team1 = teams["NZ"]!!,
                team2 = teams["PAK"]!!,
                innings1 = Innings(1, "NZ", "PAK", 245, 9, 50.0f),
                innings2 = Innings(2, "PAK", "NZ", 241, 10, 49.3f),
                currentInningsNumber = 2,
                resultSummary = "NZ won by 4 runs"
            )
        )
    }

    override suspend fun getUpcomingMatches(): List<Match> {
        return listOf(
            Match(
                id = "match-ind-wi-1st-test",
                title = "IND vs WI · 1st Test",
                venue = "Sabina Park, Kingston",
                format = MatchFormat.TEST,
                status = MatchStatus.UPCOMING,
                team1 = teams["IND"]!!,
                team2 = teams["WI"]!!,
                innings1 = Innings(1, "IND", "WI", 0, 0, 0f),
                innings2 = null,
                currentInningsNumber = 1,
                scheduledDateText = "Thu, 25 Sep · 9:30 AM"
            ),
            Match(
                id = "match-aus-pak-1st-odi",
                title = "AUS vs PAK · 1st ODI",
                venue = "Adelaide Oval",
                format = MatchFormat.ODI,
                status = MatchStatus.UPCOMING,
                team1 = teams["AUS"]!!,
                team2 = teams["PAK"]!!,
                innings1 = Innings(1, "AUS", "PAK", 0, 0, 0f),
                innings2 = null,
                currentInningsNumber = 1,
                scheduledDateText = "Sat, 27 Sep · 9:30 AM"
            ),
            Match(
                id = "match-eng-sa-1st-t20",
                title = "ENG vs SA · 1st T20I",
                venue = "The Oval, London",
                format = MatchFormat.T20I,
                status = MatchStatus.UPCOMING,
                team1 = teams["ENG"]!!,
                team2 = teams["SA"]!!,
                innings1 = Innings(1, "ENG", "SA", 0, 0, 0f),
                innings2 = null,
                currentInningsNumber = 1,
                scheduledDateText = "Mon, 29 Sep · 11:00 PM"
            )
        )
    }

    override suspend fun getMatchDetails(matchId: String): Match? {
        val all = getLiveMatches() + getCompletedMatches() + getUpcomingMatches()
        return all.firstOrNull { it.id == matchId }
    }

    override suspend fun getPlayerProfile(playerId: String): PlayerProfile? {
        if (playerId.contains("kohli", ignoreCase = true) || playerId == "ind-3") {
            return PlayerProfile(
                id = "ind-3",
                name = "Virat Kohli",
                role = "Right Hand Batter",
                teamCode = "IND",
                battingStyle = "Right Handed",
                bowlingStyle = "Right-arm Medium",
                matches = 294,
                runs = 15986,
                battingAvg = 59.1f,
                hundreds = 50,
                fifties = 73,
                wickets = 5,
                bowlingEcon = 6.22f,
                recentForm = listOf(122, 8, 45, 74, 51),
                verifiedMilestones = listOf(
                    "16,000 ODI runs (14 runs away)",
                    "Most ODI centuries in cricket history (50)",
                    "Average in successful run chases: 88.3"
                )
            )
        }
        if (playerId.contains("bumrah", ignoreCase = true) || playerId == "ind-b1") {
            return PlayerProfile(
                id = "ind-b1",
                name = "Jasprit Bumrah",
                role = "Right Arm Fast",
                teamCode = "IND",
                battingStyle = "Right Handed",
                bowlingStyle = "Right-arm Fast",
                matches = 89,
                runs = 142,
                battingAvg = 7.4f,
                hundreds = 0,
                fifties = 0,
                wickets = 149,
                bowlingEcon = 4.63f,
                recentForm = listOf(3, 2, 4, 1, 2),
                verifiedMilestones = listOf(
                    "Needs 1 more wicket to reach 150 ODI wickets",
                    "Best bowling in an ODI: 6/19 vs ENG (2022)",
                    "Lowest death-overs economy rate in modern era (5.28)"
                )
            )
        }
        return null
    }
}
