package com.example.techmain.ui.battle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techmain.ui.components.GlassCard
import com.example.techmain.ui.components.NeonButton
import com.example.techmain.ui.theme.NeonSlateGold

@Composable
fun BattleResultScreen(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()
    val game = state.game
    val myId = state.myUserId
    val me = game.players[myId]
    val opponent = game.players.filterKeys { it != myId }.values.firstOrNull()
    val isWinner = game.winnerId == myId

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Winner badge with glow effect
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(96.dp).graphicsLayer {
                if (isWinner) {
                    shadowElevation = 20f
                    shape = androidx.compose.foundation.shape.CircleShape
                    clip = true
                }
            },
            tint = if (isWinner) NeonSlateGold else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isWinner) "KAMU MENANG!" else "KALAH",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = if (isWinner) NeonSlateGold else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Result container as GlassCard
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Skor Kamu", "${me?.score ?: 0}", "${me?.correctCount ?: 0}/${me?.totalAnswered ?: 0}")
                StatCard("Lawan", "${opponent?.score ?: 0}", "${opponent?.correctCount ?: 0}/${opponent?.totalAnswered ?: 0}")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        NeonButton(
            onClick = { viewModel.playAgain() },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("MAIN LAGI", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatCard(title: String, score: String, accuracy: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(score, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Benar: $accuracy", style = MaterialTheme.typography.bodySmall)
    }
}
