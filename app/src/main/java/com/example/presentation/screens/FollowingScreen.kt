package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.CrixerViewModel
import com.example.ui.theme.CrixerBlack
import com.example.ui.theme.CrixerTextSecondary

/**
 * Following was removed from the primary CRIXER navigation.
 * This legacy screen intentionally contains no static player/team data.
 */
@Composable
fun FollowingScreen(
    viewModel: CrixerViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CrixerBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Following is not available in this version.",
            color = CrixerTextSecondary,
            fontSize = 13.sp
        )
    }
}
