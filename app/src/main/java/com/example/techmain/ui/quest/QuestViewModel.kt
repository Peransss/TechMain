package com.example.techmain.ui.quest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.TechMainApp
import com.example.techmain.data.db.entity.Quest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as TechMainApp).questRepository
    private val shopRepo = (application as TechMainApp).shopRepository

    val quests: Flow<List<Quest>> = repo.getAllQuests()
    val completedCount: Flow<Int> = repo.getCompletedCount()
    val totalCount: Flow<Int> = repo.getTotalCount()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    fun showAddDialog() { _showAddDialog.value = true }
    fun hideAddDialog() {
        _showAddDialog.value = false
        _title.value = ""
        _description.value = ""
    }

    fun onTitleChange(value: String) { _title.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }

    fun addQuest() {
        val t = _title.value.trim()
        if (t.isEmpty()) return
        viewModelScope.launch {
            repo.addQuest(Quest(title = t, description = _description.value.trim()))
            hideAddDialog()
        }
    }

    fun toggleQuestCompletion(quest: Quest) {
        viewModelScope.launch {
            if (quest.isCompleted) {
                repo.uncompleteQuest(quest.id)
            } else {
                repo.completeQuest(quest.id)
                shopRepo.addRewards(quest.xpReward, quest.goldReward)
            }
        }
    }

    fun deleteQuest(quest: Quest) {
        viewModelScope.launch { repo.deleteQuest(quest) }
    }
}
