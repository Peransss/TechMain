package com.example.techmain.ui.flashcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.data.db.entity.Flashcard
import com.example.techmain.data.db.entity.FlashcardDeck

@Composable
fun FlashcardScreen(viewModel: FlashcardViewModel = viewModel()) {
    val decks by viewModel.decks.collectAsState(initial = emptyList())
    val selectedDeckId by viewModel.selectedDeckId.collectAsState()
    val studyMode by viewModel.studyMode.collectAsState()
    val showAddDeckDialog by viewModel.showAddDeckDialog.collectAsState()
    val showAddCardDialog by viewModel.showAddCardDialog.collectAsState()
    val deckTitle by viewModel.deckTitle.collectAsState()
    val question by viewModel.question.collectAsState()
    val answer by viewModel.answer.collectAsState()

    if (selectedDeckId != null && !studyMode) {
        DeckDetailScreen(viewModel = viewModel)
    } else if (studyMode) {
        StudyScreen(viewModel = viewModel)
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { viewModel.showAddDeckDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Dek")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Flashcard Deck", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                if (decks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada deck. Buat deck baru!")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(decks, key = { it.id }) { deck ->
                            DeckCard(deck = deck, onClick = { viewModel.selectDeck(deck.id) }, onDelete = { viewModel.deleteDeck(deck) })
                        }
                    }
                }
            }
        }

        if (showAddDeckDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideAddDeckDialog() },
                title = { Text("Deck Baru") },
                text = {
                    OutlinedTextField(
                        value = deckTitle,
                        onValueChange = { viewModel.onDeckTitleChange(it) },
                        label = { Text("Nama Deck") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = { viewModel.addDeck() }) { Text("Buat") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideAddDeckDialog() }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun DeckCard(deck: FlashcardDeck, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deck.title, style = MaterialTheme.typography.titleMedium)
                if (deck.description.isNotEmpty()) {
                    Text(deck.description, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(viewModel: FlashcardViewModel) {
    val flashcards by viewModel.getFlashcards().collectAsState(initial = emptyList())
    val showAddCardDialog by viewModel.showAddCardDialog.collectAsState()
    val question by viewModel.question.collectAsState()
    val answer by viewModel.answer.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddCardDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kartu")
            }
        },
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Detail Deck") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.clearSelection() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (flashcards.isNotEmpty()) {
                        IconButton(onClick = { viewModel.startStudy(flashcards) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Belajar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (flashcards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada kartu. Tambah kartu baru!")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(flashcards, key = { it.id }) { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Q: ${card.question}", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("A: ${card.answer}")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCardDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddCardDialog() },
            title = { Text("Kartu Baru") },
            text = {
                Column {
                    OutlinedTextField(
                        value = question, onValueChange = { viewModel.onQuestionChange(it) },
                        label = { Text("Pertanyaan") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = answer, onValueChange = { viewModel.onAnswerChange(it) },
                        label = { Text("Jawaban") }, modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.addCard() }) { Text("Tambah") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideAddCardDialog() }) { Text("Batal") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(viewModel: FlashcardViewModel) {
    val currentCard by viewModel.currentCardIndex.collectAsState()
    val showAnswer by viewModel.showAnswer.collectAsState()
    val card = viewModel.getCurrentCard()

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Belajar - ${viewModel.getStudyProgress()}") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.stopStudy() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Keluar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (card != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pertanyaan", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(card.question, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            AnimatedVisibility(visible = showAnswer, enter = fadeIn(), exit = fadeOut()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Jawaban", style = MaterialTheme.typography.labelSmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(card.answer, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilledTonalButton(onClick = { viewModel.prevCard() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Sebelumnya")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sebelumnya")
                    }
                    Button(onClick = { viewModel.flipCard() }) {
                        Icon(Icons.Default.FlipToBack, contentDescription = "Balik")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAnswer) "Tutup" else "Lihat")
                    }
                    FilledTonalButton(onClick = { viewModel.nextCard() }) {
                        Text("Selanjutnya")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Selanjutnya")
                    }
                }
            } else {
                Text("Tidak ada kartu untuk dipelajari")
                Button(onClick = { viewModel.stopStudy() }) { Text("Kembali") }
            }
        }
    }
}
