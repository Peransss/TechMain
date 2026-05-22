package com.example.techmain.ui.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import com.example.techmain.firebase.GameRoom
import com.example.techmain.firebase.GameSession
import com.example.techmain.game.BotAnswerEngine
import com.example.techmain.game.BotDifficulty
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class BattleScreen {
    LOBBY, WAITING_ROOM, GAME, RESULT, JOIN_ROOM
}

data class BattleUiState(
    val screen: BattleScreen = BattleScreen.LOBBY,
    val selectedCategory: String = "",
    val roomCode: String = "",
    val joinCode: String = "",
    val gameId: String = "",
    val room: GameRoom = GameRoom(),
    val game: GameSession = GameSession(),
    val myUserId: String = "",
    val selectedAnswer: Int = -1,
    val hasAnswered: Boolean = false,
    val timeLeft: Int = 20,
    val errorMessage: String = "",
    val isBotGame: Boolean = false,
    val difficulty: BotDifficulty = BotDifficulty.MEDIUM,
    val showDifficultyDialog: Boolean = false
)

class BattleViewModel : ViewModel() {
    private val firestore = FirestoreService()
    private val _state = MutableStateFlow(BattleUiState())
    val state: StateFlow<BattleUiState> = _state.asStateFlow()

    private var gameListenerJob: Job? = null
    private var timerJob: Job? = null
    private var roomListenerJob: Job? = null
    private var botAnswerJob: Job? = null

    fun init() {
        val userId = FirebaseModule.getUserId() ?: return
        _state.value = _state.value.copy(myUserId = userId)
    }

    fun selectCategory(categoryId: String) {
        _state.value = _state.value.copy(selectedCategory = categoryId)
    }

    fun showDifficultyDialog() {
        _state.value = _state.value.copy(showDifficultyDialog = true)
    }

    fun hideDifficultyDialog() {
        _state.value = _state.value.copy(showDifficultyDialog = false)
    }

    fun setDifficulty(difficulty: BotDifficulty) {
        _state.value = _state.value.copy(difficulty = difficulty)
    }

    fun startVsBot() {
        val userId = FirebaseModule.getUserId() ?: return
        val categoryId = _state.value.selectedCategory
        if (categoryId.isEmpty()) return
        val difficulty = _state.value.difficulty

        viewModelScope.launch {
            try {
                val roomCode = firestore.createRoom(userId, "Pemain", categoryId)
                firestore.joinRoom(roomCode, "system_bot_ai", "Bot AI")

                _state.value = _state.value.copy(
                    isBotGame = true,
                    roomCode = roomCode,
                    screen = BattleScreen.WAITING_ROOM
                )

                listenRoom(roomCode)
                firestore.startGameFromRoom(roomCode)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal memulai vs bot")
            }
        }
    }

    fun createRoom() {
        val userId = FirebaseModule.getUserId() ?: return
        val categoryId = _state.value.selectedCategory
        if (categoryId.isEmpty()) return

        viewModelScope.launch {
            try {
                val roomCode = firestore.createRoom(userId, "Pemain", categoryId)
                _state.value = _state.value.copy(
                    roomCode = roomCode,
                    screen = BattleScreen.WAITING_ROOM
                )
                listenRoom(roomCode)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal membuat room")
            }
        }
    }

    fun onJoinCodeChange(code: String) {
        _state.value = _state.value.copy(joinCode = code.uppercase())
    }

