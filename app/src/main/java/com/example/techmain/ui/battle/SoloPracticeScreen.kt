package com.example.techmain.ui.battle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.techmain.game.SoloPracticeConfig
import com.example.techmain.game.SoloPracticeState
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import androidx.compose.foundation.BorderStroke
import com.example.techmain.ui.components.AnswerButton
import com.example.techmain.ui.components.GlassCard
import com.example.techmain.ui.components.NeonButton
import com.example.techmain.ui.theme.CyberAccent
import com.example.techmain.ui.theme.CyberBackground
import com.example.techmain.ui.theme.CyberGold
import com.example.techmain.ui.theme.CyberPrimary
import com.example.techmain.ui.theme.CyberSurfaceBorder
import com.example.techmain.ui.theme.CyberTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SoloPracticeScreen(
    categoryId: String,
    totalRounds: Int = 5,
    onBack: () -> Unit
) {
    var state by remember {
        mutableStateOf(
            SoloPracticeState(
                config = SoloPracticeConfig(categoryId = categoryId, totalRounds = totalRounds)
            ).start()
        )
    }

    val firestore = remember { FirestoreService() }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            val userId = FirebaseModule.getUserId()
            if (userId != null) {
                firestore.updateBestScore(userId, categoryId, state.score)
            }
        }
    }

    LaunchedEffect(state.status, state.currentRound) {
        if (state.status == "playing" && !state.hasAnswered && state.currentQuestion != null) {
            while (state.timeLeft > 0 && state.status == "playing" && !state.hasAnswered) {
                delay(1000)
                state = state.tick()
            }
            if (!state.hasAnswered && state.status == "playing") {
                state = state.timeout()
            }
        }
    }

    LaunchedEffect(state.showingFeedback) {
        if (state.showingFeedback) {
            delay(1500)
            state = state.advanceAfterFeedback()
        }
    }

    when (state.status) {
        "playing" -> SoloGameContent(
            state = state,
            onSelect = { state = state.selectAnswer(it) },
            onSubmit = { state = state.submitAnswer(state.selectedAnswer) },
            onBack = onBack
        )
        "finished" -> SoloResultContent(
            state = state,
            onBack = onBack,
            onPlayAgain = {
                state = SoloPracticeState(
                    config = SoloPracticeConfig(categoryId = categoryId, totalRounds = totalRounds)
                ).start()
            }
        )
    }
}

@Composable
private fun SoloGameContent(state: SoloPracticeState, onSelect: (Int) -> Unit, onSubmit: () -> Unit, onBack: () -> Unit) {
    val q = state.currentQuestion ?: return
    val correctAnswer = q.correctAnswer

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Latihan Solo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            NeonButton(onClick = onBack, isPrimary = false, isOutlined = true) {
                Text("KELUAR", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))

        Text(
            "Soal ${state.currentRound + 1} / ${state.totalRounds}",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(4.dp))

        val timerProgress = state.timeLeft.toFloat() / state.config.roundTimeLimit.toFloat()
        LinearProgressIndicator(
            progress = { timerProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = if (state.timeLeft > 3) CyberPrimary else CyberAccent,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            "${state.timeLeft}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (state.timeLeft > 3) CyberPrimary else CyberAccent
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Skor: ${state.score}", fontWeight = FontWeight.Bold)
            if (state.streak > 0) {
                Text("🔥 ${state.streak}", color = CyberGold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(q.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        q.options.forEachIndexed { index, option ->
            val isSelected = state.selectedAnswer == index
            AnswerButton(
                text = "${('A' + index)}. $option",
                onClick = { onSelect(index) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                enabled = !state.hasAnswered,
                isSelected = isSelected,
                isCorrect = state.showingFeedback && index == correctAnswer,
                isWrong = state.showingFeedback && isSelected && !state.lastAnswerCorrect
            )
        }

        Spacer(Modifier.height(16.dp))

        if (!state.hasAnswered) {
            NeonButton(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = state.selectedAnswer >= 0,
                isPrimary = true
            ) {
                Text("KONFIRMASI", fontWeight = FontWeight.Bold)
            }
        } else {
            Text(
                if (state.showingFeedback) {
                    if (state.lastAnswerCorrect) "✅ Benar!" else "❌ Salah!"
                } else {
                    "Menunggu soal berikutnya..."
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = if (state.showingFeedback && state.lastAnswerCorrect) Color(0xFF34D399) else CyberAccent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SoloResultContent(state: SoloPracticeState, onBack: () -> Unit, onPlayAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Latihan Selesai!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))

        Text("${state.score}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Benar: ${state.correctCount}/${state.totalAnswered}")
        Text("Akurasi: ${state.accuracy}%")
        Text("Streak Terbaik: ${state.maxStreak}")
        Spacer(Modifier.height(16.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = CyberPrimary.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f))
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hadiah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("EXP: +${state.expEarned}", fontWeight = FontWeight.Bold, color = CyberPrimary)
                Text("Koin: +${state.coinsEarned}", fontWeight = FontWeight.Bold, color = CyberGold)
            }
        }

        Spacer(Modifier.height(32.dp))

        NeonButton(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            isPrimary = true
        ) {
            Text("LATIHAN LAGI", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))

        NeonButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            isPrimary = false,
            isOutlined = true
        ) {
            Text("KEMBALI")
        }
    }
}
