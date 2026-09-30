package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AppDisplayMode
import com.example.data.repository.CricketRepository
import com.example.data.repository.SettingsRepository
import com.example.domain.model.Match
import com.example.domain.model.PlayerProfile
import com.example.presentation.components.LiquidGlassPulse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CrixerScreen {
    SPLASH,
    HOME,
    MATCHES,
    INDIA,
    MATCH_DETAIL,
    SETTINGS,
    LEAN_BACK,
    CRICKET_GLANCE
}

enum class HomeTab {
    LIVE,
    COMPLETED,
    UPCOMING
}

enum class MatchesFormatFilter {
    ALL,
    T20,
    ODI,
    TEST
}

enum class MatchDetailTab {
    OVERVIEW,
    COMMENTARY,
    SCORECARD,
    STATS,
    INSIGHTS
}

enum class InsightsSubTab {
    PLAYER_INSIGHTS,
    MATCH_INSIGHTS
}

class CrixerViewModel(
    private val repository: CricketRepository = CricketRepository(),
    private val settingsRepository: SettingsRepository = SettingsRepository()
) : ViewModel() {

    val liveMatches = repository.liveMatches
    val completedMatches = repository.completedMatches
    val upcomingMatches = repository.upcomingMatches
    val selectedMatch = repository.selectedMatch
    val validationState = repository.validationState
    val userSettings = settingsRepository.settings
    val followedTeams = repository.followedTeams
    val followedPlayers = repository.followedPlayers

    private val _currentScreen = MutableStateFlow(CrixerScreen.SPLASH)
    val currentScreen: StateFlow<CrixerScreen> = _currentScreen.asStateFlow()

    private val _homeTab = MutableStateFlow(HomeTab.LIVE)
    val homeTab: StateFlow<HomeTab> = _homeTab.asStateFlow()

    private val _formatFilter = MutableStateFlow(MatchesFormatFilter.ALL)
    val formatFilter: StateFlow<MatchesFormatFilter> = _formatFilter.asStateFlow()

    private val _matchDetailTab = MutableStateFlow(MatchDetailTab.OVERVIEW)
    val matchDetailTab: StateFlow<MatchDetailTab> = _matchDetailTab.asStateFlow()

    private val _insightsSubTab = MutableStateFlow(InsightsSubTab.PLAYER_INSIGHTS)
    val insightsSubTab: StateFlow<InsightsSubTab> = _insightsSubTab.asStateFlow()

    private val _visitedMatchIds = mutableSetOf<String>()
    private val _isRepeatVisit = MutableStateFlow(false)
    val isRepeatVisit: StateFlow<Boolean> = _isRepeatVisit.asStateFlow()

    private val _isShowingTransition = MutableStateFlow(false)
    val isShowingTransition: StateFlow<Boolean> = _isShowingTransition.asStateFlow()

    private val _liquidGlassPulse = MutableStateFlow(LiquidGlassPulse.NONE)
    val liquidGlassPulse: StateFlow<LiquidGlassPulse> = _liquidGlassPulse.asStateFlow()

    private val _selectedPlayerProfile = MutableStateFlow<PlayerProfile?>(null)
    val selectedPlayerProfile: StateFlow<PlayerProfile?> = _selectedPlayerProfile.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    init {
        viewModelScope.launch {
            repository.refreshData()
            _selectedPlayerProfile.value = repository.getPlayerProfile("ind-3")

            // Keep live scores fresh without requiring a manual pull-to-refresh.
            // The provider itself rejects stale/demo data.
            while (true) {
                delay(30_000)
                repository.refreshData()
            }
        }
    }

    fun navigateTo(screen: CrixerScreen) {
        _currentScreen.value = screen
    }

    fun setHomeTab(tab: HomeTab) {
        _homeTab.value = tab
    }

    fun setFormatFilter(filter: MatchesFormatFilter) {
        _formatFilter.value = filter
    }

    fun setMatchDetailTab(tab: MatchDetailTab) {
        _matchDetailTab.value = tab
    }

    fun setInsightsSubTab(tab: InsightsSubTab) {
        _insightsSubTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) _searchQuery.value = ""
    }

    fun onMatchClicked(match: Match) {
        repository.selectMatch(match.id)
        val isRepeat = _visitedMatchIds.contains(match.id)
        _isRepeatVisit.value = isRepeat
        _visitedMatchIds.add(match.id)

        if (!userSettings.value.reducedMotion) {
            // Trigger signature Liquid Glass transition (fast version on repeat visit)
            _isShowingTransition.value = true
        } else {
            _currentScreen.value = CrixerScreen.MATCH_DETAIL
        }
    }

    fun onTransitionFinished() {
        _isShowingTransition.value = false
        _currentScreen.value = CrixerScreen.MATCH_DETAIL
    }

    fun setDisplayMode(mode: AppDisplayMode) {
        settingsRepository.updateDisplayMode(mode)
    }

    fun toggleDisplayMode() {
        val newMode = if (userSettings.value.displayMode == AppDisplayMode.IMMERSIVE) {
            AppDisplayMode.SIMPLE
        } else {
            AppDisplayMode.IMMERSIVE
        }
        settingsRepository.updateDisplayMode(newMode)
    }

    fun toggleCricketGlance(enabled: Boolean) = settingsRepository.toggleCricketGlance(enabled)
    fun toggleLiveUpdates(enabled: Boolean) = settingsRepository.toggleLiveUpdates(enabled)
    fun toggleNotifyEvents(enabled: Boolean) = settingsRepository.toggleNotifyEvents(enabled)
    fun toggleNotifyMilestones(enabled: Boolean) = settingsRepository.toggleNotifyMilestones(enabled)
    fun toggleNotifyResults(enabled: Boolean) = settingsRepository.toggleNotifyResults(enabled)
    fun toggleReducedMotion(enabled: Boolean) = settingsRepository.toggleReducedMotion(enabled)
    fun toggleHaptics(enabled: Boolean) = settingsRepository.toggleHaptics(enabled)

    fun toggleFollowMatch(matchId: String, title: String) {
        repository.toggleFollowMatch(matchId, title)
    }

    fun toggleFollowTeam(teamCode: String) {
        repository.toggleFollowTeam(teamCode)
    }

    fun toggleFollowPlayer(playerId: String) {
        repository.toggleFollowPlayer(playerId)
    }

    fun getFollowedMatches(): List<Match> {
        return repository.getFollowedMatches()
    }

    fun getAllMatches(): List<Match> {
        return repository.getAllMatches()
    }

    fun loadPlayerProfile(playerId: String) {
        viewModelScope.launch {
            _selectedPlayerProfile.value = repository.getPlayerProfile(playerId)
        }
    }

    private fun triggerPulse(pulse: LiquidGlassPulse) {
        viewModelScope.launch {
            _liquidGlassPulse.value = pulse
            delay(1500)
            _liquidGlassPulse.value = LiquidGlassPulse.NONE
        }
    }
}