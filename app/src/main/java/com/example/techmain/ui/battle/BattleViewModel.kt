package com.example.techmain.ui.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.CustomQuiz
import com.example.techmain.firebase.FirestoreService
import com.example.techmain.firebase.GameRoom
import com.example.techmain.firebase.GameSession
import com.example.techmain.firebase.RoomPlayer
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
    LOBBY, WAITING_ROOM, GAME, RESULT, JOIN_ROOM, SOLO_PRACTICE
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
    val showDifficultyDialog: Boolean = false,
    val selectedMode: String = "casual",
    val showModePicker: Boolean = false,
    val pendingAction: String = "",
    val eliminatedOptions: Set<Int> = emptySet(),
    val doublePointsActive: Boolean = false,
    val soloRounds: Int = 5,
    val showSoloSetup: Boolean = false,
    // Penanda baru untuk mengunci UI selama menampilkan jawaban benar
    val isShowingCorrectAnswer: Boolean = false
)

class BattleViewModel : ViewModel() {
    private val firestore = FirebaseModule.firestoreService
    private val _state = MutableStateFlow(BattleUiState())
    val state: StateFlow<BattleUiState> = _state.asStateFlow()

    private val _featuredQuizzes = MutableStateFlow<List<CustomQuiz>>(emptyList())
    val featuredQuizzes: StateFlow<List<CustomQuiz>> = _featuredQuizzes.asStateFlow()

    private var gameListenerJob: Job? = null
    private var timerJob: Job? = null
    private var roomListenerJob: Job? = null
    private var botAnswerJob: Job? = null
    private var featuredQuizzesJob: Job? = null

    private var advancingRound: Int = -1
    private var botHasAnswered: Boolean = false

    fun init() {
        val userId = FirebaseModule.getUserId() ?: return
        if (featuredQuizzesJob == null) {
            featuredQuizzesJob = viewModelScope.launch {
                firestore.fetchFeaturedQuizzes().collect { _featuredQuizzes.value = it }
            }
        }
        _state.value = _state.value.copy(myUserId = userId)
        advancingRound = -1
        botHasAnswered = false
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
        if (_state.value.selectedCategory.isEmpty()) return
        showModePickerForAction("vsBot")
    }

    private fun executeStartVsBot() {
        val userId = FirebaseModule.getUserId() ?: return
        val categoryId = _state.value.selectedCategory
        if (categoryId.isEmpty()) return

        viewModelScope.launch {
            try {
                val roomCode = firestore.createRoom(userId, "Pemain", categoryId)

                _state.value = _state.value.copy(
                    isBotGame = true,
                    roomCode = roomCode,
                    screen = BattleScreen.WAITING_ROOM
                )

                listenRoom(roomCode)
                firestore.startGameFromRoom(
                    roomCode,
                    _state.value.selectedMode,
                    extraPlayers = mapOf("system_bot_ai" to RoomPlayer("system_bot_ai", "Bot AI"))
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal memulai vs bot")
            }
        }
    }

    fun createRoom() {
        if (_state.value.selectedCategory.isEmpty()) return
        showModePickerForAction("createRoom")
    }

    private fun executeCreateRoom() {
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

    fun showModePickerForAction(action: String) {
        _state.value = _state.value.copy(showModePicker = true, pendingAction = action)
    }

    fun hideModePicker() {
        _state.value = _state.value.copy(showModePicker = false, pendingAction = "")
    }

    fun setMode(mode: String) {
        val action = _state.value.pendingAction
        _state.value = _state.value.copy(selectedMode = mode, showModePicker = false)
        when (action) {
            "createRoom" -> executeCreateRoom()
            "vsBot" -> executeStartVsBot()
        }
    }

    fun showSoloSetup() {
        _state.value = _state.value.copy(showSoloSetup = true)
    }

    fun hideSoloSetup() {
        _state.value = _state.value.copy(showSoloSetup = false)
    }

    fun setSoloRounds(rounds: Int) {
        _state.value = _state.value.copy(soloRounds = rounds)
    }

    fun startSoloPractice() {
        _state.value = _state.value.copy(showSoloSetup = false, screen = BattleScreen.SOLO_PRACTICE)
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
                firestore.startGameFromRoom(roomCode, _state.value.selectedMode)
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
                // PERBAIKAN DINAMIS: Menentukan target jumlah soal berdasarkan mode game
                val adjustedGame = if (_state.value.isBotGame) {
                    // Jika mode mengandung kata "marathon", set ke 10 soal. Jika tidak (casual), set ke 5 soal.
                    val targetCount = if (game.mode.contains("marathon", ignoreCase = true) ||
                        _state.value.selectedMode.contains("marathon", ignoreCase = true)) 10 else 5

                    val limitedQuestions = game.questions.take(targetCount)
                    game.copy(
                        totalRounds = targetCount,
                        questions = limitedQuestions
                    )
                } else {
                    game
                }

                when (adjustedGame.status) {
                    "finished" -> {
                        timerJob?.cancel()
                        botAnswerJob?.cancel()
                        _state.value = _state.value.copy(game = adjustedGame, screen = BattleScreen.RESULT)
                    }
                    "playing" -> {
                        if (_state.value.screen != BattleScreen.GAME) {
                            _state.value = _state.value.copy(screen = BattleScreen.GAME)
                        }

                        if (adjustedGame.currentRound != lastRound) {
                            lastRound = adjustedGame.currentRound
                            botHasAnswered = false
                            _state.value = _state.value.copy(
                                game = adjustedGame,
                                selectedAnswer = -1,
                                hasAnswered = false,
                                eliminatedOptions = emptySet(),
                                doublePointsActive = false,
                                isShowingCorrectAnswer = false
                            )
                            startRoundTimer()
                            if (_state.value.isBotGame) {
                                botSubmitAnswer(adjustedGame)
                            }
                        } else {
                            _state.value = _state.value.copy(game = adjustedGame)
                        }

                        if (adjustedGame.currentRound == lastRound) {
                            checkAndAdvanceRound(adjustedGame)
                        }
                    }
                }
            }
        }
    }

