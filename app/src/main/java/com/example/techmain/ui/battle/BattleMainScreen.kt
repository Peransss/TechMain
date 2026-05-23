package com.example.techmain.ui.battle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BattleMainScreen(viewModel: BattleViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.init() }

    when (state.screen) {
        BattleScreen.LOBBY -> BattleLobbyScreen(viewModel = viewModel)
        BattleScreen.JOIN_ROOM -> BattleLobbyScreen(viewModel = viewModel)
        BattleScreen.WAITING_ROOM -> BattleLobbyScreen(viewModel = viewModel)
        BattleScreen.GAME -> BattleGameScreen(viewModel = viewModel)
        BattleScreen.RESULT -> BattleResultScreen(viewModel = viewModel)
        BattleScreen.SOLO_PRACTICE -> BattleLobbyScreen(viewModel = viewModel)
    }
}
