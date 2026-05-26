# TechMain — Educational Battle Quiz Game

## Build & Run

```bash
./gradlew assembleDebug              # build debug APK
./gradlew installDebug               # install on connected device
./gradlew test                        # unit tests (JUnit 4)
```

**Prerequisite:** `app/google-services.json` from a Firebase project with Anonymous Auth + Firestore enabled. This file is committed; replace with your own Firebase config if forking.

## Toolchain

| Tool | Version |
|---|---|
| Gradle | 9.4.1 |
| AGP | 9.2.1 |
| Kotlin | 2.1.20 |
| Compose BOM | 2025.05.00 (K2 compiler via `org.jetbrains.kotlin.plugin.compose`) |
| compileSdk / minSdk / targetSdk | 36 / 30 / 36 |

`gradle.properties` requires `android.disallowKotlinSourceSets=false` for KSP + compose-compiler plugin to work.

## Architecture

| Layer | Technology | Notes |
|---|---|---|
| UI | Jetpack Compose + Material3 | All screens are `@Composable` |
| State | ViewModel + StateFlow / Flow | `collectAsState()` in composables; `SoloPracticeState` is a local state machine (no Firestore) |
| Nav | Navigation Compose 2.9.0 | 4-tab bottom nav |
| Local DB | Room 2.7.1 (KSP) | 1 entity (Avatar), schema v2, destructive migration enabled |
| Image loading | Coil 3.0.4 | `coil-compose` + `coil-network-okhttp` |
| Backend | Firebase Auth + Firestore + Storage | Anonymous sign-in; real-time rooms, games, leaderboard; quiz image uploads |
| DI | None (manual) | ViewModels access `TechMainApp.database` or Firebase directly |

## Entry Points

- `TechMainApp` — Application class; creates `AppDatabase` (Avatar only)
- `MainActivity` — Single activity; calls `FirebaseModule.signInAnonymously()` on startup (blocks UI with spinner until done)

## Navigation

| Route | Screen | Bottom Nav |
|---|---|---|
| `battle` | BattleMainScreen (create/join room → quiz, solo practice) | ✓ |
| `studio` | StudioScreen (browse/manage custom quizzes) | ✓ |
| `wizard` | CreatorWizardScreen (create custom quiz) | — |
| `leaderboard` | LeaderboardScreen (global rankings) | ✓ |
| `profile` | ProfileScreen (stats, avatar, name, category mastery) | ✓ |

Bottom nav uses `popUpTo(findStartDestination)`, `launchSingleTop`, `restoreState`.

**BattleMainScreen has internal screen routing** (`BattleUiState.screen`: LOBBY/JOIN_ROOM/WAITING_ROOM/GAME/RESULT/SOLO_PRACTICE) managed by `BattleViewModel`, not `NavHost`.

## Game Modes

| Mode | Timer | Rounds | Score per correct | Notes |
|---|---|---|---|---|---|
| casual | 20s | 5 | 100 | Default |
| blitz | 10s | 5 | 150 | Fast-paced |
| marathon | 15s | 10 | 50 | Endurance |
| powerup | 20s | 5 | 100 | 3 power-ups (50:50, 2x, Freeze) |

Set via `selectedMode` in `BattleUiState`, passed to `FirestoreService.startGameFromRoom()` and `submitAnswer()`.

## Studio / Custom Quizzes

Users can create custom quizzes via `CreatorWizardScreen` (multi-step wizard). Quizzes stored in Firestore `custom_quizzes/{quizId}` with fields: `creatorId`, `title`, `categoryId`, `questions` (list of `CustomQuestion` with text, options, correctAnswer, optional imageUrl). Images uploaded to Firebase Storage at `media/quizzes/{userId}/{quizId}/{questionId}.jpg` via `StorageService`.

## Room System (like ZEP Quiz)

- Users create rooms (6-char code) or join via code
- Host selects category and mode; room stored in Firestore `rooms/{roomCode}`
- When host clicks "Mulai Game", questions are pulled from `QuestionBank` and a `games/{gameId}` document is created
- All players get the same questions simultaneously
- **First correct answer wins the round** — `submitAnswer` uses a Firestore transaction that atomically checks `roundClaimedBy` before awarding points
- Game advances when `roundClaimedBy.isNotEmpty()` OR all players have answered
- 20-second timer per round (varies by mode); answers submitted to Firestore in real-time

