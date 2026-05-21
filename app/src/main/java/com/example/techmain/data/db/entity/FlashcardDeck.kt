package com.example.techmain.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_decks")
data class FlashcardDeck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
