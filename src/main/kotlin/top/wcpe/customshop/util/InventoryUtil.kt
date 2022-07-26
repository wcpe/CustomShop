package top.wcpe.customshop.util

import org.bukkit.Material
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
