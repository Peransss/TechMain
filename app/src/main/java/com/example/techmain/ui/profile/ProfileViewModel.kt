package com.example.techmain.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.TechMainApp
import com.example.techmain.data.db.entity.Avatar
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val displayName: String = "Pemain",
    val avatarEmoji: String = "\uD83D\uDE0E",
    val rating: Int = 1000,
    val wins: Int = 0,
    val losses: Int = 0,
    val totalGames: Int = 0,
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0,
    val bestScores: Map<String, Int> = emptyMap()
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val firestore = FirestoreService()
    private val avatarDao = (application as TechMainApp).database.avatarDao()

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = FirebaseModule.getUserId() ?: return@launch
            firestore.createOrUpdateUser(userId, "Pemain")

            firestore.getUserStats(userId).collect { stats ->
                if (stats != null) {
                    val rawScores = stats["bestScores"] as? Map<*, *>
                    val bestScores = rawScores?.mapKeys { it.key.toString() }?.mapValues { (_, v) -> (v as? Long)?.toInt() ?: 0 } ?: emptyMap()
                    _state.value = _state.value.copy(
                        rating = (stats["rating"] as? Long)?.toInt() ?: 1000,
                        wins = (stats["wins"] as? Long)?.toInt() ?: 0,
                        losses = (stats["losses"] as? Long)?.toInt() ?: 0,
                        totalGames = (stats["totalGames"] as? Long)?.toInt() ?: 0,
                        correctAnswers = (stats["correctAnswers"] as? Long)?.toInt() ?: 0,
                        totalAnswers = (stats["totalAnswers"] as? Long)?.toInt() ?: 0,
                        bestScores = bestScores
                    )
                }
            }
        }

        viewModelScope.launch {
            avatarDao.getAvatar().collect { avatar ->
                if (avatar != null) {
                    _state.value = _state.value.copy(
                        displayName = avatar.name,
                        avatarEmoji = avatar.avatarEmoji
                    )
                } else {
                    avatarDao.insert(Avatar(name = "Pemain"))
                }
            }
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            val userId = FirebaseModule.getUserId() ?: return@launch
            val avatar = avatarDao.getAvatarOnce() ?: return@launch
            avatarDao.update(avatar.copy(name = name))
            firestore.updateDisplayName(userId, name)
        }
    }
}
