package com.example.techmain.data.repository

import com.example.techmain.data.db.dao.PomodoroDao
import com.example.techmain.data.db.entity.PomodoroSession
import kotlinx.coroutines.flow.Flow

class PomodoroRepository(private val pomodoroDao: PomodoroDao) {
    fun getAllSessions(): Flow<List<PomodoroSession>> = pomodoroDao.getAllSessions()
    fun getTotalFocusSessions(): Flow<Int> = pomodoroDao.getTotalFocusSessions()
    fun getTotalFocusMinutes(): Flow<Int> = pomodoroDao.getTotalFocusMinutes()
    fun getTotalMonstersDefeated(): Flow<Int> = pomodoroDao.getTotalMonstersDefeated()

    suspend fun addSession(session: PomodoroSession): Long = pomodoroDao.insert(session)
}
