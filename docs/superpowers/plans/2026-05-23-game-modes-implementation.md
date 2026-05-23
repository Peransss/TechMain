# Game Modes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Solo Practice mode and Competitive mode variants (Blitz, Marathon, Power-Up) with daily challenges, category mastery, and EXP/coins.

**Architecture:** Solo Practice is a local-only state machine (`SoloPracticeState.kt`) that uses `QuestionBank` directly. Competitive modes extend the existing Firestore-based game system — `GameSession` gains a `mode` field, scoring/timing varies by mode, and Power-Up mode adds player power-up actions.

**Tech Stack:** Kotlin, Jetpack Compose, Firebase Firestore

---

### Task 1: Add mode field to GameSession and power-ups to GamePlayer

**Files:**
- Modify: `app/src/main/java/com/example/techmain/firebase/QuizModels.kt`

- [ ] **Step 1: Add mode and powerUps fields**

```kotlin
data class GamePlayer(
    val userId: String = "",
    val displayName: String = "Pemain",
    val score: Int = 0,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0,
    val isReady: Boolean = false,
    val powerUps: Map<String, Boolean> = mapOf(
        "fiftyFifty" to true,
        "doublePoints" to true,
        "timeFreeze" to true
    ),
    val activeDoubleMultiplier: Float = 1f,
    val timeFrozenUntil: Long = 0L
)

data class GameSession(
    val gameId: String = "",
    val players: Map<String, GamePlayer> = emptyMap(),
    val categoryId: String = "",
    val status: String = "waiting",
    val currentRound: Int = 0,
    val totalRounds: Int = 5,
    val questions: List<QuizQuestion> = emptyList(),
    val roundStartTime: Long = 0L,
    val roundTimeLimit: Int = 20,
    val winnerId: String = "",
    val createdAt: Long = 0L,
    val mode: String = "casual"
)
```

- [ ] **Step 2: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/firebase/QuizModels.kt
git commit -m "feat: add mode and powerUp fields to game models"
```

---

### Task 2: Create SoloPracticeState.kt (local soloplay engine)

**Files:**
- Create: `app/src/main/java/com/example/techmain/game/SoloPracticeState.kt`

- [ ] **Step 1: Create the SoloPracticeState class**

```kotlin
package com.example.techmain.game

import com.example.techmain.firebase.QuestionBank
import com.example.techmain.firebase.QuizQuestion
import kotlin.random.Random

data class SoloPracticeConfig(
    val categoryId: String,
    val totalRounds: Int = 5,
    val roundTimeLimit: Int = 10
)

data class SoloPracticeState(
    val config: SoloPracticeConfig = SoloPracticeConfig("", 5),
    val questions: List<QuizQuestion> = emptyList(),
    val currentRound: Int = 0,
    val score: Int = 0,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0,
    val streak: Int = 0,
    val maxStreak: Int = 0,
    val expEarned: Int = 0,
    val coinsEarned: Int = 0,
    val selectedAnswer: Int = -1,
    val hasAnswered: Boolean = false,
    val timeLeft: Int = 10,
    val isFinished: Boolean = false,
    val status: String = "ready" // ready | playing | finished
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentRound)
    val accuracy: Int get() = if (totalAnswered > 0) (correctCount * 100 / totalAnswered) else 0
    val totalRounds: Int get() = questions.size

    fun start(): SoloPracticeState {
        val qs = QuestionBank.getQuestions(config.categoryId)
            .shuffled()
            .take(config.totalRounds)
        return copy(
            questions = qs,
            status = "playing",
            timeLeft = config.roundTimeLimit
        )
    }

    fun submitAnswer(answerIndex: Int): SoloPracticeState {
        val q = currentQuestion ?: return this
        val isCorrect = answerIndex == q.correctAnswer
        val newStreak = if (isCorrect) streak + 1 else 0
        val streakBonus = if (isCorrect && newStreak > 0 && newStreak % 5 == 0) 50 else 0
        val points = if (isCorrect) 100 + streakBonus else 0
        val exp = if (isCorrect) Random.nextInt(10, 31) else 0
        val coins = if (isCorrect) Random.nextInt(1, 6) else 0

        val nextRound = currentRound + 1
        val isFinished = nextRound >= totalRounds

        return copy(
            selectedAnswer = answerIndex,
            hasAnswered = true,
            score = score + points,
            correctCount = correctCount + (if (isCorrect) 1 else 0),
            totalAnswered = totalAnswered + 1,
            streak = newStreak,
            maxStreak = maxOf(maxStreak, newStreak),
            expEarned = expEarned + exp,
            coinsEarned = coinsEarned + coins,
            currentRound = nextRound,
            timeLeft = if (isFinished) 0 else config.roundTimeLimit,
            isFinished = isFinished,
            status = if (isFinished) "finished" else "playing",
            selectedAnswer = -1,
            hasAnswered = false
        )
    }

    fun timeout(): SoloPracticeState {
        val nextRound = currentRound + 1
        val isFinished = nextRound >= totalRounds
        return copy(
            selectedAnswer = -1,
            hasAnswered = true,
            totalAnswered = totalAnswered + 1,
            streak = 0,
            currentRound = nextRound,
            timeLeft = if (isFinished) 0 else config.roundTimeLimit,
            isFinished = isFinished,
            status = if (isFinished) "finished" else "playing",
            selectedAnswer = -1,
            hasAnswered = false
        )
    }

    fun selectAnswer(index: Int): SoloPracticeState {
        return copy(selectedAnswer = index)
    }

    fun tick(): SoloPracticeState {
        if (hasAnswered || isFinished) return this
        val newTime = timeLeft - 1
        return if (newTime <= 0) timeout() else copy(timeLeft = newTime)
    }
}
```

- [ ] **Step 2: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/game/SoloPracticeState.kt
git commit -m "feat: add SoloPractice state machine"
```

