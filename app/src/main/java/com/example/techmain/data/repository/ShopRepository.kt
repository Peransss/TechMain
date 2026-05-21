package com.example.techmain.data.repository

import com.example.techmain.data.db.dao.AvatarDao
import com.example.techmain.data.db.dao.InventoryDao
import com.example.techmain.data.db.dao.ShopDao
import com.example.techmain.data.db.entity.Avatar
import com.example.techmain.data.db.entity.Inventory
import com.example.techmain.data.db.entity.ShopItem
import kotlinx.coroutines.flow.Flow

class ShopRepository(
    private val avatarDao: AvatarDao,
    private val shopDao: ShopDao,
    private val inventoryDao: InventoryDao
) {
    fun getAvatar(): Flow<Avatar?> = avatarDao.getAvatar()
    suspend fun getAvatarOnce(): Avatar? = avatarDao.getAvatarOnce()
    fun getShopItems(): Flow<List<ShopItem>> = shopDao.getAllItems()
    fun getInventory(): Flow<List<Inventory>> = inventoryDao.getAll()
    fun getEquippedItems(): Flow<List<Inventory>> = inventoryDao.getEquippedItems()

    suspend fun createAvatar(avatar: Avatar): Long = avatarDao.insert(avatar)
    suspend fun updateAvatar(avatar: Avatar) = avatarDao.update(avatar)

    suspend fun addRewards(xp: Int, gold: Int) {
        val avatar = avatarDao.getAvatarOnce() ?: return
        avatarDao.addRewards(avatar.id, xp, gold)
        val updated = avatarDao.getAvatarOnce() ?: return
        if (updated.currentXp >= updated.xpToNextLevel) {
            avatarDao.levelUp(updated.id)
        }
    }

    suspend fun spendGold(amount: Int): Boolean {
        val avatar = avatarDao.getAvatarOnce() ?: return false
        val rows = avatarDao.spendGold(avatar.id, amount)
        return rows > 0
    }

    suspend fun buyItem(shopItemId: Long): Boolean {
        val item = shopDao.getItemById(shopItemId) ?: return false
        if (!spendGold(item.price)) return false
        inventoryDao.insert(Inventory(shopItemId = shopItemId))
        return true
    }

    suspend fun equipItem(inventoryId: Long) {
        inventoryDao.unequipAll()
        inventoryDao.equip(inventoryId)
    }

    suspend fun unequipItem(inventoryId: Long) {
        inventoryDao.unequip(inventoryId)
    }

    suspend fun isItemOwned(shopItemId: Long): Boolean {
        return inventoryDao.countOwned(shopItemId) > 0
    }

    suspend fun seedShopItems() {
        shopDao.insertItems(
            listOf(
                ShopItem(name = "Topi Pelajar", description = "Topi biasa untuk pelajar", price = 50, type = "head", iconRes = "hat"),
                ShopItem(name = "Mahkota Emas", description = "Mahkota untuk sang juara", price = 200, type = "head", iconRes = "crown"),
                ShopItem(name = "Baju Rantai", description = "Baju zirah ringan", price = 100, type = "body", iconRes = "chainmail"),
                ShopItem(name = "Jubah Sage", description = "Jubah penuh kebijaksanaan", price = 300, type = "body", iconRes = "robe"),
                ShopItem(name = "Pedang Kayu", description = "Pedang latihan dasar", price = 75, type = "weapon", iconRes = "wood_sword"),
                ShopItem(name = "Tongkat Sihir", description = "Tongkat untuk belajar lebih cepat", price = 250, type = "weapon", iconRes = "staff")
            )
        )
    }
}
