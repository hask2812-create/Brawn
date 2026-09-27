package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrawnBlueLight,
    onPrimary = Color.White,
    primaryContainer = BrawnBlueMedium,
    onPrimaryContainer = Color.White,
    secondary = BrawnEmeraldAccent,
    onSecondary = Color.White,
    secondaryContainer = BrawnEmeraldGreen,
    onSecondaryContainer = Color.White,
    tertiary = BrawnCyanAccent,
    background = BrawnNavyDarkBackground,
    onBackground = BrawnDarkTextPrimary,
    surface = BrawnNavyDarkSurface,
    onSurface = BrawnDarkTextPrimary,
    surfaceVariant = BrawnNavyDarkCard,
    onSurfaceVariant = BrawnDarkTextSecondary,
    outline = BrawnDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = BrawnNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = BrawnNavyPrimary,
    secondary = BrawnEmeraldGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2F1),
    onSecondaryContainer = BrawnEmeraldGreen,
    tertiary = BrawnCyanAccent,
    background = BrawnBackgroundLight,
    onBackground = BrawnTextPrimary,
    surface = BrawnSurfaceLight,
    onSurface = BrawnTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = BrawnTextSecondary,
    outline = BrawnCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our crisp BRAWN branding by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
