package com.example.techmain.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.techmain.data.db.entity.Avatar
import kotlinx.coroutines.flow.Flow

@Dao
interface AvatarDao {
    @Query("SELECT * FROM avatar LIMIT 1")
    fun getAvatar(): Flow<Avatar?>

    @Query("SELECT * FROM avatar LIMIT 1")
    suspend fun getAvatarOnce(): Avatar?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(avatar: Avatar): Long

    @Update
    suspend fun update(avatar: Avatar)

    @Query("UPDATE avatar SET currentXp = currentXp + :xp, gold = gold + :gold WHERE id = :id")
    suspend fun addRewards(id: Long, xp: Int, gold: Int)

    @Query("UPDATE avatar SET gold = gold + :gold WHERE id = :id")
    suspend fun addGold(id: Long, gold: Int)

    @Query("UPDATE avatar SET gold = gold - :gold WHERE id = :id AND gold >= :gold")
    suspend fun spendGold(id: Long, gold: Int): Int

    @Query("UPDATE avatar SET level = level + 1, currentXp = 0, xpToNextLevel = xpToNextLevel + 50 WHERE id = :id")
    suspend fun levelUp(id: Long)
}
