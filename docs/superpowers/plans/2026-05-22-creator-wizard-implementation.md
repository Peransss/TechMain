# Creator Wizard & Multimedia Upload Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the `CreatorWizardScreen` to allow users to create quizzes with image-based questions and handle uploading to Firebase.

**Architecture:** Use `AnimatedContent` for a multi-step form (Metadata -> Questions -> Media). Use `ActivityResultContracts.GetContent` for image selection. Use `NeonHackerBorder` for styling. Firebase interaction via `CreatorViewModel`.

**Tech Stack:** Jetpack Compose, Firebase Firestore, Firebase Storage, Coil, ViewModel

---

### Task 1: Create CreatorWizardScreen UI

**Files:**
- Create: `app/src/main/java/com/example/techmain/ui/studio/CreatorWizardScreen.kt`

- [ ] **Step 1: Implement basic Wizard scaffold**

```kotlin
package com.example.techmain.ui.studio

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreatorWizardScreen(viewModel: CreatorViewModel) {
    var step by remember { mutableStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        AnimatedContent(targetState = step, label = "wizard") { targetStep ->
            when(targetStep) {
                0 -> MetadataStep(onNext = { step++ })
                1 -> QuestionStep(onNext = { step++ }, onBack = { step-- })
                2 -> MediaStep(onPublish = { /* TODO */ }, onBack = { step-- })
            }
        }
    }
}

@Composable
fun MetadataStep(onNext: () -> Unit) {
    Column {
        Text("Metadata Step")
        Button(onClick = onNext) { Text("Next") }
    }
}

@Composable
fun QuestionStep(onNext: () -> Unit, onBack: () -> Unit) {
    Column {
        Text("Question Step")
        Button(onClick = onBack) { Text("Back") }
        Button(onClick = onNext) { Text("Next") }
    }
}

@Composable
fun MediaStep(onPublish: () -> Unit, onBack: () -> Unit) {
    Column {
        Text("Media Step")
        Button(onClick = onBack) { Text("Back") }
        Button(onClick = onPublish) { Text("Publish") }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/studio/CreatorWizardScreen.kt
git commit -m "feat: scaffold creator wizard ui"
```

### Task 2: Implement CreatorViewModel logic for upload and save

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/studio/CreatorViewModel.kt`

- [ ] **Step 1: Add upload and save functions to CreatorViewModel**

```kotlin
// In CreatorViewModel
    fun saveQuiz(quiz: CustomQuiz, images: Map<String, Uri>) {
        val userId = FirebaseModule.getUserId() ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val quizId = firestoreService.createCustomQuiz(quiz)
                val updatedQuestions = quiz.questions.map { question ->
                    val imageUri = images[question.id]
                    if (imageUri != null) {
                        val imageUrl = storage.uploadQuizImage(userId, quizId, question.id, imageUri)
                            .getOrThrow()
                        question.copy(imageUrl = imageUrl)
                    } else {
                        question
                    }
                }
                firestoreService.updateCustomQuizQuestions(quizId, updatedQuestions)
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "Gagal menyimpan kuis")
            }
        }
    }
```

- [ ] **Step 2: Add updateQuestions to FirestoreService**

Modify `app/src/main/java/com/example/techmain/firebase/FirestoreService.kt` to add `updateCustomQuizQuestions`.

```kotlin
    suspend fun updateCustomQuizQuestions(quizId: String, questions: List<CustomQuestion>) {
        customQuizzesCollection.document(quizId).update("questions", questions).await()
    }
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/studio/CreatorViewModel.kt app/src/main/java/com/example/techmain/firebase/FirestoreService.kt
git commit -m "feat: implement creator viewmodel upload logic"
```

### Task 3: Integrate Image Picker in CreatorWizardScreen

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/studio/CreatorWizardScreen.kt`

- [ ] **Step 1: Add ActivityResultContracts and implement UI**

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/studio/CreatorWizardScreen.kt
git commit -m "feat: implement image picker in creator wizard"
```
