package com.example.techmain.ui.battle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.ui.leaderboard.LeaderboardScreen

@Composable
fun BattleMainScreen(
    onNavigateToLeaderboard: () -> Unit,
    viewModel: BattleViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    when (state.screen) {
        BattleScreen.LOBBY -> BattleLobbyScreen(
            viewModel = viewModel,
            onNavigateToLeaderboard = onNavigateToLeaderboard
        )
        BattleScreen.MATCHMAKING -> MatchmakingScreen(viewModel = viewModel)
        BattleScreen.GAME -> BattleGameScreen(viewModel = viewModel)
        BattleScreen.RESULT -> BattleResultScreen(viewModel = viewModel)
    }
}
