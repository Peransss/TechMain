package com.example.techmain.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Battle : Screen("battle", "Battle", Icons.Default.SportsKabaddi)
    data object Studio : Screen("studio", "Studio", Icons.Default.Palette)
    data object Leaderboard : Screen("leaderboard", "Peringkat", Icons.Default.EmojiEvents)
    data object Profile : Screen("profile", "Profil", Icons.Default.Person)
    data object Wizard : Screen("wizard", "Wizard", Icons.Default.Palette)
}
