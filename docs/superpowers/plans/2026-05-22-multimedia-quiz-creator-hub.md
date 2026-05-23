# Multimedia Quiz Creator Hub Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Transform TechMain into a user-generated content platform with a "Neon Hacker" styled Studio for creating and managing multimedia quiz packs.

**Architecture:** MVVM with a centralized Firebase Storage/Firestore backend. The Studio tab handles CRUD operations for `CustomQuiz` documents, while the Battle screen is updated to display featured community content and multimedia questions.

**Tech Stack:** Kotlin, Jetpack Compose, Firebase Firestore, Firebase Storage, Coil (for image loading).

---

### Task 1: Neon Hacker Theme & Data Models

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/theme/Color.kt`
- Create: `app/src/main/java/com/example/techmain/firebase/CustomQuizModels.kt`

- [ ] **Step 1: Define Neon Hacker Colors**

```kotlin
// app/src/main/java/com/example/techmain/ui/theme/Color.kt
val NeonHackerBackground = Color(0xFF0A0A0C)
val NeonHackerSurface = Color(0xFF121217)
val NeonHackerPrimary = Color(0xFF00FFFF) // Cyan
val NeonHackerSecondary = Color(0xFF39FF14) // Lime Green
val NeonHackerBorder = Color(0xFF1F1F2E)
```

- [ ] **Step 2: Define Data Models**

```kotlin
// app/src/main/java/com/example/techmain/firebase/CustomQuizModels.kt
package com.example.techmain.firebase

data class CustomQuiz(
    val id: String = "",
    val creatorId: String = "",
    val title: String = "",
    val categoryId: String = "",
    val questions: List<CustomQuestion> = emptyList(),
    val playCount: Int = 0,
    val isFeatured: Boolean = false,
    val createdAt: Long = 0L
)