## First-Correct-Wins Mechanic

- `GameSession.roundClaimedBy: String` — set to the playerId of the first player to answer correctly in the current round
- `submitAnswer` uses `db.runTransaction` — reads `roundClaimedBy`, if empty and answer is correct, claims it and awards points
- `advanceRound` resets `roundClaimedBy = ""` alongside `isReady = false` for the next round
- `listenGame` advance trigger: `game.roundClaimedBy.isNotEmpty() || game.players.values.all { it.isReady }`

## Power-Ups

Per-player `powerUps` map in `GamePlayer` (all default `true`):
- `fiftyFifty` — eliminates 2 wrong options for that round
- `doublePoints` — doubles score for that round
- `timeFreeze` — freezes opponent's timer for 5s

Used via `usePowerUp()` in `BattleViewModel`, stored as `players.$uid.powerUps.$type = false` in Firestore.

## VS Bot Mode

- Bot player (`system_bot_ai`) is passed as `extraPlayers` to `startGameFromRoom()` — no `joinRoom` race
- Bot uses `BotAnswerEngine` with difficulty levels:

| Difficulty | Correct % | Delay range |
|---|---|---|
| EASY | 25% | 8–15s |
| MEDIUM | 50% | 5–12s |
| HARD | 80% | 2–7s |

- `botSubmitAnswer()` called from `listenGame` on each new round detection; `botAnswerJob` is cancelled by the advance trigger
- **Bot's `submitAnswer` must be wrapped in try-catch** — if the transaction throws, the game hangs forever waiting for bot's `isReady`

## Solo Practice

- `SoloPracticeState` — pure local state machine (no Firestore), managed by `SoloPracticeScreen`
- Tracks: score, streak, maxStreak, expEarned, coinsEarned, accuracy
- Streak bonus: +50 every 5 consecutive correct answers
- Questions drawn from `QuestionBank` (same pool as multiplayer)
- **Two-phase answer flow:** `submitAnswer(index)` sets `hasAnswered=true` + `showingFeedback=true` (feedback phase with green/red buttons). `advanceAfterFeedback()` applies scoring and advances to next round. `LaunchedEffect(state.showingFeedback)` in `SoloPracticeScreen` waits 1.5s before calling `advanceAfterFeedback()`.
- **Timer respects feedback phase:** `tick()` returns early when `hasAnswered` is true, pausing the countdown during feedback.

## Tests

| Suite | Framework | Location | Notes |
|---|---|---|---|
| Unit | JUnit 4 | `src/test/java/` | `SoloPracticeStateTest` (meaningful); `ExampleUnitTest` (autogenerated) |
| Instrumented | AndroidX JUnit + Espresso | `src/androidTest/java/` | `ExampleInstrumentedTest` (autogenerated) |

Run with `./gradlew test`.

## Firebase Data Model

- `users/{uid}` — displayName, rating, wins, losses, totalGames, correctAnswers, totalAnswers, bestScores (map of categoryId → score)
- `rooms/{roomCode}` — hostId, categoryId, players map, status (waiting/playing/finished), gameId
- `games/{gameId}` — players with scores/powerUps, questions, currentRound, roundStartTime, status, mode, roundClaimedBy
- `custom_quizzes/{quizId}` — creatorId, title, categoryId, questions list, playCount, isFeatured, createdAt

## Room Database

- **Database name:** `techmain_database`
- **Destructive migration is ON** (`fallbackToDestructiveMigration(true)`). Schema changes will wipe data.
- **Single table:** `Avatar` (id, name, avatarEmoji)

## Key Gotchas

