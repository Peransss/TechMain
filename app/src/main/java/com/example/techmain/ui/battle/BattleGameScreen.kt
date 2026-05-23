package com.example.techmain.ui.battle

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.techmain.firebase.CustomQuestion
import com.example.techmain.ui.theme.NeonHackerPrimary

@Composable
fun BattleGameScreen(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()
    val game = state.game
    val myId = state.myUserId
    val me = game.players[myId]
    val currentQuestion = game.questions.getOrNull(game.currentRound)
    val opponent = game.players.filterKeys { it != myId }.values.firstOrNull()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerScoreCard(modifier = Modifier.weight(1f), name = me?.displayName ?: "Kamu", score = me?.score ?: 0, isMe = true)
            Text(
                text = "VS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            PlayerScoreCard(modifier = Modifier.weight(1f), name = opponent?.displayName ?: "???", score = opponent?.score ?: 0, isMe = false)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Round ${game.currentRound + 1} / ${game.totalRounds}",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        val timerProgress = state.timeLeft.toFloat() / game.roundTimeLimit.toFloat()
        val timerColor by animateColorAsState(
            targetValue = if (state.timeLeft > 5) MaterialTheme.colorScheme.primary else Color.Red,
            label = "timerColor"
        )
        LinearProgressIndicator(
            progress = { timerProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = timerColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "${state.timeLeft}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = timerColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (currentQuestion != null) {
            val imageUrl = currentQuestion.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp).border(2.dp, NeonHackerPrimary)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = state.selectedAnswer == index
                Button(
                    onClick = { viewModel.selectAnswer(index) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.hasAnswered
                ) {
                    Text(text = "${('A' + index)}. $option")
                }
            }
        }
    }
}

@Composable
fun PlayerScoreCard(modifier: Modifier = Modifier, name: String, score: Int, isMe: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isMe)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = name, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            Text(text = "$score", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
