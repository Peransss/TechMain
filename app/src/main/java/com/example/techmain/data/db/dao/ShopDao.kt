package com.example.techmain.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.techmain.data.db.entity.Inventory
import com.example.techmain.data.db.entity.ShopItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop_items")
    fun getAllItems(): Flow<List<ShopItem>>

    @Query("SELECT * FROM shop_items WHERE id = :id")
    suspend fun getItemById(id: Long): ShopItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShopItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ShopItem>)

    @Update
    suspend fun updateItem(item: ShopItem)

    @Delete
    suspend fun deleteItem(item: ShopItem)
}
