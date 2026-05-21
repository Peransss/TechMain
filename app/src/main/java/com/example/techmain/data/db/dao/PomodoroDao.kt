package com.example.techmain.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.techmain.data.db.entity.PomodoroSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<PomodoroSession>>

    @Query("SELECT * FROM pomodoro_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): PomodoroSession?

    @Insert
    suspend fun insert(session: PomodoroSession): Long

    @Query("SELECT COALESCE(SUM(focusSessionsCompleted), 0) FROM pomodoro_sessions")
    fun getTotalFocusSessions(): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalFocusMinutes), 0) FROM pomodoro_sessions")
    fun getTotalFocusMinutes(): Flow<Int>

    @Query("SELECT COALESCE(SUM(monstersDefeated), 0) FROM pomodoro_sessions")
    fun getTotalMonstersDefeated(): Flow<Int>
}
