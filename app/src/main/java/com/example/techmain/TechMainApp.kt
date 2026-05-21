package com.example.techmain

import android.app.Application
import com.example.techmain.data.db.AppDatabase
import com.example.techmain.data.repository.FlashcardRepository
import com.example.techmain.data.repository.PomodoroRepository
import com.example.techmain.data.repository.QuestRepository
import com.example.techmain.data.repository.ShopRepository

class TechMainApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }

    val questRepository by lazy { QuestRepository(database.questDao()) }
    val pomodoroRepository by lazy { PomodoroRepository(database.pomodoroDao()) }
    val flashcardRepository by lazy {
        FlashcardRepository(database.flashcardDao(), database.flashcardDeckDao())
    }
    val shopRepository by lazy {
        ShopRepository(database.avatarDao(), database.shopDao(), database.inventoryDao())
    }
}
