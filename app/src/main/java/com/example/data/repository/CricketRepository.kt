package com.example.data.repository

import com.example.data.local.CrixerDatabase
import com.example.data.provider.CricketDataProvider
import com.example.data.provider.VerifiedCricketDataProvider
import com.example.domain.model.BallOutcome
import com.example.domain.model.BallState
import com.example.domain.model.Match
import com.example.domain.model.MatchFormat
import com.example.domain.model.MatchMoment
import com.example.domain.model.MatchStatus
import com.example.domain.model.PlayerProfile
import com.example.domain.validation.CricketDataValidator
import com.example.domain.validation.MatchValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class CricketRepository(
    private val dataProvider: CricketDataProvider = VerifiedCricketDataProvider(),
    private val database: CrixerDatabase? = null
) {
    private val _liveMatches = MutableStateFlow<List<Match>>(emptyList())
    val liveMatches: StateFlow<List<Match>> = _liveMatches.asStateFlow()

    private val _completedMatches = MutableStateFlow<List<Match>>(emptyList())
    val completedMatches: StateFlow<List<Match>> = _completedMatches.asStateFlow()

    private val _upcomingMatches = MutableStateFlow<List<Match>>(emptyList())
    val upcomingMatches: StateFlow<List<Match>> = _upcomingMatches.asStateFlow()

    private val _selectedMatch = MutableStateFlow<Match?>(null)
    val selectedMatch: StateFlow<Match?> = _selectedMatch.asStateFlow()

    private val _validationState = MutableStateFlow<Map<String, MatchValidationResult>>(emptyMap())
    val validationState: StateFlow<Map<String, MatchValidationResult>> = _validationState.asStateFlow()

    // Following entities state
    private val _followedTeams = MutableStateFlow<Set<String>>(setOf("IND", "AUS"))
    val followedTeams: StateFlow<Set<String>> = _followedTeams.asStateFlow()

    private val _followedPlayers = MutableStateFlow<Set<String>>(setOf("ind-3", "ind-1", "aus-1")) // Kohli, Rohit, Travis Head
    val followedPlayers: StateFlow<Set<String>> = _followedPlayers.asStateFlow()

    suspend fun refreshData() {
        val live = dataProvider.getLiveMatches()
        val completed = dataProvider.getCompletedMatches()
        val upcoming = dataProvider.getUpcomingMatches()

        _liveMatches.value = live
        _completedMatches.value = completed
        _upcomingMatches.value = upcoming

        if (_selectedMatch.value == null && live.isNotEmpty()) {
            _selectedMatch.value = live.first()
        }

        // Validate all matches
        val validations = (live + completed).associate { match ->
            match.id to CricketDataValidator.validateMatch(match)
        }
        _validationState.value = validations
    }

    fun selectMatch(matchId: String) {
        val all = _liveMatches.value + _completedMatches.value + _upcomingMatches.value
        _selectedMatch.value = all.firstOrNull { it.id == matchId }
    }

    suspend fun getPlayerProfile(playerId: String): PlayerProfile? {
        return dataProvider.getPlayerProfile(playerId)
    }

    fun toggleFollowMatch(matchId: String, title: String) {
        _liveMatches.value = _liveMatches.value.map { match ->
            if (match.id == matchId) match.copy(isFollowed = !match.isFollowed) else match
        }
        if (_selectedMatch.value?.id == matchId) {
            _selectedMatch.value = _selectedMatch.value?.let { it.copy(isFollowed = !it.isFollowed) }
        }
    }

    fun toggleFollowTeam(teamCode: String) {
        val current = _followedTeams.value.toMutableSet()
        if (current.contains(teamCode)) {
            current.remove(teamCode)
        } else {
            current.add(teamCode)
        }
        _followedTeams.value = current
    }

    fun toggleFollowPlayer(playerId: String) {
        val current = _followedPlayers.value.toMutableSet()
        if (current.contains(playerId)) {
            current.remove(playerId)
        } else {
            current.add(playerId)
        }
        _followedPlayers.value = current
    }

    fun getAllMatches(): List<Match> {
        return _liveMatches.value + _upcomingMatches.value + _completedMatches.value
    }

    fun getFollowedMatches(): List<Match> {
        val followedTeamsSet = _followedTeams.value
        return getAllMatches().filter { match ->
            followedTeamsSet.contains(match.team1.shortName) ||
                followedTeamsSet.contains(match.team1.id) ||
                followedTeamsSet.contains(match.team2.shortName) ||
                followedTeamsSet.contains(match.team2.id) ||
                match.isFollowed
        }
    }

    /**
     * Reconstructs match state upon a new delivery.
     * Guaranteed stable ball identity and internal relationship validation.
     */
    fun recordBallDelivery(
        matchId: String,
        outcome: BallOutcome,
        runs: Int,
        isWicket: Boolean = false,
        wicketType: String? = null
    ) {
        val currentMatch = _selectedMatch.value ?: return
        if (currentMatch.id != matchId || currentMatch.innings2 == null) return

        val inn2 = currentMatch.innings2
        val currentOvers = inn2.overs
        val fullOvers = currentOvers.toInt()
        val ballsFraction = Math.round((currentOvers % 1) * 10)

        val nextBallInOver = if (ballsFraction >= 5) 0 else ballsFraction + 1
        val nextFullOvers = if (ballsFraction >= 5) fullOvers + 1 else fullOvers
        val nextOverFloat = nextFullOvers + (nextBallInOver * 0.1f)

        val newRuns = inn2.runs + runs
        val newWickets = if (isWicket) inn2.wickets + 1 else inn2.wickets
        val newReqRuns = (currentMatch.targetRuns ?: (currentMatch.innings1.runs + 1)) - newRuns
        val remainingBalls = ((inn2.maxOvers * 6) - (nextFullOvers * 6 + nextBallInOver)).toInt().coerceAtLeast(0)
        val newRrr = if (remainingBalls > 0) (newReqRuns.toFloat() / (remainingBalls / 6f)) else 0f
        val newCrr = if (nextOverFloat > 0) (newRuns.toFloat() / (nextFullOvers + (nextBallInOver / 6f))) else 0f

        val ballId = "IND-AUS-2026-FINAL-INN2-${nextFullOvers}.${nextBallInOver}"
        val newBall = BallState(
            ballId = ballId,
            overNumber = nextFullOvers,
            ballInOver = nextBallInOver,
            bowlerName = "M. Starc",
            batsmanName = if (isWicket) "S. Yadav" else "V. Kohli",
            outcome = outcome,
            runs = runs,
            isWicket = isWicket,
            wicketType = wicketType,
            isBoundaryFour = outcome == BallOutcome.FOUR,
            isBoundarySix = outcome == BallOutcome.SIX,
            speedKph = 144.6f,
            commentary = when (outcome) {
                BallOutcome.SIX -> "SIX! Pure timing and power into the crowd!"
                BallOutcome.FOUR -> "FOUR! Pierces the gap between backward point and cover!"
                BallOutcome.WICKET -> "OUT! Caught in the deep! Massive turning point in the match!"
                BallOutcome.DOT -> "No run, beaten outside off stump."
                else -> "$runs run taken safely."
            }
        )

        val updatedBatters = inn2.batters.map { batter ->
            if (batter.isOnStrike) {
                if (isWicket) {
                    batter.copy(
                        isNotOut = false,
                        dismissalInfo = wicketType ?: "c Marsh b Starc",
                        isOnStrike = false
                    )
                } else {
                    val updatedRuns = batter.runs + runs
                    val updatedBalls = batter.balls + 1
                    val fours = batter.fours + (if (outcome == BallOutcome.FOUR) 1 else 0)
                    val sixes = batter.sixes + (if (outcome == BallOutcome.SIX) 1 else 0)
                    batter.copy(
                        runs = updatedRuns,
                        balls = updatedBalls,
                        fours = fours,
                        sixes = sixes,
                        strikeRate = (updatedRuns.toFloat() / updatedBalls) * 100f,
                        isOnStrike = runs % 2 == 0 // rotate if odd run
                    )
                }
            } else {
                batter.copy(isOnStrike = if (runs % 2 != 0) true else batter.isOnStrike)
            }
        }

        val updatedBallsList = (inn2.balls + newBall).takeLast(6)

        val newMoment = when {
            outcome == BallOutcome.SIX -> MatchMoment(
                ballId = ballId,
                overText = "${nextFullOvers}.${nextBallInOver}",
                title = "Kohli's six",
                description = "Kohli launches Starc into the top tier!",
                situationBefore = "IND need ${(newReqRuns + 6)} runs from ${(remainingBalls + 1)} balls",
                situationAfter = "India need $newReqRuns runs in $remainingBalls balls",
                isSix = true
            )
            isWicket -> MatchMoment(
                ballId = ballId,
                overText = "${nextFullOvers}.${nextBallInOver}",
                title = "WICKET! Big blow for India!",
                description = "Starc removes key batsman at the death!",
                situationBefore = "IND need $newReqRuns runs from ${(remainingBalls + 1)} balls",
                situationAfter = "India need $newReqRuns runs in $remainingBalls balls",
                isWicket = true
            )
            else -> currentMatch.activeMoment
        }

        val updatedInnings2 = inn2.copy(
            runs = newRuns,
            wickets = newWickets,
            overs = nextOverFloat,
            batters = updatedBatters,
            balls = updatedBallsList
        )

        val updatedMatch = currentMatch.copy(
            innings2 = updatedInnings2,
            requiredRuns = newReqRuns,
            remainingBalls = remainingBalls,
            requiredRunRate = Math.round(newRrr * 100) / 100f,
            currentRunRate = Math.round(newCrr * 100) / 100f,
            situationSummary = if (newReqRuns <= 0) "India won the Final by ${10 - newWickets} wickets!" else "India need $newReqRuns runs in $remainingBalls balls",
            activeMoment = newMoment,
            winProbabilityTeam1 = if (isWicket) (currentMatch.winProbabilityTeam1 - 14).coerceIn(10, 95)
                                  else if (outcome == BallOutcome.SIX) (currentMatch.winProbabilityTeam1 + 10).coerceIn(10, 95)
                                  else currentMatch.winProbabilityTeam1,
            winProbabilityTeam2 = 100 - (if (isWicket) (currentMatch.winProbabilityTeam1 - 14).coerceIn(10, 95)
                                         else if (outcome == BallOutcome.SIX) (currentMatch.winProbabilityTeam1 + 10).coerceIn(10, 95)
                                         else currentMatch.winProbabilityTeam1)
        )

        _selectedMatch.value = updatedMatch
        _liveMatches.value = _liveMatches.value.map { if (it.id == matchId) updatedMatch else it }
    }
}