    private fun checkAndAdvanceRound(game: GameSession) {
        if (advancingRound == game.currentRound) return

        val myId = _state.value.myUserId
        val me = game.players[myId]

        // 1. Pastikan status kamu benar-benar sudah menjawab di UI atau di Server
        val humanReady = _state.value.hasAnswered || me?.isReady == true

        val botReady = if (_state.value.isBotGame) {
            botHasAnswered || game.players["system_bot_ai"]?.isReady == true
        } else {
            game.players["system_bot_ai"]?.isReady == true
        }

        // 2. KUNCI UTAMA: Jika bermain VS Bot, soal HANYA BOLEH berganti jika KAMU sudah menjawab DAN BOT sudah menjawab.
        // Bot tidak bisa lagi memaksa mengganti soal sendirian jika kamu belum menjawab.
        val allReady = if (_state.value.isBotGame) {
            humanReady && botReady
        } else {
            game.players.size >= 2 && game.players.values.all { it.isReady }
        }

        // 3. Tambahkan pengecekan ketat agar server tidak melompati ronde secara sepihak
        if (allReady) {
            advancingRound = game.currentRound
            timerJob?.cancel()
            botAnswerJob?.cancel()

            viewModelScope.launch {
                val maxRounds = if (_state.value.isBotGame) game.totalRounds else game.totalRounds
                try {
                    firestore.advanceRound(game.gameId, game.currentRound, maxRounds)
                } catch (_: Exception) {
                    if (advancingRound == game.currentRound) advancingRound = -1
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
                // Jangan kurangi timer visual jika sedang membeku memperlihatkan jawaban benar
                if (!_state.value.isShowingCorrectAnswer) {
                    _state.value = _state.value.copy(timeLeft = timeLeft)
                    delay(1000)
                    timeLeft--
                } else {
                    delay(200)
                }
            }
            _state.value = _state.value.copy(timeLeft = 0)

            if (!_state.value.hasAnswered) {
                timeoutSubmit(game)
            }

            if (_state.value.isBotGame) {
                botAnswerJob?.cancel()
                botHasAnswered = true
                checkAndAdvanceRound(_state.value.game)
            }
        }
    }

    private fun timeoutSubmit(game: GameSession) {
        val userId = FirebaseModule.getUserId() ?: return
        if (_state.value.hasAnswered) return
        _state.value = _state.value.copy(hasAnswered = true, selectedAnswer = -1)

        viewModelScope.launch {
            try {
                firestore.submitAnswer(game.gameId, userId, -1, false, game.mode)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal mengirim jawaban")
            }
        }
    }

    fun selectAnswer(index: Int) {
        if (_state.value.isShowingCorrectAnswer) return // Kunci tombol input saat jeda 2 detik
        _state.value = _state.value.copy(selectedAnswer = index)
    }

    fun submitAnswer(selectedAnswer: Int = _state.value.selectedAnswer) {
        if (_state.value.isShowingCorrectAnswer) return // Cegah double-submit saat jeda ronde
        val game = _state.value.game
        val userId = FirebaseModule.getUserId() ?: return
        val round = game.currentRound
        if (_state.value.hasAnswered) return
        val currentQuestion = game.questions.getOrNull(round) ?: return
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        _state.value = _state.value.copy(hasAnswered = true, selectedAnswer = selectedAnswer)

        viewModelScope.launch {
            try {
                firestore.submitAnswer(game.gameId, userId, selectedAnswer, isCorrect, game.mode)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal mengirim jawaban")
            }
            if (_state.value.isBotGame) {
                checkAndAdvanceRound(_state.value.game)
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

            try {
                firestore.submitAnswer(game.gameId, "system_bot_ai", answer, isCorrect, game.mode)
            } catch (_: Exception) { }

            botHasAnswered = true
            checkAndAdvanceRound(_state.value.game)
        }
    }

    fun usePowerUp(type: String) {
        if (_state.value.isShowingCorrectAnswer) return
        val game = _state.value.game
        val userId = _state.value.myUserId
        viewModelScope.launch {
            try {
                when (type) {
                    "fiftyFifty" -> {
                        firestore.usePowerUp(game.gameId, userId, type)
                        val q = game.questions.getOrNull(game.currentRound) ?: return@launch
                        val wrongIndices = q.options.indices.filter { it != q.correctAnswer }.shuffled().take(2)
                        _state.value = _state.value.copy(eliminatedOptions = wrongIndices.toSet())
                    }
                    "doublePoints" -> {
                        firestore.usePowerUp(game.gameId, userId, type)
                        _state.value = _state.value.copy(doublePointsActive = true)
                    }
                    "timeFreeze" -> {
                        firestore.usePowerUp(game.gameId, userId, type)
                        firestore.freezeOpponent(game.gameId, userId, 5000L)
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun playAgain() {
        gameListenerJob?.cancel()
        timerJob?.cancel()
        botAnswerJob?.cancel()
        roomListenerJob?.cancel()
        advancingRound = -1
        botHasAnswered = false
        _state.value = BattleUiState(
            myUserId = _state.value.myUserId,
            selectedMode = _state.value.selectedMode
        )
    }

    fun leaveRoom() {
        val userId = FirebaseModule.getUserId() ?: return
        viewModelScope.launch {
            firestore.leaveRoom(_state.value.roomCode, userId)
        }
        roomListenerJob?.cancel()
        advancingRound = -1
        botHasAnswered = false
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