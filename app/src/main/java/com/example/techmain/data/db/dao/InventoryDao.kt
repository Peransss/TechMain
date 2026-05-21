package com.example.techmain.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.techmain.data.db.entity.Inventory
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory WHERE shopItemId = :shopItemId LIMIT 1")
    suspend fun getByShopItemId(shopItemId: Long): Inventory?

    @Query("SELECT * FROM inventory")
    fun getAll(): Flow<List<Inventory>>

    @Query("SELECT * FROM inventory WHERE isEquipped = 1")
    fun getEquippedItems(): Flow<List<Inventory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inventory: Inventory): Long

    @Update
    suspend fun update(inventory: Inventory)

    @Query("UPDATE inventory SET isEquipped = 0")
    suspend fun unequipAll()

    @Query("UPDATE inventory SET isEquipped = 1 WHERE id = :id")
    suspend fun equip(id: Long)

    @Query("UPDATE inventory SET isEquipped = 0 WHERE id = :id")
    suspend fun unequip(id: Long)

    @Query("DELETE FROM inventory WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM inventory WHERE shopItemId = :shopItemId")
    suspend fun countOwned(shopItemId: Long): Int
}
