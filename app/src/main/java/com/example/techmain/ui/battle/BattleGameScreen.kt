package com.example.techmain.ui.battle

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.techmain.firebase.CustomQuestion
import com.example.techmain.ui.components.AnswerButton
import com.example.techmain.ui.components.GlassCard
import com.example.techmain.ui.components.NeonButton
import com.example.techmain.ui.theme.CyberAccent
import com.example.techmain.ui.theme.CyberBackground
import com.example.techmain.ui.theme.CyberGold
import com.example.techmain.ui.theme.CyberPrimary
import com.example.techmain.ui.theme.CyberSecondary
import com.example.techmain.ui.theme.CyberSurfaceBorder
import com.example.techmain.ui.theme.CyberTextPrimary
import com.example.techmain.ui.theme.CyberTextSecondary
import com.example.techmain.ui.theme.NeonSlatePrimary

@Composable
fun BattleGameScreen(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()
    val game = state.game
    val myId = state.myUserId
    val me = game.players[myId]
    val currentQuestion = game.questions.getOrNull(game.currentRound)
    val opponent = game.players.filterKeys { it != myId }.values.firstOrNull()
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler { showExitDialog = true }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeonButton(
                onClick = { showExitDialog = true },
                isPrimary = false,
                isOutlined = true
            ) {
                Text("Exit")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

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
            targetValue = if (state.timeLeft > 5) CyberPrimary else CyberAccent,
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
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = game.mode.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                val imageUrl = currentQuestion.imageUrl
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(200.dp).border(2.dp, CyberPrimary)
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
            }
            Spacer(modifier = Modifier.height(16.dp))

            currentQuestion.options.forEachIndexed { index, option ->
                val isEliminated = state.eliminatedOptions.contains(index)
                val isSelected = state.selectedAnswer == index
                val isCorrectAnswer = currentQuestion.correctAnswer == index && state.hasAnswered
                val isWrongSelection = isSelected && currentQuestion.correctAnswer != index

                AnswerButton(
                    text = if (isEliminated) "" else "${('A' + index)}. $option",
                    onClick = { viewModel.selectAnswer(index) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    enabled = !state.hasAnswered && !isEliminated,
                    isSelected = isSelected,
                    isCorrect = isCorrectAnswer,
                    isWrong = isWrongSelection
                )
            }

            if (game.mode == "powerup" && !state.hasAnswered) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PowerUpButton("50:50", onClick = { viewModel.usePowerUp("fiftyFifty") }, isAvailable = me?.powerUps?.get("fiftyFifty") == true)
                    PowerUpButton("2x Poin", onClick = { viewModel.usePowerUp("doublePoints") }, isAvailable = me?.powerUps?.get("doublePoints") == true)
                    PowerUpButton("Freeze", onClick = { viewModel.usePowerUp("timeFreeze") }, isAvailable = me?.powerUps?.get("timeFreeze") == true)
                }
            }

            if (state.doublePointsActive) {
                Text(
                    "Double Points Aktif!",
                    color = CyberGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!state.hasAnswered) {
                NeonButton(
                    onClick = { viewModel.submitAnswer() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = state.selectedAnswer >= 0,
                    isPrimary = true
                ) {
                    Text("KONFIRMASI", fontWeight = FontWeight.Bold)
                }
    } else {
                Text(
                    text = "Menunggu lawan...",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showExitDialog) {
        Dialog(onDismissRequest = { showExitDialog = false }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                border = BorderStroke(1.dp, CyberAccent.copy(alpha = 0.5f))
            ) {
                Text("Keluar Game?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Apakah kamu yakin ingin keluar?", color = Color.White.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(24.dp))
                NeonButton(onClick = { showExitDialog = false; viewModel.leaveRoom() }, modifier = Modifier.fillMaxWidth().height(48.dp), isPrimary = true) {
                    Text("YA, KELUAR", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                NeonButton(onClick = { showExitDialog = false }, modifier = Modifier.fillMaxWidth().height(48.dp), isPrimary = false, isOutlined = true) {
                    Text("BATAL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PlayerScoreCard(modifier: Modifier = Modifier, name: String, score: Int, isMe: Boolean) {
    GlassCard(
        modifier = modifier,
        containerColor = if (isMe) CyberPrimary.copy(alpha = 0.2f) else CyberBackground,
        border = BorderStroke(1.dp, if (isMe) CyberPrimary else CyberSurfaceBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(if (isMe) CyberPrimary else CyberSecondary),
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

@Composable
fun PowerUpButton(label: String, onClick: () -> Unit, isAvailable: Boolean) {
    Button(
        onClick = onClick,
        enabled = isAvailable,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberSecondary,
            contentColor = Color.White
        )
    ) {
        Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
    }
}
