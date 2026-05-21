package com.example.techmain.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.techmain.data.db.entity.Quest
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllQuests(): Flow<List<Quest>>

    @Query("SELECT * FROM quests WHERE id = :id")
    suspend fun getQuestById(id: Long): Quest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quest: Quest): Long

    @Update
    suspend fun update(quest: Quest)

    @Delete
    suspend fun delete(quest: Quest)

    @Query("UPDATE quests SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setCompleted(id: Long, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM quests WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM quests")
    fun getTotalCount(): Flow<Int>
}
