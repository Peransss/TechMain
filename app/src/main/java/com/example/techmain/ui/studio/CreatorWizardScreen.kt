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
import com.example.techmain.firebase.CustomQuestion
import com.example.techmain.firebase.CustomQuiz
import java.util.UUID

@Composable
fun CreatorWizardScreen(viewModel: CreatorViewModel) {
    var step by remember { mutableStateOf(0) }
    var quizTitle by remember { mutableStateOf("") }
    var questions by remember { mutableStateOf(listOf<CustomQuestion>()) }
    val images = remember { mutableStateMapOf<String, Uri>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        AnimatedContent(targetState = step, label = "wizard") { targetStep ->
            when(targetStep) {
                0 -> MetadataStep(quizTitle, { quizTitle = it }, { step++ })
                1 -> QuestionStep(questions, { questions = it }, { step++ }, { step-- })
                2 -> MediaStep(questions, images, { viewModel.saveQuiz(CustomQuiz(title = quizTitle, questions = questions), images) }, { step-- })
            }
        }
    }
}

@Composable
fun MetadataStep(title: String, onTitleChange: (String) -> Unit, onNext: () -> Unit) {
    Column {
        OutlinedTextField(value = title, onValueChange = onTitleChange, label = { Text("Quiz Title") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = onNext, enabled = title.isNotEmpty(), modifier = Modifier.padding(top = 16.dp)) { Text("Next") }
    }
}

@Composable
fun QuestionStep(questions: List<CustomQuestion>, onQuestionsChange: (List<CustomQuestion>) -> Unit, onNext: () -> Unit, onBack: () -> Unit) {
    var newQuestionText by remember { mutableStateOf("") }
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(questions) { q -> Text(q.question) }
        }
        OutlinedTextField(value = newQuestionText, onValueChange = { newQuestionText = it }, label = { Text("New Question") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { 
            onQuestionsChange(questions + CustomQuestion(id = UUID.randomUUID().toString(), question = newQuestionText))
            newQuestionText = ""
        }) { Text("Add Question") }
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onNext, enabled = questions.isNotEmpty()) { Text("Next") }
        }
    }
}

@Composable
fun MediaStep(questions: List<CustomQuestion>, images: MutableMap<String, Uri>, onPublish: () -> Unit, onBack: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Handle image selection
    }
    
    Column {
        Text("Media Step")
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(questions) { q ->
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { /* launch picker */ }) {
                    Text(q.question)
                    Spacer(modifier = Modifier.weight(1f))
                    if (images.containsKey(q.id)) Text("Image selected")
                }
            }
        }
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onPublish) { Text("Publish") }
        }
    }
}