data class CustomQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val imageUrl: String? = null
)
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/theme/Color.kt app/src/main/java/com/example/techmain/firebase/CustomQuizModels.kt
git commit -m "feat: add neon hacker colors and custom quiz models"
```

---

### Task 2: Firebase Storage Service

**Files:**
- Create: `app/src/main/java/com/example/techmain/firebase/StorageService.kt`
- Modify: `app/src/main/java/com/example/techmain/firebase/FirebaseModule.kt`

- [x] **Step 1: Implement Storage Service**

```kotlin
// app/src/main/java/com/example/techmain/firebase/StorageService.kt
package com.example.techmain.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageService {
    private val storage = FirebaseStorage.getInstance().reference

    suspend fun uploadQuizImage(userId: String, quizId: String, questionId: String, uri: Uri): Result<String> {
        return runCatching {
            val ref = storage.child("media/quizzes/$userId/$quizId/$questionId.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }
    }

    suspend fun deleteQuizMedia(userId: String, quizId: String): Result<Unit> {
        return runCatching {
            val dirRef = storage.child("media/quizzes/$userId/$quizId")
            val list = dirRef.listAll().await()
            list.items.forEach { it.delete().await() }
        }
    }
}
```

- [x] **Step 2: Expose StorageService in FirebaseModule**

```kotlin
// Add to FirebaseModule.kt
val storageService = StorageService()
```

- [x] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/firebase/StorageService.kt app/src/main/java/com/example/techmain/firebase/FirebaseModule.kt gradle/libs.versions.toml app/build.gradle.kts
git commit -m "feat: implement firebase storage service for quiz media"
```

---

### Task 3: Firestore Service CRUD Extensions

**Files:**
- Modify: `app/src/main/java/com/example/techmain/firebase/FirestoreService.kt`

- [x] **Step 1: Add CRUD methods for CustomQuiz**

```kotlin
// Add to FirestoreService.kt
private val customQuizzesCollection = db.collection("custom_quizzes")

suspend fun createCustomQuiz(quiz: CustomQuiz): String {
    val ref = customQuizzesCollection.document()
    val finalQuiz = quiz.copy(id = ref.id, createdAt = System.currentTimeMillis())
    ref.set(finalQuiz).await()
    return ref.id
}

fun listenMyQuizzes(userId: String): Flow<List<CustomQuiz>> = callbackFlow {
    val listener = customQuizzesCollection
        .whereEqualTo("creatorId", userId)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val quizzes = snapshot?.toObjects(CustomQuiz::class.java) ?: emptyList()
            trySend(quizzes)
        }
    awaitClose { listener.remove() }
}

suspend fun deleteCustomQuiz(quizId: String) {
    customQuizzesCollection.document(quizId).delete().await()
}

fun fetchFeaturedQuizzes(): Flow<List<CustomQuiz>> = callbackFlow {
    val listener = customQuizzesCollection
        .whereEqualTo("isFeatured", true)
        .limit(5)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val quizzes = snapshot?.toObjects(CustomQuiz::class.java) ?: emptyList()
            trySend(quizzes)
        }
    awaitClose { listener.remove() }
}
```

- [x] **Step 2: Commit**

```bash
git add app/src/main/java/com/example/techmain/firebase/FirestoreService.kt
git commit -m "feat: add firestore crud methods for custom quizzes"
```

---

### Task 4: Creator ViewModel

**Files:**
- Create: `app/src/main/java/com/example/techmain/ui/studio/CreatorViewModel.kt`

- [x] **Step 1: Implement CreatorViewModel with CRUD logic**

```kotlin
// app/src/main/java/com/example/techmain/ui/studio/CreatorViewModel.kt
package com.example.techmain.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.firebase.CustomQuiz
import com.example.techmain.firebase.FirebaseModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreatorUiState(
    val myQuizzes: List<CustomQuiz> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CreatorViewModel : ViewModel() {
    private val firestore = FirebaseModule.firestoreService
    private val storage = FirebaseModule.storageService
    private val _state = MutableStateFlow(CreatorUiState())
    val state = _state.asStateFlow()

    init {
        loadMyQuizzes()
    }

    private fun loadMyQuizzes() {
        val userId = FirebaseModule.getUserId() ?: return
        viewModelScope.launch {
            firestore.listenMyQuizzes(userId).collect { quizzes ->
                _state.value = _state.value.copy(myQuizzes = quizzes)
            }
        }
    }

    fun deleteQuiz(quiz: CustomQuiz) {
        viewModelScope.launch {
            try {
                firestore.deleteCustomQuiz(quiz.id)
                storage.deleteQuizMedia(quiz.creatorId, quiz.id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal menghapus kuis")
            }
        }
    }
}
```

- [x] **Step 2: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/studio/CreatorViewModel.kt
git commit -m "feat: implement creator viewmodel for studio tab"
```

---

### Task 5: Studio UI & Navigation Integration

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/navigation/NavGraph.kt`
- Modify: `app/src/main/java/com/example/techmain/MainActivity.kt`
- Create: `app/src/main/java/com/example/techmain/ui/studio/StudioScreen.kt`

- [ ] **Step 1: Update NavGraph with Studio Tab**

```kotlin
// NavGraph.kt
data object Studio : Screen("studio", "Studio", Icons.Default.Palette) // Add Palette icon
```

- [ ] **Step 2: Add StudioScreen with Neon Hacker styling**

```kotlin
// app/src/main/java/com/example/techmain/ui/studio/StudioScreen.kt
@Composable
fun StudioScreen(viewModel: CreatorViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        containerColor = NeonHackerBackground,
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Navigate to Wizard */ }, containerColor = NeonHackerSecondary) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            }
        }
    ) { padding ->
        // List of quizzes with NeonHackerBorder and NeonHackerPrimary text
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/navigation/NavGraph.kt app/src/main/java/com/example/techmain/MainActivity.kt app/src/main/java/com/example/techmain/ui/studio/StudioScreen.kt
git commit -m "feat: integrate studio tab into navigation and ui"
```

---

### Task 6: Creator Wizard & Multimedia Upload

**Files:**
- Create: `app/src/main/java/com/example/techmain/ui/studio/CreatorWizardScreen.kt`

- [x] **Step 1: Build Wizard UI with Image Selection**

```kotlin
// CreatorWizardScreen.kt implemented using ActivityResultContracts.GetContent() and AnimatedContent
```

- [x] **Step 2: Implement Save/Publish logic in CreatorViewModel**

```kotlin
// Implemented handle sequential uploads to Firebase Storage before saving to Firestore
```

- [x] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/studio/CreatorWizardScreen.kt
git commit -m "feat: implement creator wizard with multimedia upload"
```

---

### Task 7: Battle Integration (Featured & Images)

**Files:**
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleMainScreen.kt`
- Modify: `app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt`

- [x] **Step 1: Add Featured Carousel to BattleMainScreen**

```kotlin
// LazyRow implementation for featured CustomQuiz items
```

- [x] **Step 2: Update BattleGameScreen to show AsyncImage (Coil)**

```kotlin
// Coil AsyncImage integrated for question images
```

- [x] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/techmain/ui/battle/BattleMainScreen.kt app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt
git commit -m "feat: integrate featured quizzes and multimedia into battle"
```
