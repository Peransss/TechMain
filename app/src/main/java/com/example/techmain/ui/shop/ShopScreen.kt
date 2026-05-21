package com.example.techmain.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.data.db.entity.Avatar
import com.example.techmain.data.db.entity.Inventory
import com.example.techmain.data.db.entity.ShopItem
import com.example.techmain.ui.theme.RPGQuestGold
import com.example.techmain.ui.theme.RpgXpGreen

@Composable
fun ShopScreen(viewModel: ShopViewModel = viewModel()) {
    val avatar by viewModel.avatar.collectAsState(initial = null)
    val shopItems by viewModel.shopItems.collectAsState(initial = emptyList())
    val inventory by viewModel.inventory.collectAsState(initial = emptyList())
    val isInitialized by viewModel.isInitialized.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (!isInitialized) viewModel.initialize()
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (!isInitialized || avatar == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Memuat...")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { AvatarProfile(avatar = avatar!!, viewModel = viewModel) }
                item {
                    Text("Inventory", style = MaterialTheme.typography.titleLarge)
                    if (inventory.isEmpty()) {
                        Text("Belum ada item", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        InventorySection(inventory = inventory, viewModel = viewModel)
                    }
                }
                item {
                    Text("Toko", style = MaterialTheme.typography.titleLarge)
                }
                items(shopItems, key = { it.id }) { item ->
                    ShopItemCard(item = item, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AvatarProfile(avatar: Avatar, viewModel: ShopViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(64.dp), tint = RPGQuestGold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(avatar.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Level ${avatar.level}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("XP", style = MaterialTheme.typography.labelSmall)
                    Text("${avatar.currentXp}/${avatar.xpToNextLevel}", color = RpgXpGreen, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gold", style = MaterialTheme.typography.labelSmall)
                    Text("${avatar.gold}", color = RPGQuestGold, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            val equipment = listOfNotNull(
                avatar.equippedHead?.let { "Head: $it" },
                avatar.equippedBody?.let { "Body: $it" },
                avatar.equippedWeapon?.let { "Weapon: $it" }
            )
            if (equipment.isNotEmpty()) {
                equipment.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
fun InventorySection(inventory: List<Inventory>, viewModel: ShopViewModel) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(inventory, key = { it.id }) { inv ->
            Card(
                modifier = Modifier.width(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (inv.isEquipped) RpgXpGreen.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(32.dp))
                    Text("Item #${inv.shopItemId}", style = MaterialTheme.typography.labelSmall)
                    if (inv.isEquipped) {
                        Text("Equipped", color = RpgXpGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    FilledTonalButton(
                        onClick = {
                            if (inv.isEquipped) viewModel.unequipItem(inv)
                            else viewModel.equipItem(inv)
                        },
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(if (inv.isEquipped) "Lepas" else "Pakai", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemCard(item: ShopItem, viewModel: ShopViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(item.description, style = MaterialTheme.typography.bodySmall)
                Text("Tipe: ${item.type}", style = MaterialTheme.typography.labelSmall)
            }
            Button(onClick = { viewModel.buyItem(item) }) {
                Text("${item.price}G")
            }
        }
    }
}
