package com.example.techmain.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.techmain.data.db.dao.AvatarDao
import com.example.techmain.data.db.dao.FlashcardDao
import com.example.techmain.data.db.dao.FlashcardDeckDao
import com.example.techmain.data.db.dao.InventoryDao
import com.example.techmain.data.db.dao.PomodoroDao
import com.example.techmain.data.db.dao.QuestDao
import com.example.techmain.data.db.dao.ShopDao
import com.example.techmain.data.db.entity.Avatar
import com.example.techmain.data.db.entity.Flashcard
import com.example.techmain.data.db.entity.FlashcardDeck
import com.example.techmain.data.db.entity.Inventory
import com.example.techmain.data.db.entity.PomodoroSession
import com.example.techmain.data.db.entity.Quest
import com.example.techmain.data.db.entity.ShopItem

@Database(
    entities = [
        Quest::class,
        PomodoroSession::class,
        FlashcardDeck::class,
        Flashcard::class,
        Avatar::class,
        ShopItem::class,
        Inventory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun flashcardDeckDao(): FlashcardDeckDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun avatarDao(): AvatarDao
    abstract fun shopDao(): ShopDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "techmain_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
