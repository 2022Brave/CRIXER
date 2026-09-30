package com.example.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.AppDisplayMode
import com.example.presentation.components.CrixerBottomBar
import com.example.presentation.components.LiquidGlassTransition
import com.example.presentation.screens.CricketGlanceScreen
import com.example.presentation.screens.HomeScreen
import com.example.presentation.screens.LeanBackScreen
import com.example.presentation.screens.LiveMatchScreen
import com.example.presentation.screens.MatchesScreen
import com.example.presentation.screens.SettingsScreen
import com.example.presentation.screens.SplashScreen
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.presentation.viewmodel.HomeTab
import com.example.ui.theme.CrixerBlack

@Composable
fun CrixerApp(viewModel: CrixerViewModel = viewModel()) {
    val screen by viewModel.currentScreen.collectAsState()
    val transition by viewModel.isShowingTransition.collectAsState()
    val selectedMatch by viewModel.selectedMatch.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val repeatVisit by viewModel.isRepeatVisit.collectAsState()
    val isPro = settings.displayMode == AppDisplayMode.PRO

    BackHandler(enabled = screen != CrixerScreen.HOME && screen != CrixerScreen.SPLASH) {
        when (screen) {
            CrixerScreen.MATCH_DETAIL -> viewModel.navigateTo(CrixerScreen.MATCHES)
            CrixerScreen.LEAN_BACK -> viewModel.navigateTo(CrixerScreen.MATCH_DETAIL)
            else -> viewModel.navigateTo(CrixerScreen.HOME)
        }
    }

    Box(Modifier.fillMaxSize().background(CrixerBlack)) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = Color.Transparent,
            bottomBar = {
                val showBar = screen != CrixerScreen.SPLASH &&
                    screen != CrixerScreen.MATCH_DETAIL &&
                    screen != CrixerScreen.LEAN_BACK &&
                    !transition
                if (showBar) {
                    CrixerBottomBar(screen, viewModel::navigateTo, isPro)
                }
            }
        ) { paddingValues ->
            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                AnimatedContent(
                    targetState = screen,
                    transitionSpec = {
                        fadeIn(androidx.compose.animation.core.tween(180)) togetherWith
                            fadeOut(androidx.compose.animation.core.tween(120))
                    },
                    label = "crixerScreen"
                ) { destination ->
                    when (destination) {
                        CrixerScreen.SPLASH -> SplashScreen { viewModel.navigateTo(CrixerScreen.HOME) }
                        CrixerScreen.HOME -> HomeScreen(viewModel)
                        CrixerScreen.LIVE -> HomeScreen(viewModel, fixedTab = HomeTab.LIVE)
                        CrixerScreen.MATCHES -> MatchesScreen(viewModel)
                        CrixerScreen.INDIA -> HomeScreen(
                            viewModel,
                            teamCodeFilter = "IND",
                            sectionTitle = "INDIA"
                        )
                        CrixerScreen.MATCH_DETAIL -> LiveMatchScreen(viewModel)
                        CrixerScreen.SETTINGS -> SettingsScreen(viewModel)
                        CrixerScreen.LEAN_BACK -> LeanBackScreen(viewModel)
                        CrixerScreen.CRICKET_GLANCE -> CricketGlanceScreen(viewModel)
                    }
                }
                if (transition && selectedMatch != null) {
                    LiquidGlassTransition(
                        match = selectedMatch!!,
                        isRepeatVisit = repeatVisit,
                        onTransitionFinished = viewModel::onTransitionFinished
                    )
                }
            }
        }
    }
}
