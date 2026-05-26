package com.example.techmain.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.firebase.CustomQuiz
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class CreatorUiState(
    val myQuizzes: List<CustomQuiz> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreatorViewModel : ViewModel() {
    private val firestoreService = FirebaseModule.firestoreService
    private val storage = FirebaseModule.storageService
    private val _state = MutableStateFlow(CreatorUiState())
    val state = _state.asStateFlow()

    fun resetSuccess() {
        _state.value = _state.value.copy(isSuccess = false)
    }

    init {
        loadMyQuizzes()
    }

    private fun loadMyQuizzes() {
        val userId = FirebaseModule.getUserId() ?: return
        viewModelScope.launch {
            firestoreService.listenMyQuizzes(userId).collect { quizzes ->
                _state.value = _state.value.copy(myQuizzes = quizzes)
            }
        }
    }

    fun deleteQuiz(quiz: CustomQuiz) {
        viewModelScope.launch {
            try {
                storage.deleteQuizMedia(quiz.creatorId, quiz.id)
                firestoreService.deleteCustomQuiz(quiz.id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal menghapus kuis")
            }
        }
    }

    fun saveQuiz(quiz: CustomQuiz, images: Map<String, android.net.Uri>) {
        val userId = FirebaseModule.getUserId() ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isSuccess = false)
            try {
                val finalQuiz = quiz.copy(creatorId = userId)
                val quizId = UUID.randomUUID().toString()
                // Upload all images first, then create the Firestore doc
                val updatedQuestions = finalQuiz.questions.map { question ->
                    val imageUri = images[question.id]
                    if (imageUri != null) {
                        val imageUrl = storage.uploadQuizImage(userId, quizId, question.id, imageUri)
                            .getOrThrow()
                        question.copy(imageUrl = imageUrl)
                    } else {
                        question
                    }
                }
                firestoreService.createCustomQuiz(finalQuiz.copy(questions = updatedQuestions), quizId)
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "Gagal menyimpan kuis: ${e.message}")
            }
        }
    }
}
