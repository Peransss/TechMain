# Game Modes Design

**Date:** 2026-05-23
**Status:** Approved for implementation

## Overview

Add two new game mode categories to TechMain: **Solo Practice** (local-only practice with progression) and **Competitive Modes** (variant rule sets for multiplayer/VS Bot). Category mastery and daily challenges fold into Solo Practice.

---

## Solo Practice

### Entry
New "LATIHAN" button on the lobby. Opens a setup panel:

### Setup
- **Rounds:** 5 or 10 (user selects)
- **Timer:** 10 seconds per question (fixed)
- **Category:** Pick from 6 categories

### Gameplay (Local-Only)
- No Firestore round trips — questions fetched from `QuestionBank` and shuffled locally
- Same question UI as competitive, but powered by a local state machine (no `BattleViewModel` involvement)
- Timer counts down from 10s

### Scoring
- Correct answer: **100 pts**
- Streak bonus: every 5th consecutive correct = **+50 pts**
- EXP per correct: random **10–30**
- Coins per correct: random **1–5**

### Streak System
- Tracks consecutive correct answers
- Resets to 0 on wrong answer
- Bonus triggers at multiples of 5 (e.g., 5, 10, 15...)
- Visual feedback on streak milestone reached

### End of Game
- Shows: final score, streak count, accuracy, EXP earned, coins earned
- Syncs EXP/coins to Firestore `users/{uid}` document
- Syncs best score to category mastery

---

## Daily Challenges

- 2–3 fixed quests per day, resets daily
- Examples: "Answer 10 questions in Matematika", "Get 3 correct in a row"
- Bonus coins upon completion
- Stored in Firestore `users/{uid}/dailyChallenges` as a subcollection or map
- Checked on solo practice complete

---

## Category Mastery

- Track **best score** per category on `users/{uid}`
- Display on profile page
- Tiers: Bronze (500), Silver (1000), Gold (2000)
- Updated after every solo practice session

---

## Competitive Modes

### Mode Selection
When creating a room (multiplayer) or starting VS Bot, a **mode picker dialog** appears before the game starts.

### GameSession Changes
- New field `mode: String` — `"casual"`, `"blitz"`, `"marathon"`, `"powerup"`

### Mode: Casual (existing)
- 5 questions, 20s timer, 100 pts per correct

### Mode: Blitz
- 5 questions, 10s timer, **150 pts** per correct
- High risk, high reward

### Mode: Marathon
- **10 questions**, 15s timer, **50 pts** per correct
- Endurance test

### Mode: Power-Up
- 5 questions, 20s timer, standard 100 pts
- Each player gets **3 power-ups** per game:
  - **50:50** — removes 2 wrong answers from the current question
  - **Double Points** — next correct answer gets a random 1.5x–3x multiplier
  - **Time Freeze** — pauses opponent's timer for 5 seconds

### Power-Up UI
- Power-up buttons shown below answer options in Power-Up mode
- Each power-up has a cooldown (used once per game)
- 50:50: gray out eliminated options
- Double Points: indicator on score display
- Time Freeze: visual effect on opponent's timer

### Scoring Per Mode

| Mode | Questions | Timer | Points/Correct | Special |
|------|-----------|-------|----------------|---------|
| Casual | 5 | 20s | 100 | — |
| Blitz | 5 | 10s | 150 | — |
| Marathon | 10 | 15s | 50 | — |
| Power-Up | 5 | 20s | 100 | 3 power-ups per player |

---

## File Changes

### New Files
- `app/.../game/SoloPracticeState.kt` — Solo practice state machine & logic

### Modified Files
- `app/.../firebase/QuizModels.kt` — Add `mode` to GameSession, power-ups to GamePlayer
- `app/.../firebase/FirestoreService.kt` — Mode-aware room creation & scoring
- `app/.../ui/battle/BattleViewModel.kt` — Mode picker dialog, power-up actions
- `app/.../ui/battle/BattleGameScreen.kt` — Power-up UI, mode display
- `app/.../ui/battle/BattleResultScreen.kt` — Solo result display (EXP, coins)
- `app/.../ui/battle/BattleLobbyScreen.kt` — "LATIHAN" button, mode picker
- `app/.../ui/profile/ProfileScreen.kt` — Category mastery display
- `app/.../ui/profile/ProfileViewModel.kt` — Load mastery from Firestore

## Non-Goals

- No changes to Room database (no new entities)
- No changes to leaderboard beyond what Firestore provides
- No animations or sound effects
- No push notifications