- **All UI strings are hardcoded in Indonesian** — not in `strings.xml` (which only has `app_name`).
- **`submitAnswer` MUST use `db.runTransaction`**, not `gameRef.update()` — needs to atomically check `roundClaimedBy` before claiming. Never revert to simple `update()`.
- **Kotlin map write syntax:** `map["key"] = value` writes the entry. `map["key"] to value` is a no-op (creates a discarded Pair). Do not confuse them.
- **Advance-round has 2s answer feedback delay:** `checkAndAdvanceRound` sets `isShowingCorrectAnswer = true`, waits 2s, then calls `advanceRound`. The `isShowingCorrectAnswer` guard blocks re-entry. The timer pauses during this window (200ms loop in `startRoundTimer`). Works for both VS Bot and multiplayer — each client independently waits 2s; Firestore's `currentRound` transaction guard prevents double-advance.
- **Do NOT use boolean flags for advance-round gating:** The `advancing` flag can get stuck forever if `advanceRound` throws (network error). Use `game.currentRound == lastRound` instead — it resets naturally when the round advances, without getting stuck.
- **`advanceRound` has a guard** `if (game.currentRound != currentRound) return` — safe against concurrent calls from subscriber + manual triggers.
- **`listenGame` must be called only once per `gameId`** — the `listenRoom` handler uses `_state.value.gameId != room.gameId` guard. Without this, duplicate calls cancel the previous listener, reset `lastRound = -1`, and restart the timer mid-game.
- **Bot's `submitAnswer` needs try-catch:** the transaction can throw (contention/retry exhaustion); without a catch the exception propagates uncaught and `isReady` is never set for the bot, hanging the game forever.
- **`botSubmitAnswer` must pass `game.mode`** to `submitAnswer()` so bot gets correct points for the game mode.
- **`BattleViewModel` and `LeaderboardViewModel`** extend plain `ViewModel` (use Firebase directly). `ProfileViewModel` extends `AndroidViewModel` (needs Room DAO via `TechMainApp`). `CreatorViewModel` extends `ViewModel` (uses Firebase directly).
- **Quiz questions** (120 total, 6 categories × 20) are hardcoded in `QuestionBank.kt`. 5 random per game (10 for marathon mode).
- **No offline Firestore persistence** configured.
- **Host cannot start game with < 2 players:** "MULAI GAME" button has `enabled = room.players.size >= 2`.
- **`forfeitPlayer` deletes player from game doc** (`FieldValue.delete()`) — removes the player from the `players` map entirely, so `advanceRound` can't reset them back to `isReady = false` the next round.
- **`checkAndAdvanceRound` multiplayer check uses `isNotEmpty()` + `roundClaimedBy`:** the condition is `game.players.isNotEmpty() && (game.roundClaimedBy.isNotEmpty() || game.players.values.all { it.isReady })`. The `roundClaimedBy` check ensures first-correct-wins advances immediately; the `isNotEmpty()` (not `>= 2`) handles the case after forfeit removes a player.
- **`freezeOpponent` targets opponent ID:** `val opponentId = game.players.keys.firstOrNull { it != userId }`, not the caller's own ID.
- **`doublePoints` multiplier passed to Firestore:** `multiplier: Float = 1f` parameter in `submitAnswer()`; ViewModel passes `2f` when `doublePointsActive` is true.
- **`timeFrozenUntil` consumed by timer:** `startRoundTimer` reads `game.players[myId]?.timeFrozenUntil` each loop iteration and pauses with 500ms delay while frozen.
- **`leaveRoom()` handles mid-game exit:** calls `forfeitPlayer()` + cancels `gameListenerJob`/`timerJob` before resetting state, so opponent doesn't hang.
- **`endGame()` marks game finished first, then updates player stats:** The `status = "finished"` write happens before per-player stat updates, so a partial failure doesn't leave the game hanging. Per-player failures are isolated with try-catch.
- **Tie scores handled correctly:** When scores are tied, `winnerId` is empty. The `hasWinner = winnerId.isNotEmpty()` guard skips both wins and losses for all players (no rating change).
- **Optimistic `hasAnswered` must be reset on error:** `submitAnswer` sets `hasAnswered = true` before the Firestore transaction. If the transaction fails, the catch block must reset `hasAnswered = false` so the user can retry.
- **`serverTimestamp()` unreliable in `set()`:** Use `System.currentTimeMillis()` instead of `FieldValue.serverTimestamp()` inside `set()` calls. `serverTimestamp()` is intended for `update()` or `SetOptions.merge()`.
- **Custom quiz creation: upload images before Firestore doc:** Images are uploaded to Storage first with a pre-generated UUID quiz ID. Only after all uploads succeed is the Firestore document created. This prevents orphaned quizzes with missing questions/images.
- **Quiz deletion: delete Storage before Firestore:** `deleteQuiz` removes media files from Storage first, then deletes the Firestore doc. Reversing this order could orphan media files if the Storage deletion fails.