    fun joinRoom() {
        val userId = FirebaseModule.getUserId() ?: return
        val code = _state.value.joinCode
        if (code.length != 6) return

        viewModelScope.launch {
            try {
                val success = firestore.joinRoom(code, userId, "Pemain")
                if (success) {
                    _state.value = _state.value.copy(
                        roomCode = code,
                        screen = BattleScreen.WAITING_ROOM
                    )
                    listenRoom(code)
                } else {
                    _state.value = _state.value.copy(errorMessage = "Room tidak ditemukan atau sudah dimulai")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal bergabung")
            }
        }
    }

    fun showJoinRoom() {
        _state.value = _state.value.copy(screen = BattleScreen.JOIN_ROOM)
    }

    private fun listenRoom(roomCode: String) {
        roomListenerJob?.cancel()
        roomListenerJob = viewModelScope.launch {
            firestore.listenRoom(roomCode).collect { room ->
                _state.value = _state.value.copy(room = room)

                if (room.status == "playing" && room.gameId.isNotEmpty() && _state.value.gameId != room.gameId) {
                    _state.value = _state.value.copy(gameId = room.gameId)
                    listenGame(room.gameId)
                }
            }
        }
    }

    fun startGame() {
        val roomCode = _state.value.roomCode
        viewModelScope.launch {
            try {
                val gameId = firestore.startGameFromRoom(roomCode)
                if (gameId.isNotEmpty()) {
                    _state.value = _state.value.copy(gameId = gameId)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal memulai game")
            }
        }
    }

    private fun listenGame(gameId: String) {
        gameListenerJob?.cancel()
        gameListenerJob = viewModelScope.launch {
            var lastRound = -1
            firestore.listenGame(gameId).collect { game ->
                _state.value = _state.value.copy(game = game)

                when (game.status) {
                    "finished" -> {
                        timerJob?.cancel()
                        botAnswerJob?.cancel()
                        _state.value = _state.value.copy(screen = BattleScreen.RESULT)
                    }
                    "playing" -> {
                        if (_state.value.screen != BattleScreen.GAME) {
                            _state.value = _state.value.copy(screen = BattleScreen.GAME)
                        }
                        if (game.currentRound != lastRound) {
                            lastRound = game.currentRound
                            _state.value = _state.value.copy(
                                selectedAnswer = -1,
                                hasAnswered = false
                            )
                            startRoundTimer()
                            if (_state.value.isBotGame) {
                                botSubmitAnswer(game)
                            }
                        }
                        if (game.currentRound == lastRound
                            && game.players.size >= 2
                            && game.players.values.all { it.isReady }
                        ) {
                            timerJob?.cancel()
                            botAnswerJob?.cancel()
                            viewModelScope.launch {
                                try {
                                    firestore.advanceRound(game.gameId, game.currentRound, game.totalRounds)
                                } catch (_: Exception) { }
                            }
                        }
                    }
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
            _state.value = _state.value.copy(timeLeft = 0)
            if (!_state.value.hasAnswered) {
                timeoutSubmit(game)
            }
        }
    }

    private fun timeoutSubmit(game: GameSession) {
        val userId = FirebaseModule.getUserId() ?: return
        if (_state.value.hasAnswered) return
        _state.value = _state.value.copy(hasAnswered = true, selectedAnswer = -1)

        viewModelScope.launch {
            try {
                firestore.submitAnswer(game.gameId, userId, -1, false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal mengirim jawaban")
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
                firestore.submitAnswer(game.gameId, userId, selectedAnswer, isCorrect)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal mengirim jawaban")
            }
        }
    }

    private fun botSubmitAnswer(game: GameSession) {
        botAnswerJob?.cancel()
        botAnswerJob = viewModelScope.launch {
            val round = game.currentRound
            val question = game.questions.getOrNull(round) ?: return@launch
            val difficulty = _state.value.difficulty

            delay(Random.nextLong(difficulty.minDelay, difficulty.maxDelay + 1))

            val isCorrect = BotAnswerEngine.shouldBeCorrect(difficulty)
            val answer = BotAnswerEngine.getAnswer(question.correctAnswer, isCorrect, question.options.size)

            firestore.submitAnswer(game.gameId, "system_bot_ai", answer, isCorrect)
        }
    }

    fun playAgain() {
        gameListenerJob?.cancel()
        timerJob?.cancel()
        botAnswerJob?.cancel()
        roomListenerJob?.cancel()
        _state.value = BattleUiState(myUserId = _state.value.myUserId)
    }

    fun leaveRoom() {
        viewModelScope.launch {
            val userId = FirebaseModule.getUserId() ?: return@launch
            firestore.leaveRoom(_state.value.roomCode, userId)
        }
        roomListenerJob?.cancel()
        _state.value = BattleUiState(myUserId = _state.value.myUserId)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = "")
    }

    override fun onCleared() {
        super.onCleared()
        gameListenerJob?.cancel()
        timerJob?.cancel()
        botAnswerJob?.cancel()
        roomListenerJob?.cancel()
    }
}
