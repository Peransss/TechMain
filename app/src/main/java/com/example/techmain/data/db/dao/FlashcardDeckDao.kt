package com.example.techmain.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.techmain.data.db.entity.FlashcardDeck
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDeckDao {
    @Query("SELECT * FROM flashcard_decks ORDER BY createdAt DESC")
    fun getAllDecks(): Flow<List<FlashcardDeck>>

    @Query("SELECT * FROM flashcard_decks WHERE id = :id")
    suspend fun getDeckById(id: Long): FlashcardDeck?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deck: FlashcardDeck): Long

    @Update
    suspend fun update(deck: FlashcardDeck)

    @Delete
    suspend fun delete(deck: FlashcardDeck)
}
