package com.example.techmain.ui.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import com.example.techmain.firebase.GameSession
import com.example.techmain.firebase.MatchmakingTicket
import com.example.techmain.firebase.QuestionBank
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BattleScreen {
    LOBBY, MATCHMAKING, GAME, RESULT
}

data class BattleUiState(
    val screen: BattleScreen = BattleScreen.LOBBY,
    val selectedCategory: String = "",
    val isSearching: Boolean = false,
    val ticketsCount: Int = 0,
    val gameId: String = "",
    val game: GameSession = GameSession(),
    val myUserId: String = "",
    val selectedAnswer: Int = -1,
    val hasAnswered: Boolean = false,
    val timeLeft: Int = 20,
    val errorMessage: String = ""
)

class BattleViewModel : ViewModel() {
    private val firestore = FirestoreService()
    private val _state = MutableStateFlow(BattleUiState())
    val state: StateFlow<BattleUiState> = _state.asStateFlow()

    private var gameListenerJob: Job? = null
    private var timerJob: Job? = null
    private var matchmakingListenerJob: Job? = null

    fun init() {
        val userId = FirebaseModule.getUserId() ?: return
        _state.value = _state.value.copy(myUserId = userId)
    }

    fun selectCategory(categoryId: String) {
        _state.value = _state.value.copy(selectedCategory = categoryId)
    }

    fun startMatchmaking() {
        val userId = FirebaseModule.getUserId() ?: return
        val categoryId = _state.value.selectedCategory
        if (categoryId.isEmpty()) return

        _state.value = _state.value.copy(isSearching = true, screen = BattleScreen.MATCHMAKING)

        viewModelScope.launch {
            try {
                val ticket = MatchmakingTicket(
                    userId = userId,
                    displayName = "Pemain",
                    categoryId = categoryId
                )
                firestore.joinMatchmaking(ticket)

                matchmakingListenerJob = viewModelScope.launch {
                    firestore.listenMatchmaking(categoryId).collect { tickets ->
                        val filtered = tickets.filter { it.userId != userId }
                        _state.value = _state.value.copy(ticketsCount = filtered.size)

                        if (filtered.size >= 1 && ticket.userId < filtered.first().userId) {
                            val opponent = filtered.first()
                            startGame(opponent)
                        } else if (filtered.size >= 1) {
                            val opponent = filtered.first { it.userId != userId }
                            startGame(opponent)
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal mencari lawan", isSearching = false)
            }
        }
    }

    private suspend fun startGame(opponent: MatchmakingTicket) {
        val userId = FirebaseModule.getUserId() ?: return
        val categoryId = _state.value.selectedCategory

        firestore.leaveMatchmaking(userId)
        matchmakingListenerJob?.cancel()

        val gameId = firestore.createGame(
            player1Id = userId,
            player1Name = "Pemain",
            player2Id = opponent.userId,
            player2Name = opponent.displayName,
            categoryId = categoryId
        )

        _state.value = _state.value.copy(
            gameId = gameId,
            screen = BattleScreen.GAME,
            isSearching = false
        )

        listenGame(gameId)
    }

    private fun listenGame(gameId: String) {
        gameListenerJob?.cancel()
        gameListenerJob = viewModelScope.launch {
            firestore.listenGame(gameId).collect { game ->
                _state.value = _state.value.copy(game = game)

                when (game.status) {
                    "finished" -> {
                        timerJob?.cancel()
                        _state.value = _state.value.copy(screen = BattleScreen.RESULT)
                    }
                    "playing" -> startRoundTimer()
                }
            }
        }
    }

    private fun startRoundTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val game = _state.value.game
            var timeLeft = game.roundTimeLimit

            while (timeLeft > 0) {
                _state.value = _state.value.copy(timeLeft = timeLeft)
                delay(1000)
                timeLeft--
            }

            val userId = FirebaseModule.getUserId() ?: return@launch
            if (!_state.value.hasAnswered) {
                submitAnswer(-1)
            }
        }
    }

    fun selectAnswer(index: Int) {
        _state.value = _state.value.copy(selectedAnswer = index)
    }

    fun submitAnswer(selectedAnswer: Int = _state.value.selectedAnswer) {
        val game = _state.value.game
        val userId = FirebaseModule.getUserId() ?: return
        val round = game.currentRound

        if (_state.value.hasAnswered) return

        val currentQuestion = game.questions.getOrNull(round) ?: return
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        _state.value = _state.value.copy(hasAnswered = true, selectedAnswer = selectedAnswer)

        viewModelScope.launch {
            try {
                firestore.submitAnswer(game.gameId, userId, round, selectedAnswer, isCorrect)
                delay(3000)

                val updatedGame = _state.value.game
                val bothReady = updatedGame.players.values.all { it.isReady }
                if (bothReady) {
                    firestore.advanceRound(game.gameId, round, game.totalRounds)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal mengirim jawaban")
            }
        }
    }

    fun playAgain() {
        _state.value = BattleUiState(
            screen = BattleScreen.LOBBY,
            myUserId = _state.value.myUserId
        )
        gameListenerJob?.cancel()
        timerJob?.cancel()
    }

    fun cancelMatchmaking() {
        viewModelScope.launch {
            val userId = FirebaseModule.getUserId() ?: return@launch
            firestore.leaveMatchmaking(userId)
        }
        matchmakingListenerJob?.cancel()
        _state.value = BattleUiState(screen = BattleScreen.LOBBY, myUserId = _state.value.myUserId)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = "")
    }

    override fun onCleared() {
        super.onCleared()
        gameListenerJob?.cancel()
        timerJob?.cancel()
        matchmakingListenerJob?.cancel()
    }
}
