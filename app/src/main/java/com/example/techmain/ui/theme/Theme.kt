package com.example.techmain.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = CyberTextPrimary,
    secondary = CyberSecondary,
    onSecondary = CyberTextPrimary,
    tertiary = CyberAccent,
    onTertiary = CyberTextPrimary,
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurface,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberSurfaceBorder,
    error = CyberError,
    onError = CyberTextPrimary
)

@Composable
fun TechMainTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
