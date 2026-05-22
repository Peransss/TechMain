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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.Login

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.techmain.firebase.QuestionBank
import com.example.techmain.game.BotDifficulty

@Composable
fun BattleLobbyScreen(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()

    when (state.screen) {
        BattleScreen.LOBBY -> LobbyContent(viewModel = viewModel)
        BattleScreen.JOIN_ROOM -> JoinRoomContent(viewModel = viewModel)
        BattleScreen.WAITING_ROOM -> WaitingRoomScreen(viewModel = viewModel)
        BattleScreen.GAME -> BattleGameScreen(viewModel = viewModel)
        BattleScreen.RESULT -> BattleResultScreen(viewModel = viewModel)
    }
}

@Composable
fun LobbyContent(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Quiz Battle", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Battle real-time dengan pemain lain!", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Pilih Kategori", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        val categories = QuestionBank.categories
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categories) { category ->
                val isSelected = state.selectedCategory == category.id
                Card(
                    onClick = { viewModel.selectCategory(category.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (category.icon) {
                                "calculate" -> "\uD83D\uDD22"
                                "biotech" -> "\uD83D\uDD2C"
                                "history" -> "\uD83D\uDCD6"
                                "public" -> "\uD83C\uDF0D"
                                "translate" -> "\uD83D\uDD0A"
                                else -> "\uD83D\uDCA1"
                            },
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.createRoom() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = state.selectedCategory.isNotEmpty(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("BUAT ROOM (Multiplayer)", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.showDifficultyDialog() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = state.selectedCategory.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("\uD83E\uDD16 VS BOT", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        FilledTonalButton(
            onClick = { viewModel.showJoinRoom() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("GABUNG ROOM", fontWeight = FontWeight.Bold)
        }
    }

    if (state.showDifficultyDialog) {
        DifficultyDialog(viewModel = viewModel)
    }
}

@Composable
fun JoinRoomContent(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Gabung Room", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = state.joinCode,
            onValueChange = { viewModel.onJoinCodeChange(it) },
            label = { Text("Kode Room") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.joinRoom() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = state.joinCode.length == 6,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("GABUNG", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        FilledTonalButton(
            onClick = { viewModel.playAgain() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("KEMBALI")
        }
    }
}

@Composable
fun DifficultyDialog(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()

    AlertDialog(
        onDismissRequest = { viewModel.hideDifficultyDialog() },
        title = {
            Text("Pilih Kesulitan Bot", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                BotDifficulty.entries.forEach { difficulty ->
                    val isSelected = state.difficulty == difficulty
                    TextButton(
                        onClick = {
                            viewModel.setDifficulty(difficulty)
                            viewModel.hideDifficultyDialog()
                            viewModel.startVsBot()
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                when (difficulty) {
                                    BotDifficulty.EASY -> "\uD83C\uDF31 Easy"
                                    BotDifficulty.MEDIUM -> "\u26A1 Medium"
                                    BotDifficulty.HARD -> "\uD83D\uDD25 Hard"
                                },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                when (difficulty) {
                                    BotDifficulty.EASY -> "Bot menjawab random (~25% benar)"
                                    BotDifficulty.MEDIUM -> "Bot menjawab cukup baik (~50% benar)"
                                    BotDifficulty.HARD -> "Bot sangat pintar (~80% benar)"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.hideDifficultyDialog() }) {
                Text("BATAL")
            }
        }
    )
}

@Composable
fun WaitingRoomScreen(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()
    val room = state.room
    val isHost = room.hostId == state.myUserId

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Room Siap!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Kode Room", style = MaterialTheme.typography.labelMedium)
                Text(state.roomCode, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Bagikan kode ini ke temanmu", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Pemain (${room.players.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        val sortedPlayers = room.players.entries.sortedBy { it.key != room.hostId }
        sortedPlayers.forEach { (_, player) ->
            val isHostPlayer = player.userId == room.hostId
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isHostPlayer -> MaterialTheme.colorScheme.tertiaryContainer
                        player.userId == state.myUserId -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(player.displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    if (isHostPlayer) {
                        androidx.compose.material3.SuggestionChip(
                            onClick = {},
                            label = { Text("HOST") }
                        )
                    } else {
                        Text("\u2705 Siap")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = if (isHost) ({ viewModel.startGame() }) else ({ viewModel.leaveRoom() }),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = if (isHost) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
        ) {
            Text(
                if (isHost) "MULAI GAME" else "KELUAR",
                fontWeight = FontWeight.Bold
            )
        }
        if (isHost && room.players.size < 2) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tunggu pemain lain bergabung...", style = MaterialTheme.typography.bodySmall)
        }
    }
}
