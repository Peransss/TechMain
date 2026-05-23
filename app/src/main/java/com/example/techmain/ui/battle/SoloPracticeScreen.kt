package com.example.techmain.ui.battle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.techmain.game.SoloPracticeConfig
import com.example.techmain.game.SoloPracticeState
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import com.example.techmain.ui.theme.NeonSlateGold
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

    when (state.status) {
        "playing" -> SoloGameContent(
            state = state,
            onSelect = { state = state.selectAnswer(it) },
            onSubmit = { state = state.submitAnswer(state.selectedAnswer) }
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
private fun SoloGameContent(state: SoloPracticeState, onSelect: (Int) -> Unit, onSubmit: () -> Unit) {
    val q = state.currentQuestion ?: return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Latihan Solo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
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
            color = if (state.timeLeft > 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            "${state.timeLeft}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (state.timeLeft > 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Skor: ${state.score}", fontWeight = FontWeight.Bold)
            if (state.streak > 0) {
                Text("🔥 ${state.streak}", color = NeonSlateGold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(q.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        q.options.forEachIndexed { index, option ->
            val isSelected = state.selectedAnswer == index
            Button(
                onClick = { onSelect(index) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                enabled = !state.hasAnswered
            ) {
                Text(
                    "${('A' + index)}. $option",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (!state.hasAnswered) {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = state.selectedAnswer >= 0
            ) {
                Text("KONFIRMASI", fontWeight = FontWeight.Bold)
            }
        } else {
            Text(
                "Menunggu soal berikutnya...",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hadiah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("EXP: +${state.expEarned}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Koin: +${state.coinsEarned}", fontWeight = FontWeight.Bold, color = NeonSlateGold)
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("LATIHAN LAGI", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("KEMBALI")
        }
    }
}
