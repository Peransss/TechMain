# Soft Neon Theme Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace RPG-fantasy + hacker-neon colors with a cohesive Soft Neon (Neon Slate) dark theme across the TechMain app.

**Architecture:** Pure theme swap — no logic or layout changes. Define new color constants in `Color.kt`, wire them into `Theme.kt` as the Material3 dark scheme, then replace direct `NeonHacker*`/`RPGQuestGold` references in 5 screen files with the new equivalents.

**Tech Stack:** Kotlin, Jetpack Compose, Material3

**Files to modify:**
- `app/src/main/java/com/example/techmain/ui/theme/Color.kt`
- `app/src/main/java/com/example/techmain/ui/theme/Theme.kt`
- `app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt`
- `app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt`
- `app/src/main/java/com/example/techmain/ui/battle/BattleResultScreen.kt`
- `app/src/main/java/com/example/techmain/ui/leaderboard/LeaderboardScreen.kt`
- `app/src/main/java/com/example/techmain/ui/studio/StudioScreen.kt`

---

### Task 1: Define Soft Neon color constants in Color.kt

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/theme/Color.kt`

- [ ] **Step 1: Replace all color constants in Color.kt**

Replace the existing `Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`, `RPG*`, and `NeonHacker*` constants with the new Soft Neon palette.

**New content for Color.kt:**

```kotlin
package com.example.techmain.ui.theme

import androidx.compose.ui.graphics.Color

val NeonSlateBackground = Color(0xFF0F172A)
val NeonSlateSurface = Color(0xFF1E293B)
val NeonSlateSurfaceBorder = Color(0xFF334155)
val NeonSlatePrimary = Color(0xFF22D3EE)
val NeonSlateSecondary = Color(0xFF818CF8)
val NeonSlateAccent = Color(0xFFF472B6)
val NeonSlateSuccess = Color(0xFF34D399)
val NeonSlateError = Color(0xFFFB7185)
val NeonSlateGold = Color(0xFFFBBF24)
val NeonSlateTextPrimary = Color(0xFFF1F5F9)
val NeonSlateTextSecondary = Color(0xFF94A3B8)
```

- [ ] **Step 2: Verify the file compiles**

Run: `./gradlew assembleDebug`
Expected: Build succeeds with no errors related to Color.kt

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/theme/Color.kt
git commit -m "feat: add Soft Neon color palette"
```

---

### Task 2: Update Theme.kt to use Soft Neon dark scheme

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/theme/Theme.kt`

- [ ] **Step 1: Rewrite Theme.kt**

Replace the light/dark/dynamic color scheme setup with a single dark-only scheme using Soft Neon colors.

**New content for Theme.kt:**

```kotlin
package com.example.techmain.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SoftNeonColorScheme = darkColorScheme(
    primary = NeonSlatePrimary,
    onPrimary = NeonSlateBackground,
    secondary = NeonSlateSecondary,
    onSecondary = NeonSlateBackground,
    tertiary = NeonSlateAccent,
    onTertiary = NeonSlateBackground,
    background = NeonSlateBackground,
    onBackground = NeonSlateTextPrimary,
    surface = NeonSlateSurface,
    onSurface = NeonSlateTextPrimary,
    surfaceVariant = NeonSlateSurface,
    onSurfaceVariant = NeonSlateTextSecondary,
    outline = NeonSlateSurfaceBorder,
    error = NeonSlateError,
    onError = NeonSlateBackground
)

@Composable
fun TechMainTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SoftNeonColorScheme,
        typography = Typography,
        content = content
    )
}
```

- [ ] **Step 2: Verify the file compiles**

Run: `./gradlew assembleDebug`
Expected: Build succeeds with no errors related to Theme.kt

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/theme/Theme.kt
git commit -m "feat: switch to Soft Neon dark theme"
```

---

### Task 3: Update BattleGameScreen.kt color references

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt`

- [ ] **Step 1: Replace NeonHackerPrimary with NeonSlatePrimary**

In the import section:
```kotlin
import com.example.techmain.ui.theme.NeonSlatePrimary
// Remove: import com.example.techmain.ui.theme.NeonHackerPrimary
```

In the composable (line ~87):
```kotlin
// Before:
.border(2.dp, NeonHackerPrimary)
// After:
.border(2.dp, NeonSlatePrimary)
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew assembleDebug`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt
git commit -m "feat: update BattleGameScreen to Soft Neon colors"
```

