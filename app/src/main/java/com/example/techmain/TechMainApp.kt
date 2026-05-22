package com.example.techmain

import android.app.Application
import com.example.techmain.data.db.AppDatabase

class TechMainApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
}
