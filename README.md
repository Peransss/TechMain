# TechMain — Educational Battle Quiz Game

[![Kotlin](https://img.shields.io/badge/kotlin-2.1.20-%237F52FF?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-BOM_2025.05.00-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase)](https://firebase.google.com)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

TechMain is an Android multiplayer educational quiz game inspired by ZEP Quiz. Compete in real-time quiz battles against friends or an AI bot across multiple categories with power-ups, streaks, and a first-correct-wins mechanic.

## Features

- **Real-time Multiplayer** — Create or join rooms with a 6-character code; first correct answer wins each round
- **VS Bot Mode** — Practice against an AI opponent with configurable difficulty (Easy / Medium / Hard)
- **Solo Practice** — Local self-paced quizzes with streak bonuses and scoring
- **4 Game Modes** — Casual, Blitz, Marathon, and Power-up mode with 3 special abilities
- **Custom Quiz Studio** — Create, manage, and share your own quizzes with image uploads
- **Leaderboards** — Global rankings tracked via Firestore
- **Profile & Avatars** — Track stats, category mastery, and customize your avatar
- **Soft Neon Glassmorphism** — Dark-themed UI with glass cards, neon accents, and glowing effects

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.1.20 |
| UI | Jetpack Compose + Material3 (dark-only theme) |
| Architecture | MVVM (ViewModel + StateFlow) |
| Navigation | Navigation Compose 2.9.0 (4-tab bottom nav) |
| Backend | Firebase Auth (anonymous), Firestore (real-time), Storage |
| Local DB | Room 2.7.1 with KSP (Avatar entity only) |
| Image Loading | Coil 3.0.4 with OkHttp network layer |
| DI | Manual (no DI framework) |
| Min SDK / Target | 30 / 36 |

## Getting Started

### Prerequisites

1. A Firebase project with **Anonymous Authentication** and **Firestore** enabled
2. Place your `google-services.json` in `app/google-services.json`

### Build & Run

```bash
./gradlew assembleDebug    # Build debug APK
./gradlew installDebug     # Install on connected device
./gradlew test             # Run unit tests
```

## Game Modes

| Mode | Timer | Rounds | Score/Correct | Notes |
|---|---|---|---|---|
| Casual | 20s | 5 | 100 | Default |
| Blitz | 10s | 5 | 150 | Fast-paced |
| Marathon | 15s | 10 | 50 | Endurance |
| Power-up | 20s | 5 | 100 | 3 power-ups (50:50, 2×, Freeze) |

### Power-ups

- **50:50** — Eliminates 2 wrong options for the round
- **2× Points** — Doubles your score for the round
- **Freeze** — Freezes opponent's timer for 5 seconds

### VS Bot Difficulty

| Difficulty | Correct % | Answer Delay |
|---|---|---|
| Easy | 25% | 8–15s |
| Medium | 50% | 5–12s |
| Hard | 80% | 2–7s |

## Architecture

TechMain follows an **MVVM pattern** with manual dependency injection:

```
UI Layer (Compose) → ViewModel (StateFlow) → FirestoreService / Room DAO
```

- **Screens** are `@Composable` functions observing `StateFlow` via `collectAsState()`
- **ViewModels** hold game state and coordinate Firebase transactions
- **FirestoreService** handles all real-time document reads/writes with atomic transactions for round claiming
- **SoloPracticeState** is a pure local state machine (no Firestore dependency)

### Project Structure

```
app/src/main/java/com/example/techmain/
├── TechMainApp.kt              # Application class, Room DB init
├── MainActivity.kt             # Single activity, Firebase auth
├── data/db/                    # Room database (Avatar)
├── firebase/                   # FirestoreService, QuizModels, QuestionBank, StorageService
├── game/                       # BotAnswerEngine, SoloPracticeState
└── ui/
    ├── battle/                 # BattleMainScreen, BattleGameScreen, BattleLobbyScreen, etc.
    ├── components/             # Shared glassmorphism components (GlassCard, AnswerButton)
    ├── leaderboard/            # LeaderboardScreen + ViewModel
    ├── navigation/             # NavGraph (Screen routes)
    ├── profile/                # ProfileScreen + ViewModel
    ├── studio/                 # StudioScreen, CreatorWizardScreen + ViewModel
    └── theme/                  # Color, Type, Theme (dark-only neon scheme)
```

## Firebase Data Model

- `users/{uid}` — displayName, rating, wins/losses, best scores per category
- `rooms/{roomCode}` — hostId, categoryId, players, status (waiting/playing/finished)
- `games/{gameId}` — players (scores, power-ups), questions, currentRound, roundClaimedBy, mode
- `custom_quizzes/{quizId}` — creatorId, title, questions, playCount

## Local Database (Room)

- **Database:** `techmain_database` (destructive migration enabled)
- **Entity:** `Avatar` (id, name, avatarEmoji)

## Quiz Content

120 hardcoded questions across 6 categories (20 each) in `QuestionBank.kt`. 5 random per game (10 in Marathon mode). Custom quizzes are stored in Firestore.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/my-feature`)
3. Commit your changes (`git commit -m 'Add feature'`)
4. Push to the branch (`git push origin feat/my-feature`)
5. Open a Pull Request

## License

MIT
