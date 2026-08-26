package com.ardabank.aradapay.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BankLightColorScheme = lightColorScheme(
    primary = PrimaryEmerald,
    onPrimary = Color.White,
    primaryContainer = PrimaryEmeraldContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = ShinyCyan,
    onSecondary = Color.White,
    secondaryContainer = ShinyCyanContainer,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentAmber,
    error = AccentRose,
    onError = Color.White,
    errorContainer = AccentRoseContainer,
    onErrorContainer = OnAccentRoseContainer,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainerLow,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    outlineVariant = SurfaceBorder,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceWhite,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = Color(0xFFCBD5E1)
)

@Composable
fun AradaPayTheme(content: @Composable () -> Unit) {
    val colorScheme = BankLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Dark icons for light theme status bar & navigation bar
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
