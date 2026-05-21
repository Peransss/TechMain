package com.example.techmain.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avatar")
data class Avatar(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "Petualang",
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 100,
    val gold: Int = 0,
    val equippedHead: String? = null,
    val equippedBody: String? = null,
    val equippedWeapon: String? = null
)