---

### Task 4: Update BattleLobbyScreen.kt color references

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt`

- [ ] **Step 1: Replace NeonHacker* colors with NeonSlate* equivalents**

Import changes:
```kotlin
import com.example.techmain.ui.theme.NeonSlateBackground
import com.example.techmain.ui.theme.NeonSlatePrimary
// Remove: import com.example.techmain.ui.theme.NeonHackerBackground
// Remove: import com.example.techmain.ui.theme.NeonHackerBorder
// Remove: import com.example.techmain.ui.theme.NeonHackerPrimary
```

Usage changes:
- Line ~80: `color = NeonHackerPrimary` → `color = NeonSlatePrimary`
- Line ~92: `NeonHackerPrimary` → `NeonSlatePrimary`
- Line ~94: `NeonHackerBackground` → `NeonSlateBackground`
- Line ~99: `NeonHackerPrimary` → `NeonSlatePrimary`

- [ ] **Step 2: Verify compilation**

Run: `./gradlew assembleDebug`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt
git commit -m "feat: update BattleLobbyScreen to Soft Neon colors"
```

---

### Task 5: Update BattleResultScreen.kt color references

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleResultScreen.kt`

- [ ] **Step 1: Replace RPGQuestGold with NeonSlateGold**

Import changes:
```kotlin
import com.example.techmain.ui.theme.NeonSlateGold
// Remove: import com.example.techmain.ui.theme.RPGQuestGold
```

Usage changes:
- Line ~51: `tint = if (isWinner) RPGQuestGold else ...` → `tint = if (isWinner) NeonSlateGold else ...`
- Line ~58: `color = if (isWinner) RPGQuestGold else ...` → `color = if (isWinner) NeonSlateGold else ...`

- [ ] **Step 2: Verify compilation**

Run: `./gradlew assembleDebug`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleResultScreen.kt
git commit -m "feat: update BattleResultScreen to Soft Neon colors"
```

---

### Task 6: Update LeaderboardScreen.kt color references

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/leaderboard/LeaderboardScreen.kt`

- [ ] **Step 1: Replace RPGQuestGold with NeonSlateGold**

Import changes:
```kotlin
import com.example.techmain.ui.theme.NeonSlateGold
// Remove: import com.example.techmain.ui.theme.RPGQuestGold
```

Usage changes:
- Line ~181: `color = RPGQuestGold` → `color = NeonSlateGold`

- [ ] **Step 2: Verify compilation**

Run: `./gradlew assembleDebug`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/leaderboard/LeaderboardScreen.kt
git commit -m "feat: update LeaderboardScreen to Soft Neon colors"
```

---

### Task 7: Update StudioScreen.kt color references

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/studio/StudioScreen.kt`

- [ ] **Step 1: Replace NeonHacker* colors with NeonSlate* equivalents**

Import changes:
```kotlin
import com.example.techmain.ui.theme.NeonSlateBackground
import com.example.techmain.ui.theme.NeonSlatePrimary
import com.example.techmain.ui.theme.NeonSlateSurfaceBorder
// Remove: import com.example.techmain.ui.theme.NeonHackerBackground
// Remove: import com.example.techmain.ui.theme.NeonHackerBorder
// Remove: import com.example.techmain.ui.theme.NeonHackerSecondary
```

Usage changes:
- Line ~32: `containerColor = NeonHackerBackground` → `containerColor = NeonSlateBackground`
- Line ~36: `containerColor = NeonHackerSecondary` → `containerColor = NeonSlatePrimary`
- Line ~48: `.border(1.dp, NeonHackerBorder, ...)` → `.border(1.dp, NeonSlateSurfaceBorder, ...)`

- [ ] **Step 2: Verify compilation**

Run: `./gradlew assembleDebug`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/studio/StudioScreen.kt
git commit -m "feat: update StudioScreen to Soft Neon colors"
```

---

### Task 8: Final build verification

**Files:**
- Verify: full project

- [ ] **Step 1: Run full build**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Quick grep for old color references (cleanup check)**

Run: `rg "NeonHacker|RPGQuestGold|RPGHpRed|RpgMana|RpgXp|RpgBgDark|RpgBgSurface|RpgAccent|Purple80|Purple40|PurpleGrey" app/src/main/java/com/example/techmain/`
Expected: No matches (or only matches in the design spec docs)