---

### Task 3: Add mode awareness to FirestoreService

**Files:**
- Modify: `app/src/main/java/com/example/techmain/firebase/FirestoreService.kt`

- [ ] **Step 1: Update scoring to be mode-aware in submitAnswer**

Replace the scoring in `submitAnswer`:

Current:
```kotlin
"players.$playerId.score" to FieldValue.increment(if (isCorrect) 100L else 0L),
```

New: accept a mode parameter and calculate points:

```kotlin
suspend fun submitAnswer(gameId: String, playerId: String, selectedAnswer: Int, isCorrect: Boolean, mode: String = "casual") {
    val gameRef = gamesCollection.document(gameId)
    val points = when (mode) {
        "blitz" -> 150L
        "marathon" -> 50L
        else -> 100L
    }
    gameRef.update(
        mapOf(
            "players.$playerId.totalAnswered" to FieldValue.increment(1L),
            "players.$playerId.correctCount" to FieldValue.increment(if (isCorrect) 1L else 0L),
            "players.$playerId.score" to FieldValue.increment(if (isCorrect) points else 0L),
            "players.$playerId.isReady" to true
        )
    ).await()
}
```

- [ ] **Step 2: Update startGameFromRoom to accept mode parameter**

Add `mode: String = "casual"` parameter. When building the game document:
- `totalRounds` depends on mode (Marathon=10, others=5)
- `roundTimeLimit` depends on mode (Blitz=10, Marathon=15, others=20)
- Add `"mode" to mode` to the game map

```kotlin
suspend fun startGameFromRoom(roomCode: String, mode: String = "casual"): String {
    // ... existing code ...
    val totalRounds = if (mode == "marathon") 10 else questions.size
    val roundTimeLimit = when (mode) {
        "blitz" -> 10
        "marathon" -> 15
        else -> 20
    }

    // Then use totalRounds as take count when fetching questions:
    val finalQuestions = questions.shuffled().take(totalRounds)

    // And in game map:
    "totalRounds" to finalQuestions.size,
    "roundTimeLimit" to roundTimeLimit,
    "mode" to mode,
```

