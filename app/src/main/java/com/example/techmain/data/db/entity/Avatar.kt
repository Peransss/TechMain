package com.example.techmain.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avatar")
data class Avatar(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "Pemain",
    val avatarEmoji: String = "\uD83D\uDE0E"
)
