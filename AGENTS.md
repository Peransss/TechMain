# TechMain — Educational Battle Quiz Game

## Build & Run

```bash
./gradlew assembleDebug                          # build debug APK
./gradlew installDebug                           # install on connected device
./gradlew test                                   # unit tests (none written yet)
```

**Prerequisite:** `app/google-services.json` from a Firebase project with Anonymous Auth + Firestore enabled. This file is committed; replace with your own Firebase config if forking.

## Architecture

| Layer | Technology | Notes |
|---|---|---|
| UI | Jetpack Compose + Material3 | All screens are `@Composable` |
| State | ViewModel + StateFlow / Flow | `collectAsState()` in composables |
| Nav | Navigation Compose 2.9.0 | 3-tab bottom nav |
| Local DB | Room 2.7.1 (KSP) | 1 entity (Avatar), schema v2, destructive migration enabled |
| Backend | Firebase Auth + Firestore | Anonymous sign-in; real-time rooms, games & leaderboard |
| DI | None (manual) | ViewModels access `TechMainApp.database` or Firebase directly |

## Entry Points

- `TechMainApp` — Application class; creates `AppDatabase` (Avatar only)
- `MainActivity` — Single activity; calls `FirebaseModule.signInAnonymously()` on startup (blocks UI with spinner until done)

## Navigation

| Route | Screen | Bottom Nav |
|---|---|---|
| `battle` | BattleMainScreen (create/join room → quiz) | ✓ |
| `leaderboard` | LeaderboardScreen (global rankings) | ✓ |
| `profile` | ProfileScreen (stats, avatar, name) | ✓ |

Bottom nav uses `popUpTo(findStartDestination)`, `launchSingleTop`, `restoreState`.

**BattleMainScreen has internal screen routing** (`BattleUiState.screen`: LOBBY/JOIN_ROOM/WAITING_ROOM/GAME/RESULT) managed by `BattleViewModel`, not `NavHost`.

## Room System (like ZEP Quiz)

- Users create rooms (6-char code) or join via code
- Host selects category; room stored in Firestore `rooms/{roomCode}`
- When host clicks "Mulai Game", questions are pulled from `QuestionBank` and a `games/{gameId}` document is created
- All players get the same 5 questions simultaneously
- 20-second timer per round; answers submitted to Firestore in real-time
- Game advances when all players have answered

## Firebase Data Model

- `users/{uid}` — displayName, rating, wins, losses, totalGames, correctAnswers, totalAnswers
- `rooms/{roomCode}` — hostId, categoryId, players map, status (waiting/playing/finished), gameId
- `games/{gameId}` — players with scores, questions, currentRound, roundStartTime, status

## Room Database

- **Database name:** `techmain_database`
- **Destructive migration is ON** (`fallbackToDestructiveMigration(true)`). Schema changes will wipe data.
- **Single table:** `Avatar` (id, name, avatarEmoji)

## Key Gotchas

- **All UI strings are hardcoded in Indonesian** — not in `strings.xml` (which only has `app_name`).
- **google-services.json is committed** — replace with your own Firebase config if forking. Needs Anonymous Auth + Firestore enabled.
- **No offline Firestore persistence** configured.
- **No tests exist** beyond boilerplate `ExampleUnitTest`.
- **Quiz questions** (48 total, 6 categories × 8) are hardcoded in `QuestionBank.kt`. 5 random per game.
- **`Icons.Default.ArrowBack`** should use `Icons.AutoMirrored.Filled.ArrowBack` (deprecation warning).
- Android Gradle Plugin 9.2.1 requires `android.disallowKotlinSourceSets=false` in `gradle.properties` when KSP or compose-compiler plugins are used.
- **`BattleViewModel` and `LeaderboardViewModel`** extend plain `ViewModel` (use Firebase directly). `ProfileViewModel` extends `AndroidViewModel` (needs Room DAO via `TechMainApp`).
