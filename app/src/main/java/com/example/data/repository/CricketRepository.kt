package com.example.data.repository

import com.example.data.local.CrixerDatabase
import com.example.data.provider.CricketDataProvider
import com.example.data.provider.VerifiedCricketDataProvider
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
    private val _followedTeams = MutableStateFlow<Set<String>>(emptySet())
    val followedTeams: StateFlow<Set<String>> = _followedTeams.asStateFlow()

    private val _followedPlayers = MutableStateFlow<Set<String>>(emptySet())
    val followedPlayers: StateFlow<Set<String>> = _followedPlayers.asStateFlow()

    suspend fun refreshData() {
        val live = dataProvider.getLiveMatches()
        val completed = dataProvider.getCompletedMatches()
        val upcoming = dataProvider.getUpcomingMatches()

        _liveMatches.value = live
        _completedMatches.value = completed
        _upcomingMatches.value = upcoming

        // A previously selected live match must not survive a refresh when it is no longer live.
        if (live.isEmpty() && _selectedMatch.value?.status == MatchStatus.LIVE) {
            _selectedMatch.value = null
        } else if (_selectedMatch.value == null && live.isNotEmpty()) {
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


}