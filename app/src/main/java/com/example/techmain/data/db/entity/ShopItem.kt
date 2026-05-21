package com.example.techmain.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val price: Int,
    val type: String,
    val iconRes: String = ""
)
