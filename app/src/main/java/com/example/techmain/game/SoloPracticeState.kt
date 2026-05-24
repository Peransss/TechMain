package com.example.techmain.game

import com.example.techmain.firebase.QuestionBank
import com.example.techmain.firebase.QuizQuestion
import kotlin.random.Random

data class SoloPracticeConfig(
    val categoryId: String,
    val totalRounds: Int = 5,
    val roundTimeLimit: Int = 10
)

data class SoloPracticeState(
    val config: SoloPracticeConfig = SoloPracticeConfig("", 5),
    val questions: List<QuizQuestion> = emptyList(),
    val currentRound: Int = 0,
    val score: Int = 0,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0,
    val streak: Int = 0,
    val maxStreak: Int = 0,
    val expEarned: Int = 0,
    val coinsEarned: Int = 0,
    val selectedAnswer: Int = -1,
    val hasAnswered: Boolean = false,
    val timeLeft: Int = 10,
    val isFinished: Boolean = false,
    val status: String = "ready"
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentRound)
    val accuracy: Int get() = if (totalAnswered > 0) (correctCount * 100 / totalAnswered) else 0
    val totalRounds: Int get() = questions.size

    fun start(): SoloPracticeState {
        val qs = QuestionBank.getQuestions(config.categoryId)
            .shuffled()
            .take(config.totalRounds)
        return copy(
            questions = qs,
            status = "playing",
            timeLeft = config.roundTimeLimit
        )
    }

    fun submitAnswer(answerIndex: Int): SoloPracticeState {
        val q = currentQuestion ?: return this
        val isCorrect = answerIndex == q.correctAnswer
        val newStreak = if (isCorrect) streak + 1 else 0
        val streakBonus = if (isCorrect && newStreak > 0 && newStreak % 5 == 0) 50 else 0
        val points = if (isCorrect) 100 + streakBonus else 0
        val exp = if (isCorrect) Random.nextInt(10, 31) else 0
        val coins = if (isCorrect) Random.nextInt(1, 6) else 0

        val nextRound = currentRound + 1
        val isFinished = nextRound >= totalRounds

        return copy(
            selectedAnswer = -1,    // DIPERBAIKI: Reset pilihan jawaban untuk soal berikutnya
            hasAnswered = false,    // DIPERBAIKI: Reset status agar soal berikutnya bisa dijawab
            score = score + points,
            correctCount = correctCount + (if (isCorrect) 1 else 0),
            totalAnswered = totalAnswered + 1,
            streak = newStreak,
            maxStreak = maxOf(maxStreak, newStreak),
            expEarned = expEarned + exp,
            coinsEarned = coinsEarned + coins,
            currentRound = nextRound,
            timeLeft = if (isFinished) 0 else config.roundTimeLimit,
            isFinished = isFinished,
            status = if (isFinished) "finished" else "playing"
        )
    }

    fun timeout(): SoloPracticeState {
        val nextRound = currentRound + 1
        val isFinished = nextRound >= totalRounds
        return copy(
            selectedAnswer = -1,
            hasAnswered = false,    // DIPERBAIKI: Reset status agar soal berikutnya bisa dijawab saat waktu habis
            totalAnswered = totalAnswered + 1,
            streak = 0,
            currentRound = nextRound,
            timeLeft = if (isFinished) 0 else config.roundTimeLimit,
            isFinished = isFinished,
            status = if (isFinished) "finished" else "playing"
        )
    }

    fun selectAnswer(index: Int): SoloPracticeState {
        return copy(selectedAnswer = index)
    }

    fun tick(): SoloPracticeState {
        if (hasAnswered || isFinished) return this
        val newTime = timeLeft - 1
        return if (newTime <= 0) timeout() else copy(timeLeft = newTime)
    }
}