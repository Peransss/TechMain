Task: Update BattleViewModel, BattleLobbyScreen, and BattleGameScreen for Battle Integration (Featured & Images).

1. **Update `BattleViewModel.kt`**:
   - Add `featuredQuizzes` as a `StateFlow<List<CustomQuiz>>`.
   - Initialize by collecting `firestoreService.fetchFeaturedQuizzes()` in `init()`.

2. **Update `BattleLobbyScreen.kt`**:
   - In `LobbyContent`, display the featured quizzes (from viewmodel) as a `LazyRow`.
   - Apply Neon Hacker theme (dark card background, Cyan borders, text).

3. **Update `BattleGameScreen.kt`**:
   - Add `AsyncImage` for `currentQuestion.imageUrl` when it is not null.
   - Use modifier `Modifier.fillMaxWidth().height(200.dp).border(2.dp, NeonHackerPrimary)`.

Context:
- Use `FirestoreService` in `BattleViewModel`.
- `CustomQuiz` model has `id`, `title`, etc.
- Theme constants are in `app/src/main/java/com/example/techmain/ui/theme/Color.kt`.
- Use Coil's `AsyncImage` for image loading.
