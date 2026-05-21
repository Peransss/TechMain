# TechMain — Gamified Study App (RPG)

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
| Nav | Navigation Compose 2.9.0 | 5-tab bottom nav + leaderboard sub-route |
| Local DB | Room 2.7.1 (KSP) | 7 entities, 7 DAOs, schema v1 |
| Backend | Firebase Auth + Firestore | Anonymous sign-in; real-time matchmaking & leaderboard |
| DI | None (manual) | Repositories in `TechMainApp`; ViewModels cast `application` |

## Entry Points

- `TechMainApp` — Application class; creates `AppDatabase` + 4 repositories
- `MainActivity` — Single activity; calls `FirebaseModule.signInAnonymously()` on startup (blocks UI with spinner until done)

## Navigation (NavGraph.kt)

| Route | Screen | Bottom Nav |
|---|---|---|
| `quest` | QuestScreen (to-do list) | ✓ |
| `pomodoro` | PomodoroScreen (timer) | ✓ |
| `flashcard` | FlashcardScreen (spaced repetition) | ✓ |
| `battle` | BattleMainScreen (quiz battle) | ✓ |
| `shop` | ShopScreen (avatar + shop) | ✓ |
| `leaderboard` | LeaderboardScreen | sub-route from Battle |

Bottom nav uses `popUpTo(findStartDestination)`, `launchSingleTop`, `restoreState`.

**BattleMainScreen has its own internal screen routing** (`BattleUiState.screen`: LOBBY/MATCHMAKING/GAME/RESULT) managed by `BattleViewModel`, not `NavHost`.

## Room Database

- **Database name:** `techmain_database`
- **Destructive migration is OFF** (`fallbackToDestructiveMigration(false)`). Schema changes without a migration will crash.
- **Single-row avatar table** — only one avatar per device.
- Entities: `Quest`, `PomodoroSession`, `FlashcardDeck`, `Flashcard`, `Avatar`, `ShopItem`, `Inventory`.

## Firebase Data Model

- `users/{uid}` — rating, wins, losses, totalGames, correctAnswers, totalAnswers
- `games/{gameId}` — real-time game session with players, questions, round state
- `matchmaking/{userId}` — matchmaking ticket (categoryId, joinedAt)

Matchmaking is simplistic: pairs first two tickets in same category by userId string comparison.

## Key Gotchas

- **All UI strings are hardcoded in Indonesian** — not in `strings.xml` (which only has `app_name`).
- **google-services.json is committed** to the repo. The placeholder values have been replaced with a real Firebase project. Rotate API keys if needed.
- **No offline Firestore persistence** configured.
- **No tests exist** beyond boilerplate `ExampleUnitTest`.
- **Quiz questions** (48 total, 6 categories × 8) are hardcoded in `QuestionBank.kt`. 5 random per game.
- **`Icons.Default.ArrowBack`** should use `Icons.AutoMirrored.Filled.ArrowBack` (deprecation warning).
- Android Gradle Plugin 9.2.1 requires `android.disallowKotlinSourceSets=false` in `gradle.properties` when KSP or compose-compiler plugins are used.
