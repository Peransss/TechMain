package com.example.techmain.firebase

data class QuizQuestion(
    val id: String = "",
    val categoryId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val difficulty: String = "medium",
    val timeLimit: Int = 20,
    val imageUrl: String? = null
)

data class GamePlayer(
    val userId: String = "",
    val displayName: String = "Pemain",
    val score: Int = 0,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0,
    val isReady: Boolean = false,
    val powerUps: Map<String, Boolean> = mapOf(
        "fiftyFifty" to true,
        "doublePoints" to true,
        "timeFreeze" to true
    ),
    val activeDoubleMultiplier: Float = 1f,
    val timeFrozenUntil: Long = 0L
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
    val createdAt: Long = 0L,
    val mode: String = "casual",
    val roundClaimedBy: String = ""
)

data class GameRoom(
    val roomCode: String = "",
    val hostId: String = "",
    val categoryId: String = "",
    val players: Map<String, RoomPlayer> = emptyMap(),
    val status: String = "waiting",
    val gameId: String = "",
    val createdAt: Long = 0L
)

data class RoomPlayer(
    val userId: String = "",
    val displayName: String = "Pemain",
    val isReady: Boolean = false
)
