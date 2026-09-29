package com.example.domain.model

data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val flagEmoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long
)

enum class MatchFormat {
    T20I, ODI, TEST
}

enum class MatchStatus {
    LIVE, COMPLETED, UPCOMING, DELAYED, ABANDONED
}

data class BallState(
    val ballId: String,          // e.g. "IND-AUS-2026-FINAL-INN2-18.2"
    val overNumber: Int,         // 18
    val ballInOver: Int,         // 2
    val bowlerName: String,
    val batsmanName: String,
    val outcome: BallOutcome,
    val runs: Int,
    val isWicket: Boolean = false,
    val wicketType: String? = null,
    val isBoundaryFour: Boolean = false,
    val isBoundarySix: Boolean = false,
    val isExtra: Boolean = false,
    val extraType: String? = null,
    val speedKph: Float = 142.4f,
    val commentary: String = ""
)

enum class BallOutcome(val label: String) {
    DOT("·"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    SIX("6"),
    WICKET("W"),
    WIDE("WD"),
    NO_BALL("NB"),
    BYE("B"),
    LEG_BYE("LB")
}

data class BatterScore(
    val id: String,
    val name: String,
    val shortName: String,
    val runs: Int,
    val balls: Int,
    val fours: Int,
    val sixes: Int,
    val strikeRate: Float,
    val isNotOut: Boolean = true,
    val dismissalInfo: String? = null,
    val isOnStrike: Boolean = false
)

data class BowlerFigures(
    val id: String,
    val name: String,
    val shortName: String,
    val overs: Float,
    val maidens: Int,
    val runsConceded: Int,
    val wickets: Int,
    val economy: Float
)

data class FallOfWicket(
    val wicketNumber: Int,
    val score: Int,
    val over: Float,
    val playerOut: String
)

data class Partnership(
    val batter1Name: String,
    val batter1Runs: Int,
    val batter2Name: String,
    val batter2Runs: Int,
    val totalRuns: Int,
    val balls: Int
)

data class Innings(
    val inningsNumber: Int,
    val battingTeamId: String,
    val bowlingTeamId: String,
    val runs: Int,
    val wickets: Int,
    val overs: Float,
    val maxOvers: Float = 20.0f,
    val extras: Int = 0,
    val batters: List<BatterScore> = emptyList(),
    val bowlers: List<BowlerFigures> = emptyList(),
    val fallOfWickets: List<FallOfWicket> = emptyList(),
    val currentPartnership: Partnership? = null,
    val balls: List<BallState> = emptyList()
)

data class MatchMoment(
    val ballId: String,
    val overText: String,
    val title: String,
    val description: String,
    val situationBefore: String,
    val situationAfter: String,
    val isSix: Boolean = false,
    val isWicket: Boolean = false
)

data class ContextualInsight(
    val id: String,
    val playerId: String,
    val playerName: String,
    val title: String,
    val milestoneTarget: Int,
    val currentProgress: Int,
    val description: String,
    val verifiedSource: String = "CricMetric & Howstat"
)

data class PlayerProfile(
    val id: String,
    val name: String,
    val role: String,
    val teamCode: String,
    val battingStyle: String,
    val bowlingStyle: String,
    val matches: Int,
    val runs: Int,
    val battingAvg: Float,
    val hundreds: Int,
    val fifties: Int,
    val wickets: Int,
    val bowlingEcon: Float,
    val recentForm: List<Int>,
    val verifiedMilestones: List<String>
)

data class Match(
    val id: String,
    val title: String,                // "Asia Cup 2026 · Final"
    val venue: String,                // "Dubai International Stadium"
    val format: MatchFormat,
    val status: MatchStatus,
    val team1: Team,
    val team2: Team,
    val innings1: Innings,
    val innings2: Innings?,
    val currentInningsNumber: Int,
    val targetRuns: Int? = null,
    val requiredRuns: Int? = null,
    val remainingBalls: Int? = null,
    val requiredRunRate: Float? = null,
    val currentRunRate: Float = 0.0f,
    val situationSummary: String = "",
    val activeMoment: MatchMoment? = null,
    val activeInsight: ContextualInsight? = null,
    val winProbabilityTeam1: Int = 50,
    val winProbabilityTeam2: Int = 50,
    val isFollowed: Boolean = false,
    val resultSummary: String? = null,
    val scheduledDateText: String? = null
)
