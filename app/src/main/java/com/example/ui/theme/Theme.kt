package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = NeonGreen,
        secondary = NeonPurple,
        tertiary = NeonPurple,
        background = PureBlack,
        surface = DarkGray,
        surfaceVariant = LightGray,
        onPrimary = PureBlack,
        onSecondary = TextPrimary,
        onTertiary = TextPrimary,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF6200EE), // Rich Indigo primary
        secondary = Color(0xFF03DAC6),
        tertiary = Color(0xFF3700B3),
        background = Color(0xFFF9F9FB), // Clean off-white
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFEAEAEE), // Dynamic replacement for LightGray container
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onTertiary = Color(0xFFFFFFFF),
        onBackground = Color(0xFF121212),
        onSurface = Color(0xFF121212),
        onSurfaceVariant = Color(0xFF666666),
    )

@Composable
fun MyApplicationTheme(
    theme: String = "Follow System",
    content: @Composable () -> Unit,
) {
    val darkTheme = when (theme) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }
    
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
