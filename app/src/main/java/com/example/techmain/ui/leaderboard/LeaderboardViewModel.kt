package com.example.techmain.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.firebase.FirebaseModule
import com.example.techmain.firebase.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LeaderboardViewMode { LIST, TABLE }

data class LeaderboardEntry(
    val rank: Int = 0,
    val userId: String = "",
    val displayName: String = "Pemain",
    val rating: Int = 1000,
    val wins: Int = 0,
    val losses: Int = 0,
    val totalGames: Int = 0,
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0,
    val isMe: Boolean = false
)

data class LeaderboardState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val myStats: LeaderboardEntry? = null,
    val isLoading: Boolean = true,
    val viewMode: LeaderboardViewMode = LeaderboardViewMode.LIST
)

class LeaderboardViewModel : ViewModel() {
    private val firestore = FirestoreService()
    private val _state = MutableStateFlow(LeaderboardState())
    val state: StateFlow<LeaderboardState> = _state.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun toggleViewMode() {
        _state.value = _state.value.copy(
            viewMode = if (_state.value.viewMode == LeaderboardViewMode.LIST)
                LeaderboardViewMode.TABLE
            else
                LeaderboardViewMode.LIST
        )
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            val myUserId = FirebaseModule.getUserId() ?: return@launch

            firestore.getLeaderboard().collect { users ->
                val entries = users.mapIndexed { index, data ->
                    LeaderboardEntry(
                        rank = index + 1,
                        userId = data["userId"] as? String ?: "",
                        displayName = data["displayName"] as? String ?: "Pemain",
                        rating = (data["rating"] as? Long)?.toInt() ?: 1000,
                        wins = (data["wins"] as? Long)?.toInt() ?: 0,
                        losses = (data["losses"] as? Long)?.toInt() ?: 0,
                        totalGames = (data["totalGames"] as? Long)?.toInt() ?: 0,
                        correctAnswers = (data["correctAnswers"] as? Long)?.toInt() ?: 0,
                        totalAnswers = (data["totalAnswers"] as? Long)?.toInt() ?: 0,
                        isMe = data["userId"] == myUserId
                    )
                }
                val myEntry = entries.find { it.isMe }
                _state.value = LeaderboardState(
                    entries = entries,
                    myStats = myEntry,
                    isLoading = false
                )
            }
        }
    }
}
