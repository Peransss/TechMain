package com.example.techmain.game

import kotlin.random.Random

enum class BotDifficulty(val label: String, val correctChance: Float, val minDelay: Long, val maxDelay: Long) {
    EASY("Easy", 0.25f, 8000L, 15000L),
    MEDIUM("Medium", 0.50f, 5000L, 12000L),
    HARD("Hard", 0.80f, 2000L, 7000L)
}

object BotAnswerEngine {
    fun shouldBeCorrect(difficulty: BotDifficulty): Boolean {
        return Random.nextFloat() < difficulty.correctChance
    }

    fun getAnswer(correctAnswer: Int, isCorrect: Boolean, optionsCount: Int): Int {
        if (isCorrect) return correctAnswer
        if (optionsCount <= 1) return correctAnswer
        var wrong = Random.nextInt(optionsCount)
        while (wrong == correctAnswer) wrong = Random.nextInt(optionsCount)
        return wrong
    }
}
