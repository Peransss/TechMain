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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import com.example.techmain.firebase.CustomQuiz
import com.example.techmain.ui.components.GlassCard
import com.example.techmain.ui.components.NeonButton
import com.example.techmain.ui.theme.NeonSlateBackground
import com.example.techmain.ui.theme.NeonSlateSurfaceBorder
import com.example.techmain.ui.theme.NeonSlatePrimary
import com.example.techmain.ui.theme.NeonSlateSecondary
import com.example.techmain.ui.theme.GlassWhiteHigh

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
        BattleScreen.SOLO_PRACTICE -> {
            Text("Loading...")
        }
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

        
        val featuredQuizzes by viewModel.featuredQuizzes.collectAsState()
        if (featuredQuizzes.isNotEmpty()) {
            Text("Featured Quizzes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = NeonSlatePrimary)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().height(140.dp)
            ) {
                items(featuredQuizzes) { quiz ->
                    val isSelected = state.selectedCategory == quiz.id
                    GlassCard(
                        onClick = { viewModel.selectCategory(quiz.id) },
                        modifier = Modifier.size(width = 200.dp, height = 120.dp),
                        border = BorderStroke(2.dp, if (isSelected) Color.White else NeonSlatePrimary.copy(alpha = 0.5f)),
                        containerColor = if (isSelected) NeonSlatePrimary.copy(alpha = 0.2f) else GlassWhiteHigh.copy(alpha = 0.1f)
                    ) {
                        Text(quiz.title, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("${quiz.questions.size} Questions", color = NeonSlatePrimary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

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
                GlassCard(
                    onClick = { viewModel.selectCategory(category.id) },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(2.dp, if (isSelected) NeonSlatePrimary else Color.Transparent),
                    containerColor = if (isSelected) NeonSlatePrimary.copy(alpha = 0.1f) else GlassWhiteHigh.copy(alpha = 0.05f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (isSelected) NeonSlatePrimary else Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeonButton(
            onClick = { viewModel.createRoom() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = state.selectedCategory.isNotEmpty(),
            isPrimary = true
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("BUAT ROOM (Multiplayer)", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        NeonButton(
            onClick = { viewModel.showDifficultyDialog() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = state.selectedCategory.isNotEmpty(),
            isPrimary = false
        ) {
            Text("\uD83E\uDD16 VS BOT", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        NeonButton(
            onClick = { viewModel.showSoloSetup() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = state.selectedCategory.isNotEmpty(),
            isPrimary = false,
            isOutlined = true
        ) {
            Text("\uD83C\uDFAF LATIHAN (Solo)", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        NeonButton(
            onClick = { viewModel.showJoinRoom() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            isPrimary = true,
            isOutlined = true
        ) {
            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("GABUNG ROOM", fontWeight = FontWeight.Bold)
        }
    }

    if (state.showDifficultyDialog) {
        DifficultyDialog(viewModel = viewModel)
    }

    if (state.showModePicker) {
        ModePickerDialog(viewModel = viewModel)
    }

    if (state.showSoloSetup) {
        AlertDialog(
            onDismissRequest = { viewModel.hideSoloSetup() },
            title = { Text("Latihan Solo", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Pilih jumlah soal:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    listOf(5, 10).forEach { rounds ->
                        val isSelected = state.soloRounds == rounds
                        TextButton(
                            onClick = { viewModel.setSoloRounds(rounds) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Text(
                                "$rounds Soal",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.startSoloPractice() }) {
                    Text("MULAI", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { viewModel.hideSoloSetup() }) { Text("BATAL") } }
        )
    }
}

@Composable
fun ModePickerDialog(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()
    AlertDialog(
        onDismissRequest = { viewModel.hideModePicker() },
        title = { Text("Pilih Mode", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                val modes = listOf(
                    Triple("casual", "Casual", "Standar 5 soal \u00B7 20 detik"),
                    Triple("blitz", "Blitz", "Cepat 5 soal \u00B7 10 detik \u00B7 150 pts"),
                    Triple("marathon", "Marathon", "10 soal \u00B7 15 detik \u00B7 50 pts"),
                    Triple("powerup", "Power-Up", "5 soal + 3 power-ups (50:50, 2x, Freeze)")
                )
                modes.forEach { (modeId, title, desc) ->
                    val isSelected = state.selectedMode == modeId
                    TextButton(
                        onClick = { viewModel.setMode(modeId) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(desc, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { viewModel.hideModePicker() }) { Text("BATAL") } }
    )
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

    androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.hideDifficultyDialog() }) {
        GlassCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            border = BorderStroke(1.dp, NeonSlatePrimary.copy(alpha = 0.5f))
        ) {
            Text(
                "Pilih Kesulitan Bot",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                BotDifficulty.entries.forEach { difficulty ->
                    val isSelected = state.difficulty == difficulty
                    GlassCard(
                        onClick = {
                            viewModel.setDifficulty(difficulty)
                            viewModel.hideDifficultyDialog()
                            viewModel.startVsBot()
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        containerColor = if (isSelected) NeonSlatePrimary.copy(alpha = 0.2f) else GlassWhiteHigh.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, if (isSelected) NeonSlatePrimary else Color.Transparent)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                when (difficulty) {
                                    BotDifficulty.EASY -> "\uD83C\uDF31 Easy"
                                    BotDifficulty.MEDIUM -> "\u26A1 Medium"
                                    BotDifficulty.HARD -> "\uD83D\uDD25 Hard"
                                },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) NeonSlatePrimary else Color.White
                            )
                            Text(
                                when (difficulty) {
                                    BotDifficulty.EASY -> "Bot menjawab random (~25% benar)"
                                    BotDifficulty.MEDIUM -> "Bot menjawab cukup baik (~50% benar)"
                                    BotDifficulty.HARD -> "Bot sangat pintar (~80% benar)"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(
                onClick = { viewModel.hideDifficultyDialog() },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false,
                isOutlined = true
            ) {
                Text("BATAL", fontWeight = FontWeight.Bold)
            }
        }
    }
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
