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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ... (Header/Timer UI remains same)

        if (currentQuestion != null) {
            val imageUrl = (currentQuestion as? CustomQuestion)?.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp).border(2.dp, NeonHackerPrimary)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            val questionText = (currentQuestion as? CustomQuestion)?.question ?: (currentQuestion as? com.example.techmain.firebase.QuizQuestion)?.question ?: ""
            Text(
                text = questionText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            val options = (currentQuestion as? CustomQuestion)?.options ?: (currentQuestion as? com.example.techmain.firebase.QuizQuestion)?.options ?: emptyList()
            options.forEachIndexed { index, option ->
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
