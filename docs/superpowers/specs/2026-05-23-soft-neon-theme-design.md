# Soft Neon Theme Design

**Date:** 2026-05-23
**Status:** Approved for implementation

## Overview

Replace the existing mixed RPG-fantasy + hacker-neon visual style with a cohesive **Soft Neon** (Neon Slate) dark theme. The goal is a simpler, more modern look with dark navy backgrounds, desaturated neon accents (cyan, indigo, pink), and improved readability — "cyber but easy on the eyes."

## Color Palette

| Token | Color | Hex | Usage |
|-------|-------|-----|-------|
| `NeonSlateBackground` | Deep Navy | `#0F172A` | Main app background |
| `NeonSlateSurface` | Slate Navy | `#1E293B` | Cards, sheets, elevated surfaces |
| `NeonSlateSurfaceBorder` | Muted Slate | `#334155` | Card borders, dividers |
| `NeonSlatePrimary` | Soft Cyan | `#22D3EE` | Primary buttons, active states, links |
| `NeonSlateSecondary` | Soft Indigo | `#818CF8` | Secondary actions, highlights |
| `NeonSlateAccent` | Soft Pink | `#F472B6` | Badges, decorative highlights |
| `NeonSlateSuccess` | Soft Green | `#34D399` | Correct answer indicators |
| `NeonSlateError` | Soft Red | `#FB7185` | Wrong answer indicators, errors |
| `NeonSlateGold` | Warm Gold | `#FBBF24` | Wins, trophy icons, rating numbers |
| `NeonSlateTextPrimary` | Near White | `#F1F5F9` | Primary body text |
| `NeonSlateTextSecondary` | Muted Gray | `#94A3B8` | Secondary text, labels, hints |

## Theme Strategy

- Single dark-only theme (no light mode). The app's battle quiz nature suits a dark interface.
- Disable the dynamic color (Material You) fallback. Lock the app to the custom Soft Neon dark scheme for a consistent brand experience.
- The existing default purple Material3 colors (`Purple80`, `Purple40`, etc.) are removed.

## Files to Change

### 1. `Color.kt`
- Remove all `RPG*` and `NeonHacker*` color constants
- Define the 11 new `NeonSlate*` color constants

### 2. `Theme.kt`
- Replace `DarkColorScheme` to use `NeonSlate*` colors mapped to Material3 roles
- Remove light scheme and dynamic color support
- Keep the `TechMainTheme` composable signature but lock to dark

### 3. Screen Files (color reference swaps only)

| File | Old Color(s) | New Color(s) |
|------|-------------|--------------|
| `BattleGameScreen.kt` | `NeonHackerPrimary` (image border) | `NeonSlatePrimary` |
| `BattleLobbyScreen.kt` | `NeonHackerPrimary`, `NeonHackerBackground` | `NeonSlatePrimary`, `NeonSlateBackground` |
| `BattleResultScreen.kt` | `RPGQuestGold` (trophy + text) | `NeonSlateGold` |
| `LeaderboardScreen.kt` | `RPGQuestGold` (rating number) | `NeonSlateGold` |
| `StudioScreen.kt` | `NeonHackerBackground`, `NeonHackerBorder`, `NeonHackerSecondary` | `NeonSlateBackground`, `NeonSlateSurfaceBorder`, `NeonSlatePrimary` |

## Non-Goals

- No layout or structure changes to any screen
- No typography changes (Type.kt stays as-is)
- No new components or features
- No Room database changes
- No Firebase or backend changes

## Verification

- Run `./gradlew assembleDebug` — build must succeed
- Visual inspection: screens should render with the new color scheme without layout regressions
