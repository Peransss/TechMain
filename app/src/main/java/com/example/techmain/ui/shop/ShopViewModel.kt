package com.example.techmain.ui.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techmain.TechMainApp
import com.example.techmain.data.db.entity.Avatar
import com.example.techmain.data.db.entity.Inventory
import com.example.techmain.data.db.entity.ShopItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as TechMainApp).shopRepository

    val avatar: Flow<Avatar?> = repo.getAvatar()
    val shopItems: Flow<List<ShopItem>> = repo.getShopItems()
    val inventory: Flow<List<Inventory>> = repo.getInventory()
    val equippedItems: Flow<List<Inventory>> = repo.getEquippedItems()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            val existingAvatar = repo.getAvatarOnce()
            if (existingAvatar == null) {
                repo.createAvatar(Avatar(name = "Petualang"))
                repo.seedShopItems()
            }
            _isInitialized.value = true
        }
    }

    fun clearMessage() { _message.value = null }

    fun renameAvatar(name: String) {
        viewModelScope.launch {
            val a = repo.getAvatarOnce() ?: return@launch
            repo.updateAvatar(a.copy(name = name))
        }
    }

    fun buyItem(shopItem: ShopItem) {
        viewModelScope.launch {
            val owned = repo.isItemOwned(shopItem.id)
            if (owned) {
                _message.value = "${shopItem.name} sudah dimiliki!"
                return@launch
            }
            val success = repo.buyItem(shopItem.id)
            _message.value = if (success) "Berhasil membeli ${shopItem.name}!"
            else "Gold tidak cukup!"
        }
    }

    fun equipItem(inventoryItem: Inventory) {
        viewModelScope.launch {
            repo.equipItem(inventoryItem.id)
        }
    }

    fun unequipItem(inventoryItem: Inventory) {
        viewModelScope.launch {
            repo.unequipItem(inventoryItem.id)
        }
    }
}
