package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppDisplayMode {
    SIMPLE,
    IMMERSIVE
}

data class UserSettings(
    val displayMode: AppDisplayMode = AppDisplayMode.IMMERSIVE,
    val liveUpdatesEnabled: Boolean = true,
    val cricketGlanceEnabled: Boolean = true,
    val notifyMatchEvents: Boolean = true,
    val notifyMilestones: Boolean = true,
    val notifyResults: Boolean = true,
    val wifiOnlyData: Boolean = false,
    val reducedMotion: Boolean = false,
    val hapticFeedback: Boolean = true
)

class SettingsRepository {
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    fun updateDisplayMode(mode: AppDisplayMode) {
        _settings.value = _settings.value.copy(displayMode = mode)
    }

    fun toggleLiveUpdates(enabled: Boolean) {
        _settings.value = _settings.value.copy(liveUpdatesEnabled = enabled)
    }

    fun toggleCricketGlance(enabled: Boolean) {
        _settings.value = _settings.value.copy(cricketGlanceEnabled = enabled)
    }

    fun toggleNotifyEvents(enabled: Boolean) {
        _settings.value = _settings.value.copy(notifyMatchEvents = enabled)
    }

    fun toggleNotifyMilestones(enabled: Boolean) {
        _settings.value = _settings.value.copy(notifyMilestones = enabled)
    }

    fun toggleNotifyResults(enabled: Boolean) {
        _settings.value = _settings.value.copy(notifyResults = enabled)
    }

    fun toggleReducedMotion(enabled: Boolean) {
        _settings.value = _settings.value.copy(reducedMotion = enabled)
    }

    fun toggleHaptics(enabled: Boolean) {
        _settings.value = _settings.value.copy(hapticFeedback = enabled)
    }
}
