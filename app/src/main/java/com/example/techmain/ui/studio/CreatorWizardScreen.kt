package com.example.techmain.ui.studio

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.firebase.CustomQuestion
import com.example.techmain.firebase.CustomQuiz
import java.util.UUID

@Composable
fun CreatorWizardScreen(viewModel: CreatorViewModel = viewModel(), onBack: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    var quizTitle by remember { mutableStateOf("") }
    var questions by remember { mutableStateOf(listOf<CustomQuestion>()) }
    val images = remember { mutableStateMapOf<String, Uri>() }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        AnimatedContent(targetState = step, label = "wizard") { targetStep ->
            when(targetStep) {
                0 -> MetadataStep(quizTitle, { quizTitle = it }, { step++ })
                1 -> QuestionStep(questions, { questions = it }, { step++ }, { step-- })
                2 -> MediaStep(
                    questions, 
                    images, 
                    onPublish = { 
                        viewModel.saveQuiz(CustomQuiz(title = quizTitle, questions = questions), images) 
                    }, 
                    onBack = { step-- }
                )
            }
        }
        
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun MetadataStep(title: String, onTitleChange: (String) -> Unit, onNext: () -> Unit) {
    Column {
        Text("Langkah 1: Informasi Dasar", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title, 
            onValueChange = onTitleChange, 
            label = { Text("Judul Kuis") }, 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNext, 
            enabled = title.isNotEmpty(), 
            modifier = Modifier.fillMaxWidth()
        ) { Text("Lanjut") }
    }
}

@Composable
fun QuestionStep(questions: List<CustomQuestion>, onQuestionsChange: (List<CustomQuestion>) -> Unit, onNext: () -> Unit, onBack: () -> Unit) {
    var newQuestionText by remember { mutableStateOf("") }
    Column {
        Text("Langkah 2: Tambah Pertanyaan", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(questions) { q -> 
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(q.question, modifier = Modifier.padding(12.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = newQuestionText, 
            onValueChange = { newQuestionText = it }, 
            label = { Text("Pertanyaan Baru") }, 
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { 
                onQuestionsChange(questions + CustomQuestion(id = UUID.randomUUID().toString(), question = newQuestionText, options = listOf("Opsi 1", "Opsi 2", "Opsi 3", "Opsi 4")))
                newQuestionText = ""
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text("Tambah Pertanyaan") }
        
        Row(modifier = Modifier.padding(top = 24.dp).fillMaxWidth()) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Kembali") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onNext, enabled = questions.isNotEmpty(), modifier = Modifier.weight(1f)) { Text("Lanjut") }
        }
    }
}

@Composable
fun MediaStep(questions: List<CustomQuestion>, images: MutableMap<String, Uri>, onPublish: () -> Unit, onBack: () -> Unit) {
    var selectedQuestionId by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedQuestionId?.let { id ->
                images[id] = it
            }
        }
    }
    
    Column {
        Text("Langkah 3: Tambah Media (Opsional)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(questions) { q ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { 
                            selectedQuestionId = q.id
                            launcher.launch("image/*") 
                        }
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(q.question, modifier = Modifier.weight(1f))
                        if (images.containsKey(q.id)) {
                            Text("✅ Terpilih", color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("📁 Pilih Gambar", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        
        Row(modifier = Modifier.padding(top = 24.dp).fillMaxWidth()) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Kembali") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onPublish, modifier = Modifier.weight(1f)) { Text("Publikasikan") }
        }
    }
}
