package com.example.techmain.data.repository

import com.example.techmain.data.db.dao.FlashcardDao
import com.example.techmain.data.db.dao.FlashcardDeckDao
import com.example.techmain.data.db.entity.Flashcard
import com.example.techmain.data.db.entity.FlashcardDeck
import kotlinx.coroutines.flow.Flow

class FlashcardRepository(
    private val flashcardDao: FlashcardDao,
    private val flashcardDeckDao: FlashcardDeckDao
) {
    fun getAllDecks(): Flow<List<FlashcardDeck>> = flashcardDeckDao.getAllDecks()
    fun getFlashcardsByDeck(deckId: Long): Flow<List<Flashcard>> = flashcardDao.getFlashcardsByDeck(deckId)
    fun getDueFlashcards(deckId: Long, now: Long = System.currentTimeMillis()): Flow<List<Flashcard>> =
        flashcardDao.getDueFlashcards(deckId, now)
    fun getFlashcardCount(deckId: Long): Flow<Int> = flashcardDao.getCountByDeck(deckId)

    suspend fun getDeckById(id: Long): FlashcardDeck? = flashcardDeckDao.getDeckById(id)
    suspend fun getFlashcardById(id: Long): Flashcard? = flashcardDao.getFlashcardById(id)

    suspend fun addDeck(deck: FlashcardDeck): Long = flashcardDeckDao.insert(deck)
    suspend fun updateDeck(deck: FlashcardDeck) = flashcardDeckDao.update(deck)
    suspend fun deleteDeck(deck: FlashcardDeck) = flashcardDeckDao.delete(deck)

    suspend fun addFlashcard(flashcard: Flashcard): Long = flashcardDao.insert(flashcard)
    suspend fun updateFlashcard(flashcard: Flashcard) = flashcardDao.update(flashcard)
    suspend fun deleteFlashcard(flashcard: Flashcard) = flashcardDao.delete(flashcard)
}
