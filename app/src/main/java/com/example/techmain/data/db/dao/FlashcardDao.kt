package com.example.techmain.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.techmain.data.db.entity.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY nextReviewDate ASC")
    fun getFlashcardsByDeck(deckId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getFlashcardById(id: Long): Flashcard?

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND nextReviewDate <= :now ORDER BY nextReviewDate ASC")
    fun getDueFlashcards(deckId: Long, now: Long): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flashcard: Flashcard): Long

    @Update
    suspend fun update(flashcard: Flashcard)

    @Delete
    suspend fun delete(flashcard: Flashcard)

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    fun getCountByDeck(deckId: Long): Flow<Int>
}
