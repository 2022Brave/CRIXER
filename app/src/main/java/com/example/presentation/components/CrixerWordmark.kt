package com.example.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrixerRed
import com.example.ui.theme.CrixerTextSecondary
import com.example.ui.theme.CrixerWhite

@Composable
fun CrixerWordmark(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false,
    fontSize: Int = 22
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CRI",
                color = CrixerWhite,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "X",
                color = CrixerRed,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "ER",
                color = CrixerWhite,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        if (showTagline) {
            Text(
                text = "EVERY  BALL  COUNTS",
                color = CrixerTextSecondary,
                fontSize = (fontSize * 0.42).sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.5.sp
            )
        }
    }
}
