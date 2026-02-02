package com.example.battimuro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NeonColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonMagenta,
    tertiary = NeonGreen,
    background = DarkBackground,
    surface = NeonGray,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = DarkBackground,
    onBackground = NeonCyan,
    onSurface = NeonGreen
)

@Composable
fun BattimuroTheme(
    // Force dark theme for the game feeling
    darkTheme: Boolean = true,
    // Disable dynamic color to maintain the neon aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = NeonColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}