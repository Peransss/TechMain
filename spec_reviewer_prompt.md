Review the implementation of Task 7: Battle Integration (Featured & Images).

Requirements check:
1. Does `BattleViewModel` now expose `featuredQuizzes` as `StateFlow<List<CustomQuiz>>`?
2. Is `featuredQuizzes` initialized from `firestoreService.fetchFeaturedQuizzes()`?
3. Does `BattleLobbyScreen` contain a `LazyRow` displaying these featured quizzes?
4. Are the quiz cards styled with Neon Hacker theme (dark background, cyan borders/text)?
5. Does `BattleGameScreen` display an `AsyncImage` for `currentQuestion.imageUrl` if it's not null?
6. Does the `AsyncImage` use the specified modifier?
