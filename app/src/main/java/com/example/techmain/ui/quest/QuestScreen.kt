package com.example.techmain.ui.quest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.data.db.entity.Quest

@Composable
fun QuestScreen(viewModel: QuestViewModel = viewModel()) {
    val quests by viewModel.quests.collectAsState(initial = emptyList())
    val showDialog by viewModel.showAddDialog.collectAsState()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Quest")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Quest Board", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))

            if (quests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada quest. Tambah quest pertama kamu!", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quests, key = { it.id }) { quest ->
                        QuestCard(quest = quest, onToggle = { viewModel.toggleQuestCompletion(quest) }, onDelete = { viewModel.deleteQuest(quest) })
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideAddDialog() },
                title = { Text("Quest Baru") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { viewModel.onTitleChange(it) },
                            label = { Text("Nama Quest") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { viewModel.onDescriptionChange(it) },
                            label = { Text("Deskripsi (opsional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.addQuest() }) { Text("Tambah") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideAddDialog() }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun QuestCard(quest: Quest, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (quest.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = quest.isCompleted, onCheckedChange = { onToggle() })
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (quest.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    fontWeight = if (quest.isCompleted) FontWeight.Normal else FontWeight.Bold
                )
                if (quest.description.isNotEmpty()) {
                    Text(text = quest.description, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(" +${quest.xpReward} XP", style = MaterialTheme.typography.labelSmall)
                    Text("  +${quest.goldReward} Gold", style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus")
            }
        }
    }
}
