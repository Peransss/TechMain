package com.example.techmain.data.repository

import com.example.techmain.data.db.dao.QuestDao
import com.example.techmain.data.db.entity.Quest
import kotlinx.coroutines.flow.Flow

class QuestRepository(private val questDao: QuestDao) {
    fun getAllQuests(): Flow<List<Quest>> = questDao.getAllQuests()
    fun getCompletedCount(): Flow<Int> = questDao.getCompletedCount()
    fun getTotalCount(): Flow<Int> = questDao.getTotalCount()

    suspend fun getQuestById(id: Long): Quest? = questDao.getQuestById(id)

    suspend fun addQuest(quest: Quest): Long = questDao.insert(quest)

    suspend fun updateQuest(quest: Quest) = questDao.update(quest)

    suspend fun deleteQuest(quest: Quest) = questDao.delete(quest)

    suspend fun completeQuest(id: Long) = questDao.setCompleted(id, true)

    suspend fun uncompleteQuest(id: Long) = questDao.setCompleted(id, false)
}
