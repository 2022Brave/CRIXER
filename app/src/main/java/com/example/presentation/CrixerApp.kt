package com.example.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

@Composable
fun CrixerApp(viewModel: CrixerViewModel = viewModel()) {
    val screen by viewModel.currentScreen.collectAsState()
    val transition by viewModel.isShowingTransition.collectAsState()
    val selectedMatch by viewModel.selectedMatch.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val repeatVisit by viewModel.isRepeatVisit.collectAsState()

    BackHandler(enabled = screen != CrixerScreen.HOME && screen != CrixerScreen.SPLASH) {
        when (screen) {
            CrixerScreen.MATCH_DETAIL -> viewModel.navigateTo(CrixerScreen.MATCHES)
            CrixerScreen.LEAN_BACK -> viewModel.navigateTo(CrixerScreen.MATCH_DETAIL)
            else -> viewModel.navigateTo(CrixerScreen.HOME)
        }
    }

    Box(Modifier.fillMaxSize().background(CrixerBlack)) {
        if (settings.displayMode.name == "IMMERSIVE") {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFF091426), Color(0xFF05080D), CrixerBlack))
                )
            )
        }
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = Color.Transparent,
            bottomBar = {
                if (screen != CrixerScreen.SPLASH && screen != CrixerScreen.MATCH_DETAIL &&
                    screen != CrixerScreen.LEAN_BACK && !transition) {
                    CrixerBottomBar(screen, viewModel)
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                AnimatedContent(
                    targetState = screen,
                    transitionSpec = {
                        fadeIn(androidx.compose.animation.core.tween(180)) togetherWith
                            fadeOut(androidx.compose.animation.core.tween(120))
                    },
                    label = "crixerScreen"
                ) { destination ->
                    when (destination) {
                        CrixerScreen.SPLASH -> SplashScreen(onSplashFinished = { viewModel.navigateTo(CrixerScreen.HOME) })
                        CrixerScreen.HOME -> HomeScreen(viewModel)
                        CrixerScreen.LIVE -> HomeScreen(viewModel, fixedTab = HomeTab.LIVE)
                        CrixerScreen.MATCHES -> MatchesScreen(viewModel)
                        CrixerScreen.INDIA -> HomeScreen(
                            viewModel, teamCodeFilter = "IND", sectionTitle = "INDIA"
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

@Composable
private fun CrixerBottomBar(screen: CrixerScreen, viewModel: CrixerViewModel) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, CrixerScreen.HOME),
        NavItem("Live", Icons.Filled.SportsCricket, Icons.Outlined.SportsCricket, CrixerScreen.LIVE),
        NavItem("Matches", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, CrixerScreen.MATCHES),
        NavItem("India", Icons.Filled.Public, Icons.Outlined.Public, CrixerScreen.INDIA),
        NavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, CrixerScreen.SETTINGS)
    )
    Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 6.dp)) {
        Row(
            Modifier.fillMaxWidth().background(Color(0xD90A1018), RoundedCornerShape(28.dp))
                .border(1.dp, Color(0x332F435B), RoundedCornerShape(28.dp))
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = screen == item.screen ||
                    (screen == CrixerScreen.MATCH_DETAIL && item.screen == CrixerScreen.MATCHES)
                Column(
                    Modifier.weight(1f).clickable { viewModel.navigateTo(item.screen) }
                        .padding(vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        if (selected) item.selected else item.unselected,
                        contentDescription = item.label,
                        tint = if (selected) CrixerWhite else CrixerTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(Modifier.size(2.dp))
                    Text(
                        item.label,
                        color = if (selected) CrixerWhite else CrixerTextSecondary,
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

private data class NavItem(
    val label: String,
    val selected: ImageVector,
    val unselected: ImageVector,
    val screen: CrixerScreen
)
