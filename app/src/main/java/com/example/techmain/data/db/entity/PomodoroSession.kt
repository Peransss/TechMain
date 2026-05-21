package com.example.techmain.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val focusSessionsCompleted: Int = 0,
    val totalFocusMinutes: Int = 0,
    val monstersDefeated: Int = 0
)
