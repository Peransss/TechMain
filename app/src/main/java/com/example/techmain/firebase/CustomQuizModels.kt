package com.example.techmain.firebase

data class CustomQuiz(
    val id: String = "",
    val creatorId: String = "",
    val title: String = "",
    val categoryId: String = "",
    val questions: List<CustomQuestion> = emptyList(),
    val playCount: Int = 0,
    val isFeatured: Boolean = false,
    val createdAt: Long = 0L
)

data class CustomQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val imageUrl: String? = null
)
