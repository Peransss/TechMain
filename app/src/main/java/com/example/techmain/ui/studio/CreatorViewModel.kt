package com.example.techmain.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.firebase.CustomQuiz
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreatorUiState(
    val myQuizzes: List<CustomQuiz> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CreatorViewModel : ViewModel() {
    private val firestoreService = FirestoreService()
    private val storage = FirebaseModule.storageService
    private val _state = MutableStateFlow(CreatorUiState())
    val state = _state.asStateFlow()

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
                firestoreService.deleteCustomQuiz(quiz.id)
                storage.deleteQuizMedia(quiz.creatorId, quiz.id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal menghapus kuis")
            }
        }
    }
}
