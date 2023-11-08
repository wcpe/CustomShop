package top.wcpe.customshop.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

/**
 * 由 WCPE 在 2022/7/22 3:08 创建
 *
 * Created by WCPE on 2022/7/22 3:08
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.0.4-alpha-dev-2
 */
object InventoryUtil {

    @JvmStatic
    fun checkItem(
        item: ItemStack, lore: String, takeAmount: Int, takeCallback: (ItemStack) -> Unit
    ): Boolean {
        if (!item.hasItemMeta()) return false
        val itemMeta = item.itemMeta
        if (!itemMeta.hasLore()) return false
        val l = itemMeta.lore
        val amount = item.amount
        for (s in l) {
            if (s.contains(lore)) {
                takeCallback(
                    if (amount > takeAmount) {
                        item.amount = amount - takeAmount
                        item
                    } else {
                        ItemStack(Material.AIR)
                    }
                )
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun clearInventoryItem(player: Player, lore: String, takeAmount: Int) {
        val inventory = player.inventory
        for (i in 0 until inventory.size) {
            val item = inventory.getItem(i) ?: continue
            if (checkItem(item, lore, takeAmount) { itemStack ->
                    inventory.setItem(i, itemStack)
                }) {
                return
            }
        }
        inventory.itemInOffHand?.let {
            if (checkItem(it, lore, takeAmount) { itemStack ->
                    inventory.itemInOffHand = itemStack
                }) {
                return
            }
        }


        inventory.helmet?.let {
            if (checkItem(it, lore, takeAmount) { itemStack ->
                    inventory.helmet = itemStack
                }) {
                return
            }
        }

        inventory.chestplate?.let {
            if (checkItem(it, lore, takeAmount) { itemStack ->
                    inventory.chestplate = itemStack
                }) {
                return
            }
        }

        inventory.leggings?.let {
            if (checkItem(it, lore, takeAmount) { itemStack ->
                    inventory.leggings = itemStack
                }) {
                return
            }
        }

        inventory.boots?.let {
            if (checkItem(it, lore, takeAmount) { itemStack ->
                    inventory.boots = itemStack
                }) {
                return
            }
        }
    }

    @JvmStatic
    fun getContentsEmptySlot(inventory: PlayerInventory): Int {
        var emptySlotNumber = 0
        for (i in 0 until inventory.size) {
            inventory.getItem(i) ?: emptySlotNumber++
        }

        val off = inventory.itemInOffHand
        if (off == null || off.type == Material.AIR) {
            emptySlotNumber--
        }
        inventory.helmet ?: emptySlotNumber--
        inventory.chestplate ?: emptySlotNumber--
        inventory.leggings ?: emptySlotNumber--
        inventory.boots ?: emptySlotNumber--
        return emptySlotNumber
    }
}
