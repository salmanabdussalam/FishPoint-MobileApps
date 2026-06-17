package com.app.fishpoint.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FishPointColorScheme = lightColorScheme(
    primary          = FishGreen,
    onPrimary        = CardBackground,
    primaryContainer = FishGreenSurface,
    secondary        = FishGreenDark,
    background       = PageBackground,
    surface          = CardBackground,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    error            = ErrorRed,
)

@Composable
fun FishPointTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FishPointColorScheme,
        content = content,
    )
}