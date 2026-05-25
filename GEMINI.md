# TechMain — Educational Battle Quiz Game

TechMain is an Android-based multiplayer educational quiz game inspired by "ZEP Quiz". It allows users to compete in real-time quiz battles, either against other players or an AI bot, with categories spanning various educational subjects.

## Project Overview

*   **Platform:** Android (Kotlin)
*   **UI Framework:** Jetpack Compose with Material3
*   **Backend:** Firebase (Authentication, Firestore)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **State Management:** Kotlin Flows (StateFlow)

## Building and Running

*   **Build Project:** `./gradlew assembleDebug`
*   **Run on Device:** `./gradlew installDebug`
*   **Run Unit Tests:** `./gradlew test` (Note: Minimal test coverage currently exists)
*   **Lint Check:** `./gradlew lint`

**Prerequisite:** A valid `app/google-services.json` file is required. The project uses Firebase Anonymous Authentication and Firestore.

## Architecture & Technology Stack

### Backend (Firebase)
*   **Firestore:** Used for real-time room management, game sessions, and global leaderboards.
*   **Auth:** Anonymous sign-in handles user identity.
*   **Service Layer:** `FirestoreService.kt` manages all database interactions, including atomic round advancement using Firestore Transactions.

### Game Logic (VS Bot)
*   **AI Engine:** `BotAnswerEngine.kt` simulates AI behavior with adjustable difficulty (Easy, Medium, Hard).
*   **Response Timing:** Bot answer delays are scaled by difficulty (e.g., Hard is faster and more accurate).
*   **Reactive Flow:** Round advancement is triggered reactively by `BattleViewModel` monitoring player readiness, ensuring stability across network latencies.

### UI & Navigation
*   **Navigation:** Uses `Navigation Compose`. The main flow is defined in `MainActivity.kt` and `NavGraph.kt`.
*   **Battle Screens:** Managed internally within `BattleMainScreen.kt` using a custom state-driven routing (`LOBBY`, `WAITING_ROOM`, `GAME`, `RESULT`).
*   **Theming:** Soft Neon Glassmorphism (custom dark-only theme using `NeonSlate` palette) with consistent glassmorphism effects (GlassCard, AnswerButton).

## Development Conventions

*   **Reactive Programming:** Always prefer reactive snapshot listeners over one-shot polls for real-time features.
*   **Atomicity:** Use Firestore Transactions for state transitions (like `advanceRound`) to prevent race conditions.
*   **Hardcoded Strings:** Currently, most UI strings are hardcoded in Indonesian. New features should ideally use `strings.xml`.
*   **Icons:** Use `Icons.AutoMirrored` for directional icons (e.g., `ArrowBack`) to follow current Material standards.
*   **Bot IDs:** Internal system entities like the AI bot should use the prefix `system_bot_` to avoid collisions with user UIDs.
*   **UI Components:** Use shared `GlassCard` and `NeonButton`/`AnswerButton` for all game screens to maintain consistent Soft Neon styling.

## Key Modules

*   `app/src/main/java/com/example/techmain/firebase/`: Data models and Firestore service.
*   `app/src/main/java/com/example/techmain/ui/battle/`: Core game UI and ViewModel.
*   `app/src/main/java/com/example/techmain/game/`: AI logic and difficulty settings.
*   `app/src/main/java/com/example/techmain/data/db/`: Local Room database (primarily for local user profile/avatar).
