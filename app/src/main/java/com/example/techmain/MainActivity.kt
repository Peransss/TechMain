package com.example.techmain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.ui.battle.BattleMainScreen
import com.example.techmain.ui.flashcard.FlashcardScreen
import com.example.techmain.ui.leaderboard.LeaderboardScreen
import com.example.techmain.ui.navigation.Screen
import com.example.techmain.ui.pomodoro.PomodoroScreen
import com.example.techmain.ui.quest.QuestScreen
import com.example.techmain.ui.shop.ShopScreen
import com.example.techmain.ui.theme.TechMainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechMainTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var isSignedIn by remember { mutableStateOf(false) }
    var isSigningIn by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!FirebaseModule.isSignedIn()) {
            FirebaseModule.signInAnonymously()
        }
        isSignedIn = FirebaseModule.isSignedIn()
        isSigningIn = false
    }

    if (isSigningIn) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val screens = listOf(
        Screen.Quest,
        Screen.Pomodoro,
        Screen.Flashcard,
        Screen.Battle,
        Screen.Shop
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Quest.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Quest.route) { QuestScreen() }
            composable(Screen.Pomodoro.route) { PomodoroScreen() }
            composable(Screen.Flashcard.route) { FlashcardScreen() }
            composable(Screen.Shop.route) { ShopScreen() }
            composable(Screen.Battle.route) {
                BattleMainScreen(
                    onNavigateToLeaderboard = {
                        navController.navigate(Screen.Leaderboard.route)
                    }
                )
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
