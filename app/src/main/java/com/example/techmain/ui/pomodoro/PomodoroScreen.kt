package com.example.techmain.ui.pomodoro

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.ui.theme.RPGHpRed
import com.example.techmain.ui.theme.RpgManaBlue

@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val timerDisplay by viewModel.timerDisplay.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val sessionCount by viewModel.sessionCount.collectAsState()
    val monstersDefeated by viewModel.monstersDefeated.collectAsState()
    val totalSessions by viewModel.totalFocusSessions.collectAsState(initial = 0)
    val totalMinutes by viewModel.totalFocusMinutes.collectAsState(initial = 0)
    val totalMonsters by viewModel.totalMonstersDefeated.collectAsState(initial = 0)

    val timerColor by animateColorAsState(
        targetValue = when (state) {
            PomodoroState.FOCUS -> RPGHpRed
            PomodoroState.BREAK -> RpgManaBlue
            PomodoroState.IDLE -> MaterialTheme.colorScheme.primary
        }, label = "timerColor"
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Pomodoro Dungeon", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                Canvas(modifier = Modifier.size(250.dp)) {
                    val strokeWidth = 20f
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = timerColor,
                        startAngle = -90f,
                        sweepAngle = 360f * (1f - progress),
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timerDisplay,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = timerColor
                    )
                    Text(
                        text = when (state) {
                            PomodoroState.IDLE -> "Siap Fokus"
                            PomodoroState.FOCUS -> "Belajar..."
                            PomodoroState.BREAK -> "Istirahat"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                when (state) {
                    PomodoroState.IDLE -> {
                        Button(onClick = { viewModel.startFocus() }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mulai Fokus")
                        }
                    }
                    PomodoroState.FOCUS -> {
                        FilledTonalButton(onClick = { viewModel.skipToBreak() }) {
                            Icon(Icons.Default.SkipNext, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Selesai")
                        }
                        Button(onClick = { viewModel.reset() }) {
                            Icon(Icons.Default.Replay, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset")
                        }
                    }
                    PomodoroState.BREAK -> {
                        Button(onClick = { viewModel.startFocus() }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mulai Lagi")
                        }
                        FilledTonalButton(onClick = { viewModel.reset() }) {
                            Icon(Icons.Default.Replay, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard("Sesi", "${sessionCount}", RpgManaBlue)
                StatCard("Monster", "${monstersDefeated}", RPGHpRed)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Total Keseluruhan", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard("Total Sesi", "$totalSessions", RpgManaBlue)
                StatCard("Total Menit", "$totalMinutes", MaterialTheme.colorScheme.primary)
                StatCard("Monster", "$totalMonsters", RPGHpRed)
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        modifier = Modifier.width(100.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
