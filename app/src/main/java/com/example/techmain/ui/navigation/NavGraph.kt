package com.example.techmain.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Quest : Screen("quest", "Quest", Icons.Default.Checklist)
    data object Pomodoro : Screen("pomodoro", "Fokus", Icons.Default.Timer)
    data object Flashcard : Screen("flashcard", "Kartu", Icons.Default.Style)
    data object Shop : Screen("shop", "Toko", Icons.Default.ShoppingCart)
}
