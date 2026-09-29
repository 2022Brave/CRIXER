package com.example.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.AppDisplayMode
import com.example.presentation.components.CrixerLiquidGlassSegmentedControl
import com.example.presentation.components.LiquidGlassSegmentItem
import com.example.presentation.components.LiquidGlassTransition
import com.example.presentation.components.SegmentDensity
import com.example.presentation.screens.CricketGlanceScreen
import com.example.presentation.screens.HomeScreen
import com.example.presentation.screens.LeanBackScreen
import com.example.presentation.screens.LiveMatchScreen
import com.example.presentation.screens.MatchesScreen
import com.example.presentation.screens.SettingsScreen
import com.example.presentation.screens.SplashScreen
import com.example.presentation.viewmodel.CrixerScreen
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

@Composable
fun CrixerApp(
    viewModel: CrixerViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isShowingTransition by viewModel.isShowingTransition.collectAsState()
    val isRepeatVisit by viewModel.isRepeatVisit.collectAsState()
    val selectedMatch by viewModel.selectedMatch.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    val isSimpleMode = userSettings.displayMode == AppDisplayMode.SIMPLE

    // Background color animation between Simple (pure obsidian) and Immersive (liquid atmosphere)
    val immersiveAtmosphereAlpha by animateFloatAsState(
        targetValue = if (isSimpleMode) 0f else 1f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "atmosphereAlpha"
    )

    // Back handling
    BackHandler(enabled = currentScreen != CrixerScreen.HOME && currentScreen != CrixerScreen.SPLASH) {
        when (currentScreen) {
            CrixerScreen.LEAN_BACK -> viewModel.navigateTo(CrixerScreen.MATCH_DETAIL)
            CrixerScreen.MATCH_DETAIL -> viewModel.navigateTo(CrixerScreen.HOME)
            else -> viewModel.navigateTo(CrixerScreen.HOME)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CrixerBlack)
    ) {
        // Continuous Atmospheric Surface extending right behind the Status Bar
        if (immersiveAtmosphereAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(immersiveAtmosphereAlpha)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F1E36), // Deep stadium ambient blue
                                Color(0xFF091220),
                                Color(0xFF05070A)
                            ),
                            startY = 0f,
                            endY = 1200f
                        )
                    )
            )
        }

        // Top Status Bar protective subtle vignette to ensure 100% legibility of system icons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isSimpleMode) Color(0xD905070A) else Color(0x66000000),
                            Color.Transparent
                        )
                    )
                )
        )

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = Color.Transparent,
            bottomBar = {
                // Show bottom bar only on primary app destinations
                val showBottomNav = currentScreen != CrixerScreen.SPLASH &&
                    currentScreen != CrixerScreen.LEAN_BACK &&
                    !isShowingTransition

                if (showBottomNav) {
                    CrixerBottomNavigation(
                        currentScreen = currentScreen,
                        displayMode = userSettings.displayMode,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSelectMode = { viewModel.setDisplayMode(it) },
                        onToggleMode = { viewModel.toggleDisplayMode() }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                    label = "screenTransition"
                ) { screen ->
                    when (screen) {
                        CrixerScreen.SPLASH -> SplashScreen(
                            onSplashFinished = { viewModel.navigateTo(CrixerScreen.HOME) }
                        )
                        CrixerScreen.HOME -> HomeScreen(viewModel = viewModel)
                        CrixerScreen.MATCHES -> MatchesScreen(viewModel = viewModel)
                        CrixerScreen.MATCH_DETAIL -> LiveMatchScreen(viewModel = viewModel)
                        CrixerScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        CrixerScreen.LEAN_BACK -> LeanBackScreen(viewModel = viewModel)
                        CrixerScreen.CRICKET_GLANCE -> CricketGlanceScreen(viewModel = viewModel)
                    }
                }

                // Signature Liquid Glass Match Entry Transition Overlay
                if (isShowingTransition && selectedMatch != null) {
                    LiquidGlassTransition(
                        match = selectedMatch!!,
                        isRepeatVisit = isRepeatVisit,
                        onTransitionFinished = { viewModel.onTransitionFinished() }
                    )
                }
            }
        }
    }
}

/**
 * Approved CRIXER V2 Bottom Navigation:
 * HOME | MATCHES | [LITE / PRO TOGGLE] | SETTINGS
 */
@Composable
private fun CrixerBottomNavigation(
    currentScreen: CrixerScreen,
    displayMode: AppDisplayMode,
    onNavigate: (CrixerScreen) -> Unit,
    onSelectMode: (AppDisplayMode) -> Unit,
    onToggleMode: () -> Unit
) {
    val isSimpleMode = displayMode == AppDisplayMode.SIMPLE

    // Floating full-width Liquid Glass pill, matching the Home segmented-control language.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xE6080E17))
                .border(
                    width = 0.8.dp,
                    color = Color(0xFF243449),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 5.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        // 1. HOME
        BottomNavItem(
            modifier = Modifier.weight(1f),
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            isSelected = currentScreen == CrixerScreen.HOME,
            onClick = { onNavigate(CrixerScreen.HOME) }
        )

        // 2. MATCHES
        BottomNavItem(
            modifier = Modifier.weight(1f),
            label = "Matches",
            selectedIcon = Icons.Filled.SportsCricket,
            unselectedIcon = Icons.Outlined.SportsCricket,
            isSelected = currentScreen == CrixerScreen.MATCHES || currentScreen == CrixerScreen.MATCH_DETAIL,
            onClick = { onNavigate(CrixerScreen.MATCHES) }
        )

        // 3. CENTER TOGGLE: Unmistakable SIMPLE / IMMERSIVE Mode Selector
        Box(modifier = Modifier.weight(1.35f), contentAlignment = Alignment.Center) {
            ModeSwitchButton(
                isSimpleMode = isSimpleMode,
                onSelectMode = onSelectMode
            )
        }

        // 5. SETTINGS
        BottomNavItem(
            modifier = Modifier.weight(1f),
            label = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            isSelected = currentScreen == CrixerScreen.SETTINGS,
            onClick = { onNavigate(CrixerScreen.SETTINGS) }
        )
        }
    }
}

/**
 * CRIXER Liquid Glass Switch (Reference: liquidglassdesign.com/gallery/liquid-glass-switch):
 * Compact pill-shaped control with a single movable Liquid Glass selection surface.
 * Powered by CrixerLiquidGlassSegmentedControl for unified physical interaction language.
 */
@Composable
private fun ModeSwitchButton(
    isSimpleMode: Boolean,
    onSelectMode: (AppDisplayMode) -> Unit
) {
    val modeItems = remember {
        listOf(
            LiquidGlassSegmentItem(
                key = AppDisplayMode.SIMPLE,
                label = "LITE",
                accentColor = Color(0xFF64748B),
                testTag = "mode_switch_simple"
            ),
            LiquidGlassSegmentItem(
                key = AppDisplayMode.IMMERSIVE,
                label = "PRO",
                accentColor = Color(0xFF38BDF8),
                testTag = "mode_switch_immersive"
            )
        )
    }

    CrixerLiquidGlassSegmentedControl(
        items = modeItems,
        selectedKey = if (isSimpleMode) AppDisplayMode.SIMPLE else AppDisplayMode.IMMERSIVE,
        onItemSelected = onSelectMode,
        isSimpleMode = isSimpleMode,
        density = SegmentDensity.COMPACT,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mode_toggle_bottom_bar")
    )
}

@Composable
private fun BottomNavItem(
    modifier: Modifier = Modifier,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) CrixerWhite else CrixerTextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) CrixerWhite else CrixerTextSecondary,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
