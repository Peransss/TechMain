package com.example.techmain.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val xpReward: Int = 50,
    val goldReward: Int = 10,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
