package com.example.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.CrixerScreen
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

private data class CrixerNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val icon: ImageVector,
    val screen: CrixerScreen
)

@Composable
fun CrixerBottomBar(
    currentScreen: CrixerScreen,
    onNavigate: (CrixerScreen) -> Unit,
    isPro: Boolean
) {
    val items = listOf(
        CrixerNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, CrixerScreen.HOME),
        CrixerNavItem("Live", Icons.Filled.SportsCricket, Icons.Outlined.SportsCricket, CrixerScreen.LIVE),
        CrixerNavItem("Matches", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, CrixerScreen.MATCHES),
        CrixerNavItem("India", Icons.Filled.Public, Icons.Outlined.Public, CrixerScreen.INDIA),
        CrixerNavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, CrixerScreen.SETTINGS)
    )

    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    if (isPro) Color(0xCC101722) else Color(0xF20A0D12),
                    RoundedCornerShape(24.dp)
                )
                .border(
                    1.dp,
                    if (isPro) Color(0x38FFFFFF) else Color(0x241F2937),
                    RoundedCornerShape(24.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentScreen == item.screen ||
                    (currentScreen == CrixerScreen.MATCH_DETAIL && item.screen == CrixerScreen.MATCHES)
                Box(
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { onNavigate(item.screen) },
                    contentAlignment = Alignment.Center
                ) {
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (selected) 52.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = 0.78f,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "navLensWidth"
                    )

                    if (indicatorWidth > 0.dp) {
                        Box(
                            Modifier
                                .size(width = indicatorWidth, height = 48.dp)
                                .background(
                                    Color(0x241F334A),
                                    RoundedCornerShape(18.dp)
                                )
                                .border(
                                    0.8.dp,
                                    if (isPro) Color(0x55FFFFFF) else Color(0x2A475569),
                                    RoundedCornerShape(18.dp)
                                )
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                            tint = if (selected) CrixerWhite else CrixerTextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(Modifier.size(2.dp))
                        Text(
                            item.label,
                            color = if (selected) CrixerWhite else CrixerTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
