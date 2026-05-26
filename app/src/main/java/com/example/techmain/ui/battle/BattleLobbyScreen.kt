package com.example.techmain.ui.battle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.techmain.firebase.CustomQuiz
import com.example.techmain.firebase.QuestionBank
import com.example.techmain.game.BotDifficulty
import com.example.techmain.ui.components.GlassCard
import com.example.techmain.ui.components.NeonButton
import com.example.techmain.ui.theme.CyberAccent
import com.example.techmain.ui.theme.CyberBackground
import com.example.techmain.ui.theme.CyberPrimary
import com.example.techmain.ui.theme.CyberSecondary
import com.example.techmain.ui.theme.CyberSurfaceBorder
import com.example.techmain.ui.theme.CyberTextPrimary
import com.example.techmain.ui.theme.CyberTextSecondary

import androidx.activity.compose.BackHandler

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
            Text("Featured Quizzes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CyberPrimary)
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
                        border = BorderStroke(2.dp, if (isSelected) Color.White else CyberPrimary.copy(alpha = 0.5f)),
                        containerColor = if (isSelected) CyberPrimary.copy(alpha = 0.2f) else CyberSurfaceBorder.copy(alpha = 0.1f)
                    ) {
                        Text(quiz.title, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("${quiz.questions.size} Questions", color = CyberPrimary, style = MaterialTheme.typography.bodySmall)
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
                    border = BorderStroke(2.dp, if (isSelected) CyberPrimary else Color.Transparent),
                    containerColor = if (isSelected) CyberPrimary.copy(alpha = 0.1f) else CyberBackground
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (category.icon) {
                            "calculate" -> Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.White)
                            "biotech" -> Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.White)
                            "history" -> Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.White)
                            "public" -> Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.White)
                            "translate" -> Text("\uD83D\uDD0A", style = MaterialTheme.typography.headlineLarge)
                            else -> Text("\uD83D\uDCA1", style = MaterialTheme.typography.headlineLarge)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (isSelected) CyberPrimary else Color.White
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
        Dialog(onDismissRequest = { viewModel.hideSoloSetup() }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                border = BorderStroke(1.dp, CyberSecondary.copy(alpha = 0.5f))
            ) {
                Text("Latihan Solo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Text("Pilih jumlah soal:", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.height(8.dp))
                    listOf(5, 10).forEach { rounds ->
                        val isSelected = state.soloRounds == rounds
                        GlassCard(
                            onClick = { viewModel.setSoloRounds(rounds) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            containerColor = if (isSelected) CyberSecondary.copy(alpha = 0.2f) else CyberBackground,
                            border = BorderStroke(1.dp, if (isSelected) CyberSecondary else Color.Transparent),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Text(
                                "$rounds Soal",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyberSecondary else Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                NeonButton(onClick = { viewModel.startSoloPractice() }, modifier = Modifier.fillMaxWidth().height(52.dp), isPrimary = true) {
                    Text("MULAI", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                NeonButton(onClick = { viewModel.hideSoloSetup() }, modifier = Modifier.fillMaxWidth(), isPrimary = false, isOutlined = true) {
                    Text("BATAL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ModePickerDialog(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()

    Dialog(onDismissRequest = { viewModel.hideModePicker() }) {
        GlassCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            border = BorderStroke(1.dp, CyberSecondary.copy(alpha = 0.5f))
        ) {
            Text("Pilih Mode", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                val modes = listOf(
                    Triple("casual", "Casual", "Standar 5 soal \u00B7 20 detik"),
                    Triple("blitz", "Blitz", "Cepat 5 soal \u00B7 10 detik \u00B7 150 pts"),
                    Triple("marathon", "Marathon", "10 soal \u00B7 15 detik \u00B7 50 pts"),
                    Triple("powerup", "Power-Up", "5 soal + 3 power-ups (50:50, 2x, Freeze)")
                )
                modes.forEach { (modeId, title, desc) ->
                    val isSelected = state.selectedMode == modeId
                    GlassCard(
                        onClick = { viewModel.setMode(modeId) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        containerColor = if (isSelected) CyberSecondary.copy(alpha = 0.2f) else CyberBackground,
                        border = BorderStroke(1.dp, if (isSelected) CyberSecondary else Color.Transparent)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) CyberSecondary else Color.White
                            )
                            Text(
                                desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(
                onClick = { viewModel.hideModePicker() },
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
fun JoinRoomContent(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f))
        ) {
            Text(
                "Gabung Room",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = state.joinCode,
                onValueChange = { viewModel.onJoinCodeChange(it) },
                label = { Text("Kode Room") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center, color = Color.White),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(
                onClick = { viewModel.joinRoom() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = state.joinCode.length == 6,
                isPrimary = true
            ) {
                Text("GABUNG", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            NeonButton(
                onClick = { viewModel.playAgain() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                isPrimary = false,
                isOutlined = true
            ) {
                Text("KEMBALI")
            }
        }
    }
}

@Composable
fun DifficultyDialog(viewModel: BattleViewModel) {
    val state by viewModel.state.collectAsState()

    Dialog(onDismissRequest = { viewModel.hideDifficultyDialog() }) {
        GlassCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f))
        ) {
            Text("Pilih Kesulitan Bot", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
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
                        containerColor = if (isSelected) CyberPrimary.copy(alpha = 0.2f) else CyberBackground,
                        border = BorderStroke(1.dp, if (isSelected) CyberPrimary else Color.Transparent)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                when (difficulty) {
                                    BotDifficulty.EASY -> "Easy"
                                    BotDifficulty.MEDIUM -> "Medium"
                                    BotDifficulty.HARD -> "Hard"
                                },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) CyberPrimary else Color.White
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

    BackHandler { viewModel.leaveRoom() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Room Siap!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f)),
            containerColor = CyberBackground
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
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                containerColor = when {
                    isHostPlayer -> CyberAccent.copy(alpha = 0.2f)
                    player.userId == state.myUserId -> CyberPrimary.copy(alpha = 0.2f)
                    else -> CyberBackground
                },
                border = BorderStroke(1.dp, when {
                    isHostPlayer -> CyberAccent
                    player.userId == state.myUserId -> CyberPrimary
                    else -> CyberSurfaceBorder
                })
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(player.displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    if (isHostPlayer) {
                        SuggestionChip(onClick = {}, label = { Text("HOST") })
                    } else {
                        Text("Siap")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        if (isHost) {
            NeonButton(
                onClick = { viewModel.startGame() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = room.players.size >= 2,
                isPrimary = true
            ) {
                Text("MULAI GAME", fontWeight = FontWeight.Bold)
            }
            if (room.players.size < 2) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tunggu pemain lain bergabung...", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            NeonButton(
                onClick = { viewModel.leaveRoom() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                isPrimary = false,
                isOutlined = true
            ) {
                Text("BATALKAN ROOM", fontWeight = FontWeight.Bold)
            }
        } else {
            NeonButton(
                onClick = { viewModel.leaveRoom() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                isPrimary = false,
                isOutlined = true
            ) {
                Text("KELUAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}
