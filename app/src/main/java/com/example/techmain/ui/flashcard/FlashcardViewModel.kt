package com.example.techmain.ui.flashcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.TechMainApp
import com.example.techmain.data.db.entity.Flashcard
import com.example.techmain.data.db.entity.FlashcardDeck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as TechMainApp).flashcardRepository

    val decks: Flow<List<FlashcardDeck>> = repo.getAllDecks()

    private val _selectedDeckId = MutableStateFlow<Long?>(null)
    val selectedDeckId: StateFlow<Long?> = _selectedDeckId.asStateFlow()

    private val _showAddDeckDialog = MutableStateFlow(false)
    val showAddDeckDialog: StateFlow<Boolean> = _showAddDeckDialog.asStateFlow()

    private val _showAddCardDialog = MutableStateFlow(false)
    val showAddCardDialog: StateFlow<Boolean> = _showAddCardDialog.asStateFlow()

    private val _deckTitle = MutableStateFlow("")
    val deckTitle: StateFlow<String> = _deckTitle.asStateFlow()

    private val _question = MutableStateFlow("")
    val question: StateFlow<String> = _question.asStateFlow()

    private val _answer = MutableStateFlow("")
    val answer: StateFlow<String> = _answer.asStateFlow()

    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    private val _showAnswer = MutableStateFlow(false)
    val showAnswer: StateFlow<Boolean> = _showAnswer.asStateFlow()

    private val _studyMode = MutableStateFlow(false)
    val studyMode: StateFlow<Boolean> = _studyMode.asStateFlow()

    private var cachedCards: List<Flashcard> = emptyList()

    fun selectDeck(deckId: Long) {
        _selectedDeckId.value = deckId
        _currentCardIndex.value = 0
        _showAnswer.value = false
        _studyMode.value = false
    }

    fun clearSelection() {
        _selectedDeckId.value = null
        cachedCards = emptyList()
    }

    fun getFlashcards(): Flow<List<Flashcard>> {
        val id = _selectedDeckId.value ?: return repo.getFlashcardsByDeck(-1)
        return repo.getFlashcardsByDeck(id)
    }

    fun getCardCount(): Flow<Int> {
        val id = _selectedDeckId.value ?: return repo.getFlashcardCount(-1)
        return repo.getFlashcardCount(id)
    }

    fun showAddDeckDialog() { _showAddDeckDialog.value = true }
    fun hideAddDeckDialog() {
        _showAddDeckDialog.value = false
        _deckTitle.value = ""
    }

    fun showAddCardDialog() { _showAddCardDialog.value = true }
    fun hideAddCardDialog() {
        _showAddCardDialog.value = false
        _question.value = ""
        _answer.value = ""
    }

    fun onDeckTitleChange(value: String) { _deckTitle.value = value }
    fun onQuestionChange(value: String) { _question.value = value }
    fun onAnswerChange(value: String) { _answer.value = value }

    fun addDeck() {
        val t = _deckTitle.value.trim()
        if (t.isEmpty()) return
        viewModelScope.launch {
            repo.addDeck(FlashcardDeck(title = t))
            hideAddDeckDialog()
        }
    }

    fun addCard() {
        val q = _question.value.trim()
        val a = _answer.value.trim()
        val deckId = _selectedDeckId.value ?: return
        if (q.isEmpty() || a.isEmpty()) return
        viewModelScope.launch {
            repo.addFlashcard(Flashcard(deckId = deckId, question = q, answer = a))
            hideAddCardDialog()
        }
    }

    fun deleteDeck(deck: FlashcardDeck) {
        viewModelScope.launch { repo.deleteDeck(deck) }
        clearSelection()
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch { repo.deleteFlashcard(card) }
    }

    fun startStudy(cards: List<Flashcard>) {
        cachedCards = cards
        _currentCardIndex.value = 0
        _showAnswer.value = false
        _studyMode.value = true
    }

    fun stopStudy() {
        _studyMode.value = false
        cachedCards = emptyList()
    }

    fun flipCard() {
        _showAnswer.value = !_showAnswer.value
    }

    fun nextCard() {
        if (_currentCardIndex.value < cachedCards.size - 1) {
            _currentCardIndex.value += 1
            _showAnswer.value = false
        }
    }

    fun prevCard() {
        if (_currentCardIndex.value > 0) {
            _currentCardIndex.value -= 1
            _showAnswer.value = false
        }
    }

    fun getCurrentCard(): Flashcard? =
        cachedCards.getOrNull(_currentCardIndex.value)

    fun getStudyProgress(): String =
        "${_currentCardIndex.value + 1}/${cachedCards.size}"
}
