package com.example.techmain.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory",
    foreignKeys = [
        ForeignKey(
            entity = ShopItem::class,
            parentColumns = ["id"],
            childColumns = ["shopItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("shopItemId")]
)
data class Inventory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shopItemId: Long,
    val isEquipped: Boolean = false
)
