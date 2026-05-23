# Final UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement featured quiz display with `LazyRow` and add `AsyncImage` for question images.

**Architecture:** 
1. `BattleViewModel` will be updated to expose `featuredQuizzes`.
2. `BattleMainScreen` will use `LazyRow` to display them.
3. `BattleGameScreen` will use `AsyncImage` (from Coil) for question images.

**Tech Stack:** Jetpack Compose, Coil, ViewModel.

---

### Task 1: Update ViewModel

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt`

- [ ] **Step 1: Update `BattleViewModel` to hold featured quizzes**

```kotlin
// In BattleUiState
data class BattleUiState(
    // ... existing fields
    val featuredQuizzes: List<QuizModel> = emptyList()
)

// In BattleViewModel
private val _state = MutableStateFlow(BattleUiState())

fun init() {
    val userId = FirebaseModule.getUserId() ?: return
    _state.value = _state.value.copy(myUserId = userId, featuredQuizzes = loadFeaturedQuizzes())
}

private fun loadFeaturedQuizzes(): List<QuizModel> {
    // Return mock data for now
    return emptyList()
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt
git commit -m "feat: add featuredQuizzes to BattleViewModel"
```

### Task 2: Implement Featured Quizzes Row in BattleMainScreen

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleMainScreen.kt`

- [ ] **Step 1: Add `LazyRow` to `BattleMainScreen`**

```kotlin
@Composable
fun BattleMainScreen(viewModel: BattleViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    
    // ... inside content
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.featuredQuizzes) { quiz ->
            NeonHackerQuizCard(quiz)
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleMainScreen.kt
git commit -m "feat: add LazyRow to BattleMainScreen"
```

### Task 3: Add AsyncImage to BattleGameScreen

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt`

- [ ] **Step 1: Implement `AsyncImage`**

```kotlin
// In BattleGameScreen.kt
AsyncImage(
    model = currentQuestion.imageUrl,
    contentDescription = "Question Image",
    modifier = Modifier.fillMaxWidth().height(200.dp)
)
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt
git commit -m "feat: add AsyncImage to BattleGameScreen"
```