- [ ] **Step 3: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/example/techmain/firebase/FirestoreService.kt
git commit -m "feat: add mode-aware scoring and game creation"
```

---

### Task 4: Add mode picker and LATIHAN button to lobby

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt`
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt`

- [ ] **Step 1: Add mode to BattleUiState**

```kotlin
data class BattleUiState(
    // ...existing fields...
    val selectedMode: String = "casual",
    val showModePicker: Boolean = false
)
```

- [ ] **Step 2: Add mode-related methods to ViewModel**

```kotlin
fun createRoom() {
    val userId = FirebaseModule.getUserId() ?: return
    val categoryId = _state.value.selectedCategory
    val mode = _state.value.selectedMode
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

fun startVsBot() {
    // ...existing...
    firestore.startGameFromRoom(roomCode, _state.value.selectedMode)
    // Note: pass mode to startGameFromRoom
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

fun showModePicker() {
    _state.value = _state.value.copy(showModePicker = true)
}

fun hideModePicker() {
    _state.value = _state.value.copy(showModePicker = false)
}

fun setMode(mode: String) {
    _state.value = _state.value.copy(selectedMode = mode, showModePicker = false)
}
```

- [ ] **Step 3: Add LATIHAN button + mode picker UI to lobby**

In `LobbyContent`, add a new "LATIHAN" style button (after the "VS BOT" button):

```kotlin
// After VS BOT button
Spacer(modifier = Modifier.height(8.dp))
OutlinedButton(
    onClick = { /* navigate to solo practice */ },
    modifier = Modifier.fillMaxWidth().height(52.dp),
    enabled = state.selectedCategory.isNotEmpty(),
    shape = RoundedCornerShape(12.dp)
) {
    Text("🎯 LATIHAN (Solo)", fontWeight = FontWeight.Bold)
}
```

Add mode picker dialog:

```kotlin
if (state.showModePicker) {
    AlertDialog(
        onDismissRequest = { viewModel.hideModePicker() },
        title = { Text("Pilih Mode", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                ModeOption("Casual", "Standar 5 soal · 20 detik", "casual", state.selectedMode, viewModel)
                ModeOption("Blitz", "Cepat 5 soal · 10 detik · 150 pts", "blitz", state.selectedMode, viewModel)
                ModeOption("Marathon", "10 soal · 15 detik · 50 pts", "marathon", state.selectedMode, viewModel)
                ModeOption("Power-Up", "5 soal + 3 power-ups (50:50, 2x, Freeze)", "powerup", state.selectedMode, viewModel)
            }
        },
        confirmButton = { TextButton(onClick = { viewModel.hideModePicker() }) { Text("BATAL") } }
    )
}

@Composable
fun ModeOption(title: String, desc: String, modeId: String, currentMode: String, viewModel: BattleViewModel) {
    val isSelected = currentMode == modeId
    TextButton(
        onClick = { viewModel.setMode(modeId) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(desc, style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

- [ ] **Step 4: Wire mode picker to show before createRoom/startVsBot**

In the ViewModel, modify `createRoom()` and `startVsBot()` to show the mode picker first. The user selects a mode, then the action proceeds.

Actually, a simpler approach: the mode picker dialog triggers on "BUAT ROOM" or "VS BOT" clicks. When user selects a mode, the action proceeds immediately.

Modify the LobbyContent to call `viewModel.showModePicker()` instead of direct `viewModel.createRoom()` for the create room button. Store the intended action and trigger it after mode selection.

Better approach: in BattleViewModel, have `showModePickerForAction(action: String)` which stores the pending action, then when `setMode` is called, it calls either `executeCreateRoom()` or `executeStartVsBot()`.

```kotlin
data class BattleUiState(
    // ...existing fields...
    val pendingAction: String = "" // "createRoom" or "vsBot"
)
```

- [ ] **Step 5: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt
git commit -m "feat: add mode picker and LATIHAN button to lobby"
```

---

### Task 5: Solo Practice screen and integration

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt` (add routing)
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleMainScreen.kt` (add route)
- New: `app/src/main/java/com/example/techmain/ui/battle/SoloPracticeScreen.kt`

- [ ] **Step 1: Create SoloPracticeScreen.kt**

This composable handles:
- Setup screen (choose 5 or 10 rounds, category already selected from lobby)
- Game screen (timer, question, answer buttons)
- Result screen (score, streak, EXP, coins)

The screen manages its own state via `remember { mutableStateOf(SoloPracticeConfig(...)) }` and local state transitions — no ViewModel needed.

```kotlin
@Composable
fun SoloPracticeScreen(config: SoloPracticeConfig, onBack: () -> Unit) {
    var state by remember { mutableStateOf(SoloPracticeState(config = config).start()) }
    var timerJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.status, state.currentRound) {
        if (state.status == "playing" && !state.hasAnswered) {
            timerJob?.cancel()
            timerJob = scope.launch {
                while (state.timeLeft > 0 && state.status == "playing") {
                    delay(1000)
                    state = state.tick()
                }
            }
        }
    }

    when {
        state.status == "playing" -> SoloGameContent(state = state, onSelect = { state = state.selectAnswer(it) }, onSubmit = { state = state.submitAnswer(state.selectedAnswer) })
        state.status == "finished" -> SoloResultContent(state = state, onBack = onBack, onPlayAgain = { state = SoloPracticeState(config = config).start() })
    }
}

@Composable
fun SoloGameContent(state: SoloPracticeState, onSelect: (Int) -> Unit, onSubmit: () -> Unit) {
    val q = state.currentQuestion ?: return
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Round indicator
        Text("Soal ${state.currentRound + 1} / ${state.totalRounds}", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        // Timer
        val timerProgress = state.timeLeft.toFloat() / state.config.roundTimeLimit.toFloat()
        LinearProgressIndicator(progress = { timerProgress }, modifier = Modifier.fillMaxWidth().height(8.dp))
        Text("${state.timeLeft}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        // Streak indicator
        if (state.streak > 0) {
            Text("🔥 Streak: ${state.streak}", color = NeonSlateGold)
            Spacer(Modifier.height(4.dp))
        }
        // Score & EXP
        Text("Skor: ${state.score}  EXP: ${state.expEarned}  🪙${state.coinsEarned}")
        Spacer(Modifier.height(16.dp))
        // Question
        Text(q.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        // Answer options
        q.options.forEachIndexed { index, option ->
            val isSelected = state.selectedAnswer == index
            Button(
                onClick = { onSelect(index) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                enabled = !state.hasAnswered
            ) {
                Text("${('A' + index)}. $option", modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(16.dp))
        if (!state.hasAnswered) {
            Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), enabled = state.selectedAnswer >= 0) {
                Text("KONFIRMASI", fontWeight = FontWeight.Bold)
            }
        } else {
            Text("Menunggu soal berikutnya...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SoloResultContent(state: SoloPracticeState, onBack: () -> Unit, onPlayAgain: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Latihan Selesai!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Text("Skor: ${state.score}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Benar: ${state.correctCount}/${state.totalAnswered}")
        Text("Akurasi: ${state.accuracy}%")
        Text("Streak Terbaik: ${state.maxStreak}")
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hadiah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("EXP: +${state.expEarned}", fontWeight = FontWeight.Bold)
                Text("Koin: +${state.coinsEarned}", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth()) { Text("LATIHAN LAGI", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("KEMBALI") }
    }
}
```

- [ ] **Step 2: Add solo practice routing to BattleMainScreen**

In `BattleMainScreen.kt`, add a new route/screen for solo practice. The simple approach: add `SOLO_SETUP` and `SOLO_GAME` to the `BattleScreen` enum and route accordingly.

But since solo practice is a separate flow from the room/game system, a cleaner approach is to add `SOLO_PRACTICE` to `BattleScreen` enum and render `SoloPracticeScreen` when that screen is active. The config (category + rounds) is set before entering.

- [ ] **Step 3: Wire LATIHAN button to enter Solo Practice**

In `BattleLobbyScreen`, the "LATIHAN" button opens a simple dialog to pick 5 or 10 rounds, then sets state to SOLO_PRACTICE screen with the config.

- [ ] **Step 4: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleMainScreen.kt app/src/main/java/com/example/techmain/ui/battle/SoloPracticeScreen.kt app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt
git commit -m "feat: add solo practice screen and routing"
```

---

### Task 6: Add Power-Up UI to BattleGameScreen

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt`

- [ ] **Step 1: Add power-up buttons in Power-Up mode**

When `game.mode == "powerup"`, show 3 power-up buttons below the answer options:

```kotlin
if (game.mode == "powerup" && !state.hasAnswered) {
    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        PowerUpButton("50:50", onClick = { viewModel.usePowerUp("fiftyFifty") }, isAvailable = me?.powerUps?.get("fiftyFifty") == true)
        PowerUpButton("2x Poin", onClick = { viewModel.usePowerUp("doublePoints") }, isAvailable = me?.powerUps?.get("doublePoints") == true)
        PowerUpButton("⏸ Freeze", onClick = { viewModel.usePowerUp("timeFreeze") }, isAvailable = me?.powerUps?.get("timeFreeze") == true)
    }
}

@Composable
fun PowerUpButton(label: String, onClick: () -> Unit, isAvailable: Boolean) {
    Button(onClick = onClick, enabled = isAvailable, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}
```

- [ ] **Step 2: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt
git commit -m "feat: add power-up buttons to game screen"
```

---

### Task 7: Add power-up actions to BattleViewModel

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt`
- Modify: `app/src/main/java/com/example/techmain/firebase/FirestoreService.kt`

- [ ] **Step 1: Add power-up methods to ViewModel**

```kotlin
fun usePowerUp(type: String) {
    val game = _state.value.game
    val userId = _state.value.myUserId
    viewModelScope.launch {
        try {
            when (type) {
                "fiftyFifty" -> {
                    firestore.usePowerUp(game.gameId, userId, type)
                    // Remove 2 wrong answers locally
                    val q = game.questions.getOrNull(game.currentRound) ?: return@launch
                    val wrongIndices = q.options.indices.filter { it != q.correctAnswer }.shuffled().take(2)
                    _state.value = _state.value.copy(eliminatedOptions = wrongIndices.toSet())
                }
                "doublePoints" -> {
                    firestore.usePowerUp(game.gameId, userId, type)
                    // Set multiplier on next answer
                    _state.value = _state.value.copy(doublePointsActive = true)
                }
                "timeFreeze" -> {
                    firestore.usePowerUp(game.gameId, userId, type)
                    // Pause opponent timer for 5s (stored in Firestore)
                    firestore.freezeOpponent(game.gameId, userId, 5000L)
                }
            }
        } catch (_: Exception) { }
    }
}
```

Add to `BattleUiState`:
```kotlin
val eliminatedOptions: Set<Int> = emptySet(),
val doublePointsActive: Boolean = false
```

- [ ] **Step 2: Add power-up Firestore methods**

In `FirestoreService.kt`:
```kotlin
suspend fun usePowerUp(gameId: String, playerId: String, powerUpType: String) {
    val gameRef = gamesCollection.document(gameId)
    gameRef.update("players.$playerId.powerUps.$powerUpType", false).await()
}

suspend fun freezeOpponent(gameId: String, playerId: String, durationMs: Long) {
    val gameRef = gamesCollection.document(gameId)
    gameRef.update("players.$playerId.timeFrozenUntil", System.currentTimeMillis() + durationMs).await()
}
```

- [ ] **Step 3: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt app/src/main/java/com/example/techmain/firebase/FirestoreService.kt
git commit -m "feat: add power-up actions and Firestore methods"
```

---

### Task 8: Add category mastery to ProfileScreen

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/profile/ProfileScreen.kt`
- Modify: `app/src/main/java/com/example/techmain/ui/profile/ProfileViewModel.kt`
- Modify: `app/src/main/java/com/example/techmain/firebase/FirestoreService.kt`

- [ ] **Step 1: Add category mastery to FirestoreService**

```kotlin
suspend fun updateBestScore(userId: String, categoryId: String, score: Int) {
    val userRef = usersCollection.document(userId)
    val field = "bestScores.$categoryId"
    // Only update if new score is higher
    db.runTransaction { transaction ->
        val snapshot = transaction.get(userRef)
        val current = (snapshot.getLong("bestScores.$categoryId") ?: 0L).toInt()
        if (score > current) {
            transaction.update(userRef, field, score)
        }
    }.await()
}
```

- [ ] **Step 2: Wire solo practice result to save mastery**

After solo practice finishes, call `firestore.updateBestScore(userId, config.categoryId, state.score)`.

- [ ] **Step 3: Load and display mastery on profile**

In `ProfileViewModel`, load `bestScores` from user stats. In `ProfileScreen`, show a "Category Mastery" section with each category and best score with tier badges:

```kotlin
// In ProfileState
val bestScores: Map<String, Int> = emptyMap()

// In ProfileViewModel loadProfile
bestScores = (stats["bestScores"] as? Map<String, Any>)?.mapValues { (_, v) -> (v as? Long)?.toInt() ?: 0 } ?: emptyMap()

// In ProfileScreen
if (state.bestScores.isNotEmpty()) {
    Text("Category Mastery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    state.bestScores.forEach { (categoryId, score) ->
        val tier = when {
            score >= 2000 -> "🥇 Gold"
            score >= 1000 -> "🥈 Silver"
            score >= 500 -> "🥉 Bronze"
            else -> "⚪"
        }
        Text("${categoryId}: $score pts ($tier)")
    }
}
```

- [ ] **Step 4: Verify build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/techmain/firebase/FirestoreService.kt app/src/main/java/com/example/techmain/ui/profile/ProfileScreen.kt app/src/main/java/com/example/techmain/ui/profile/ProfileViewModel.kt app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt
git commit -m "feat: add category mastery to profile"
```

---

### Task 9: Final build verification

**Files:**
- Verify: full project

- [ ] **Step 1: Run full build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Clean up old references**

Run: `rg "RPG|NeonHacker|Purple" app/src/main/java/com/example/techmain/`
Expected: No matches (or only expected ones)
