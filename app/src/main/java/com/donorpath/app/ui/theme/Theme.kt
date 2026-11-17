package com.donorpath.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SeedGreen = Color(0xFF1B5E20)
private val SeedGreenLight = Color(0xFF4C8C4A)
private val SeedGold = Color(0xFFC9A227)

private val LightColors = lightColorScheme(
    primary = SeedGreen,
    secondary = SeedGold,
    tertiary = SeedGreenLight
)

private val DarkColors = darkColorScheme(
    primary = SeedGreenLight,
    secondary = SeedGold,
    tertiary = SeedGreen
)

@Composable
fun DonorPathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
