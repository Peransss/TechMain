package com.example.techmain.ui.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.ui.components.GlassCard
import com.example.techmain.ui.theme.CyberPrimary
import com.example.techmain.ui.theme.CyberSecondary
import com.example.techmain.ui.theme.CyberAccent
import com.example.techmain.ui.theme.CyberBackground
import com.example.techmain.ui.theme.CyberSurfaceBorder
import com.example.techmain.ui.theme.CyberTextPrimary
import com.example.techmain.ui.theme.CyberTextSecondary
import com.example.techmain.ui.theme.CyberSuccess
import com.example.techmain.ui.theme.CyberGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Papan Peringkat") }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyberPrimary)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
            ) {
                state.myStats?.let { me ->
                    MyStatsCard(me)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ranking", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { viewModel.toggleViewMode() }) {
                            Icon(
                                imageVector = if (state.viewMode == LeaderboardViewMode.LIST)
                                    Icons.Default.ViewList else Icons.Default.ViewModule,
                                contentDescription = "Toggle view",
                                tint = if (state.viewMode == LeaderboardViewMode.LIST) CyberPrimary else CyberTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.viewMode == LeaderboardViewMode.LIST) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(state.entries) { index, entry ->
                            LeaderboardRow(index + 1, entry)
                        }
                    }
                } else {
                    LeaderboardTable(
                        entries = state.entries,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MyStatsCard(entry: LeaderboardEntry) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = CyberPrimary.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f))
    ) {
        Text("Statistik Kamu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem("Rating", "${entry.rating}")
            StatItem("Menang", "${entry.wins}")
            StatItem("Kalah", "${entry.losses}")
            StatItem("Akurasi", if (entry.totalAnswers > 0) "${(entry.correctAnswers * 100 / entry.totalAnswers)}%" else "0%")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun LeaderboardRow(rank: Int, entry: LeaderboardEntry) {
    val borderColor = when (rank) {
        1 -> CyberGold
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> CyberSurfaceBorder
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = if (entry.isMe) CyberPrimary.copy(alpha = 0.15f) else CyberBackground,
        border = BorderStroke(1.dp, if (entry.isMe) CyberPrimary else borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rank <= 3) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = when (rank) { 1 -> CyberGold; 2 -> Color(0xFFC0C0C0); else -> Color(0xFFCD7F32) },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            } else {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.Center,
                    color = CyberTextSecondary
                )
            }

            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(if (entry.isMe) CyberPrimary else CyberSecondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.displayName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (entry.isMe) FontWeight.Bold else FontWeight.Normal
                )
                Text("${entry.wins}W - ${entry.losses}L", style = MaterialTheme.typography.bodySmall, color = CyberTextSecondary)
            }
            Text(
                text = "${entry.rating}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CyberGold
            )
        }
    }
}

@Composable
fun LeaderboardTable(entries: List<LeaderboardEntry>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = CyberPrimary.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, CyberPrimary),
                contentPadding = PaddingValues(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#", modifier = Modifier.width(32.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = CyberTextPrimary)
                    Text("Nama", modifier = Modifier.weight(1f).padding(start = 8.dp), fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                    Text("Rating", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = CyberTextPrimary)
                    Text("W", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = CyberTextPrimary)
                    Text("L", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = CyberTextPrimary)
                    Text("Akurasi", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = CyberTextPrimary)
                }
            }
        }

        itemsIndexed(entries) { index, entry ->
            TableRow(rank = index + 1, entry = entry)
        }
    }
}

@Composable
fun TableRow(rank: Int, entry: LeaderboardEntry) {
    val accuracy = if (entry.totalAnswers > 0) "${(entry.correctAnswers * 100 / entry.totalAnswers)}%" else "0%"
    val borderColor = when (rank) {
        1 -> CyberGold
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> CyberSurfaceBorder
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = if (entry.isMe) CyberPrimary.copy(alpha = 0.15f) else CyberBackground,
        border = BorderStroke(1.dp, if (entry.isMe) CyberPrimary else borderColor),
        contentPadding = PaddingValues(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rank <= 3) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = when (rank) { 1 -> CyberGold; 2 -> Color(0xFFC0C0C0); else -> Color(0xFFCD7F32) },
                    modifier = Modifier.size(20.dp).padding(start = 6.dp)
                )
            } else {
                Text(
                    "$rank",
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center,
                    color = CyberTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                entry.displayName,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                fontWeight = if (entry.isMe) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium,
                color = if (entry.isMe) CyberPrimary else CyberTextPrimary
            )

            Text(
                "${entry.rating}",
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = CyberGold
            )

            Text(
                "${entry.wins}",
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Center,
                color = CyberSuccess,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "${entry.losses}",
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Center,
                color = CyberAccent,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                accuracy,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center,
                color = CyberTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
