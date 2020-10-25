package com.example.kmpstarter

import android.graphics.Color.parseColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = lightColors(
    primary = Color(parseColor("#008577")),
    primaryVariant = Color(parseColor("#00574B")),
    secondary = Color(parseColor("#D81B60"))
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = AppColors,
        content = content
    )
}