package com.example.techmain.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SoftNeonColorScheme = darkColorScheme(
    primary = NeonSlatePrimary,
    onPrimary = NeonSlateBackground,
    secondary = NeonSlateSecondary,
    onSecondary = NeonSlateBackground,
    tertiary = NeonSlateAccent,
    onTertiary = NeonSlateBackground,
    background = NeonSlateBackground,
    onBackground = NeonSlateTextPrimary,
    surface = NeonSlateSurface,
    onSurface = NeonSlateTextPrimary,
    surfaceVariant = NeonSlateSurface,
    onSurfaceVariant = NeonSlateTextSecondary,
    outline = NeonSlateSurfaceBorder,
    error = NeonSlateError,
    onError = NeonSlateBackground
)

@Composable
fun TechMainTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SoftNeonColorScheme,
        typography = Typography,
        content = content
    )
}
