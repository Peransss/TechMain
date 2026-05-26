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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.ui.battle.BattleMainScreen
import com.example.techmain.ui.leaderboard.LeaderboardScreen
import com.example.techmain.ui.navigation.Screen
import com.example.techmain.ui.profile.ProfileScreen
import com.example.techmain.ui.studio.CreatorWizardScreen
import com.example.techmain.ui.studio.StudioScreen
import com.example.techmain.ui.theme.TechMainTheme
import com.example.techmain.ui.theme.CyberPrimary
import com.example.techmain.ui.theme.CyberTextSecondary
import com.example.techmain.ui.theme.CyberBackground
import androidx.compose.material3.NavigationBarItemDefaults

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechMainTheme { MainScreen() }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var isSigningIn by remember { mutableStateOf(true) }
    var signInError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!FirebaseModule.isSignedIn()) {
            val result = FirebaseModule.signInAnonymously()
            result.onFailure {
                signInError = true
            }
        }
        isSigningIn = false
    }

    if (isSigningIn || signInError) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CyberPrimary)
        }
        return
    }

    val screens = listOf(Screen.Battle, Screen.Studio, Screen.Leaderboard, Screen.Profile)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = screens.any { it.route == currentDestination?.route }

            if (showBottomBar) {
                NavigationBar(
                    containerColor = CyberBackground,
                    contentColor = CyberTextSecondary
                ) {
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title, tint = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) CyberPrimary else CyberTextSecondary) },
                            label = { Text(screen.title, color = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) CyberPrimary else CyberTextSecondary) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = CyberPrimary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Battle.route,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable(Screen.Battle.route) { BattleMainScreen() }
            composable(Screen.Studio.route) { 
                StudioScreen(onNavigateToWizard = { navController.navigate(Screen.Wizard.route) }) 
            }
            composable(Screen.Wizard.route) {
                CreatorWizardScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}
