package top.wcpe.customshop

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

/**
 * 由 WCPE 在 2024/4/8 10:01 创建
 * <p>
 * Created by WCPE on 2024/4/8 10:01
 * <p>
 * <p>
 * GitHub  : <a href="https://github.com/wcpe">wcpe 's GitHub</a>
 * <p>
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.4.0-SNAPSHOT
 */


class PurchaseLimit(private val dataFile: File) {
    private val playerLimits = mutableMapOf<String, MutableMap<String, MutableMap<LimitType, Int>>>() // 玩家购买限制的映射
    private var lastCheckedDay: String
    private var lastCheckedWeek: Int
    private var lastCheckedMonth: String

    init {
        val yaml = YamlConfiguration.loadConfiguration(dataFile)
        val now = LocalDate.now()
        lastCheckedDay = yaml.getString("last-checked-day", "$now")
        val weekFields = WeekFields.of(Locale.getDefault())
        val weekNumber = now.get(weekFields.weekOfWeekBasedYear())
        lastCheckedWeek = yaml.getInt("last-checked-week", weekNumber)
        val currentYearMonth = YearMonth.now()
        val formattedYearMonth = currentYearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        lastCheckedMonth = yaml.getString("last-checked-month", formattedYearMonth)
        loadPurchaseLimits(yaml)
    }


    enum class LimitType {
        DAILY, WEEKLY, MONTHLY
    }

    private fun loadPurchaseLimits(yaml: YamlConfiguration) {
        if (!dataFile.exists()) {
            return
        }

        playerLimits.clear()
        val playerLimitsConfig = yaml.getConfigurationSection("player-limits") ?: return
        for (temp in playerLimitsConfig.getKeys(false)) {
            val limitMap = mutableMapOf<String, MutableMap<LimitType, Int>>()
            val sec = playerLimitsConfig.getConfigurationSection(temp) ?: continue
            for (key in sec.getKeys(false)) {
                val itemTypeLimits = mutableMapOf<LimitType, Int>()
                for (type in LimitType.values()) {
                    val limitValue = sec.getInt("$key.${type.name}", 0)
                    itemTypeLimits[type] = limitValue
                }
                limitMap[key] = itemTypeLimits
            }
            playerLimits[temp] = limitMap
        }
    }

    private fun savePurchaseLimits() {
        val yaml = YamlConfiguration()
        yaml["last-checked-day"] = lastCheckedDay
        yaml["last-checked-week"] = lastCheckedWeek
        yaml["last-checked-month"] = lastCheckedMonth
        for (playerName in playerLimits.keys) {
            val limitMap = playerLimits[playerName] ?: continue
            for (itemName in limitMap.keys) {
                val itemTypeLimits = limitMap[itemName] ?: continue
                for (type in itemTypeLimits.keys) {
                    val limitValue = itemTypeLimits[type] ?: continue
                    yaml.set("player-limits.$playerName.$itemName.${type.name}", limitValue)
                }
            }
        }
        try {
            yaml.save(dataFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * 获取特定玩家对特定物品的购买限制次数。
     * @param playerName 玩家名字
     * @param itemKey 物品的唯一标识符
     * @param limitType 限制类型，可选值为 LimitType 中的枚举值
     * @return 购买限制次数，如果未设置返回0
     */
    fun getPurchaseLimit(playerName: String, itemKey: String, limitType: LimitType): Int {
        isLastCheckedDataMismatch()
        return playerLimits[playerName]?.getOrDefault(itemKey, mutableMapOf())?.getOrDefault(limitType, 0) ?: 0
    }

    /**
     * 设置特定玩家对特定物品的购买限制次数。
     * @param playerName 玩家名字
     * @param itemKey 物品的唯一标识符
     * @param limitType 限制类型，可选值为 LimitType 中的枚举值
     * @param limit 购买限制次数
     */
    fun setPurchaseLimit(playerName: String, itemKey: String, limitType: LimitType, limit: Int) {
        val limitsMap = playerLimits.getOrPut(playerName) { mutableMapOf() }
        val itemTypeLimits = limitsMap.getOrPut(itemKey) { mutableMapOf() }
        itemTypeLimits[limitType] = limit
        savePurchaseLimits()
    }

    fun addPurchaseRecord(playerName: String, itemName: String) {
        isLastCheckedDataMismatch()
        addPurchaseRecord(playerName, itemName, LimitType.DAILY)
        addPurchaseRecord(playerName, itemName, LimitType.WEEKLY)
        addPurchaseRecord(playerName, itemName, LimitType.MONTHLY)
    }

    fun addPurchaseRecord(playerName: String, itemName: String, type: LimitType) {
        val limitMap = playerLimits.getOrPut(playerName) { mutableMapOf() }
        val itemTypeLimits = limitMap.getOrPut(itemName) { mutableMapOf() }
        val currentLimit = itemTypeLimits.getOrDefault(type, 0)
        itemTypeLimits[type] = currentLimit + 1
        savePurchaseLimits()
    }


    fun resetTypeLimits(type: LimitType) {
        for ((_, limitsMap) in playerLimits) {
            for ((_, itemTypeLimits) in limitsMap) {
                itemTypeLimits[type] = 0
            }
        }
        savePurchaseLimits()
    }

    // 检查上次检查的日期、星期和月份是否与当前时间不匹配
    private fun isLastCheckedDataMismatch() {
        val now = LocalDate.now()

        // 检查日期
        val formattedToday = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        if (lastCheckedDay != formattedToday) {
            lastCheckedDay = formattedToday
            resetTypeLimits(LimitType.DAILY)
        }

        // 检查星期
        val weekFields = WeekFields.of(Locale.getDefault())
        val thisWeek = now.get(weekFields.weekOfWeekBasedYear())
        if (lastCheckedWeek != thisWeek) {
            lastCheckedWeek = thisWeek
            resetTypeLimits(LimitType.WEEKLY)
        }

        // 检查月份
        val currentYearMonth = YearMonth.now()
        val formattedYearMonth = currentYearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        if (lastCheckedMonth != formattedYearMonth) {
            lastCheckedMonth = formattedYearMonth
            resetTypeLimits(LimitType.MONTHLY)
        }

    }

}
