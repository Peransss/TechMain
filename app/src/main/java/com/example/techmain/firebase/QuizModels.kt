package com.example.techmain.firebase

data class QuizQuestion(
    val id: String = "",
    val categoryId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val difficulty: String = "medium",
    val timeLimit: Int = 20
)

data class GamePlayer(
    val userId: String = "",
    val displayName: String = "Pemain",
    val score: Int = 0,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0,
    val isReady: Boolean = false
)

data class GameSession(
    val gameId: String = "",
    val players: Map<String, GamePlayer> = emptyMap(),
    val categoryId: String = "",
    val status: String = "waiting",
    val currentRound: Int = 0,
    val totalRounds: Int = 5,
    val questions: List<QuizQuestion> = emptyList(),
    val roundStartTime: Long = 0L,
    val roundTimeLimit: Int = 20,
    val winnerId: String = "",
    val createdAt: Long = 0L
)

data class MatchmakingTicket(
    val userId: String = "",
    val displayName: String = "Pemain",
    val categoryId: String = "",
    val joinedAt: Long = 0L
)
