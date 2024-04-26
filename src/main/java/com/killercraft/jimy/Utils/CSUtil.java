package com.killercraft.jimy.Utils;

import com.killercraft.jimy.Manager.GuiShop;
import com.killercraft.jimy.Utils.nms.CSHelper;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.wcpe.customshop.PurchaseLimit;
import top.wcpe.customshop.util.InventoryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.killercraft.jimy.ConfigManager.CSConfig.update;
import static com.killercraft.jimy.ConfigManager.CSDataUtil.*;
import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.Utils.CSItemUtil.*;

public class CSUtil {

    public static HashMap<String, GuiShop> getShops() {
        return customShops;
    }

    public static void sendList(Player player, String s) {
        if (!player.isOp()) return;
        player.sendMessage(langMap.get("ShopList"));
        String title = ChatColor.WHITE + ">> ";
        if (s != null) {
            for (String name : customShops.keySet()) {
                if (name.contains(s)) {
                    CSHelper.sendChat(player, title, name, "§a <<点击编辑", "§9点击编辑: §f" + name + " §9商店");
                }
            }
        } else {
            for (String name : customShops.keySet()) {
                CSHelper.sendChat(player, title, name, "§a <<点击编辑", "§9点击编辑: §f" + name + " §9商店");
            }
        }
    }

    public static void sendCostList(CommandSender player) {
        String costTitle = langMap.get("CostTitle");
        String costName = langMap.get("CostName");
        player.sendMessage(ChatColor.GREEN + "=============CustomShop=============");
        for (String costId : costMap.keySet()) {
            player.sendMessage(costTitle + costId + " " + costName + costMap.get(costId));
        }
        player.sendMessage(ChatColor.GREEN + "=============CustomShop=============");
    }

    /**
     * 打开指定名称的商店
     *
     * @param player   玩家对象
     * @param shopName 商店名称
     */
    public static void openShop(Player player, String shopName) {
        shopName = shopName.replace('&', ChatColor.COLOR_CHAR);

        // 检查是否存在指定名称的商店
        if (customShops.containsKey(shopName)) {
            GuiShop guiShop = customShops.get(shopName);
            refreshShopIfNeeded(guiShop, shopName);
            guiShop.openShop(player);
        } else {
            player.sendMessage(langMap.get("NoShop"));
        }
    }


    public static void openEditInv(Player player, String shopName) {
        if (!player.isOp()) {
            return;
        }
        shopName = shopName.replace('&', ChatColor.COLOR_CHAR);
        if (customShops.containsKey(shopName)) {
            GuiShop gs = customShops.get(shopName);
            gs.openEditInv(player);
        } else {
            player.sendMessage(langMap.get("NoShop"));
        }
    }

    /**
     * 如果需要，刷新商店
     *
     * @param guiShop  商店对象
     * @param shopName 商店名称
     */
    private static void refreshShopIfNeeded(GuiShop guiShop, String shopName) {
        boolean refresh = false;
        if (refreshShops.containsKey(shopName)) {
            int refreshDay = refreshShops.get(shopName);
            if (day != refreshDay) {
                refreshShops.put(shopName, day);
                refresh = true;
            }
        } else {
            refreshShops.put(shopName, day);
            refresh = true;
        }
        if (refresh) {
            guiShop.refresh();
        }
    }

    /**
     * 刷新指定名称的商店
     *
     * @param player   玩家对象
     * @param shopName 商店名称
     * @param force    是否强制刷新
     */
    public static void refreshShop(Player player, String shopName, boolean force) {
        if (!player.isOp()) {
            return;
        }
        shopName = shopName.replace('&', ChatColor.COLOR_CHAR);
        if (customShops.containsKey(shopName)) {
            GuiShop gs = customShops.get(shopName);
            if (force) {
                gs.refresh();
                player.sendMessage(langMap.get("ShopRefresh"));
                return;
            }
            refreshShopIfNeeded(gs, shopName);
            player.sendMessage(langMap.get("ShopRefresh"));
        } else {
            player.sendMessage(langMap.get("NoShop"));
        }
    }

    public static void createShop(Player player, String[] args) {
        if (!player.isOp()) return;
        args[1] = args[1].replace('&', ChatColor.COLOR_CHAR);
        int line = Integer.parseInt(args[2]);
        if (line >= 1 && line <= 6) {
            if (!customShops.containsKey(args[1])) {
                GuiShop gs = new GuiShop(line, args[1]);
                customShops.put(args[1], gs);
                player.sendMessage(langMap.get("ShopCreate"));
            } else player.sendMessage(langMap.get("AlreadyCreate"));
        } else player.sendMessage(langMap.get("LineNull"));
    }

    public static void deleteShop(Player player, String shopName) {
        if (!player.isOp()) return;
        shopName = shopName.replace('&', ChatColor.COLOR_CHAR);
        if (customShops.containsKey(shopName)) {
            customShops.remove(shopName);
            refreshShops.remove(shopName);
            cancelSet.remove(shopName);
            player.sendMessage(langMap.get("ShopDelete"));
        } else player.sendMessage(langMap.get("NoShop"));
    }

    public static void sendCosts(Player player) {
        player.sendMessage(langMap.get("LeftCost"));
        String pName = player.getName();
        if (playerData.containsKey(pName)) {
            HashMap<String, Integer> pCosts = playerData.get(pName);
            for (String id : pCosts.keySet()) {
                if (costMap.containsKey(id)) {
                    player.sendMessage(costMap.get(id) + ChatColor.GOLD + ">>: " + ChatColor.BLUE + pCosts.get(id));
                }
            }
        } else {
            player.sendMessage(langMap.get("NoCost"));
        }
    }

    public static void reloadConfig(CommandSender sender) {
        if (!sender.isOp()) {
            return;
        }
        saveData();
        saveShops();
        saveRefresh();
        update();
        sender.sendMessage("[CustomShop]Reload Config!");
    }


    public static void closeShop(String shopName, Inventory inv) {
        if (!customShops.containsKey(shopName)) {
            return;
        }

        GuiShop gs = customShops.get(shopName);
        HashMap<Integer, ItemStack> items = new HashMap<>();

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack != null && stack.hasItemMeta()) {
                items.put(i, stack.clone());
            }
        }

        gs.setItems(items);
        customShops.put(shopName, gs);
    }


    public static void clickShop(String title, Player player, int slot) {
        if (InventoryUtil.getContentsEmptySlot(player.getInventory()) < 1) {
            player.sendMessage(langMap.get("inventory-full"));
            return;
        }
        GuiShop guiShop = customShops.get(title);
        HashMap<Integer, ItemStack> items = guiShop.getItems();
        if (!items.containsKey(slot)) {
            return;
        }
        ItemStack itemStack = items.get(slot);
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!itemMeta.hasLore()) {
            return;
        }
        List<String> lores = itemMeta.getLore();
        boolean isBuy = false;
        List<String> takeList = new ArrayList<>();

        isBuy = isBuy(player, lores, isBuy, takeList);

        if (!isBuy) {
            player.sendMessage(langMap.get("NoNeed"));
            return;
        }

        boolean noLimit = false;
        boolean take = false;
        ItemStack aStack = itemStack.clone();
        ItemMeta meta = aStack.getItemMeta();
        List<String> aLores = meta.getLore();
        replaceLoreVar(player, aLores);
        aLores.removeIf(aLore -> aLore.startsWith("b~") || aLore.startsWith("m~") || aLore.startsWith("s~") || aLore.startsWith("c~"));
        if (aLores.isEmpty()) {
            meta.setLore(null);
        } else {
            meta.setLore(aLores);
        }
        aStack.setItemMeta(meta);
        HashMap<Integer, String> changeMap = new HashMap<>();
        int i = 0;
        for (String lore : lores) {
            if (lore.startsWith("s~")) {
                String[] setting = lore.split("~");
                switch (setting[1]) {
                    case "close":
                        player.closeInventory();
                        break;
                    case "open":
                        openShop(player, setting[2]);
                        break;
                    case "limit": {
                        player.closeInventory();
                        int limit = Integer.parseInt(setting[2]);
                        if (limit <= 0) {
                            player.sendMessage(langMap.get("NoStock"));
                            take = true;
                            noLimit = true;
                        } else {
                            limit -= 1;
                            changeMap.put(i, "s~limit~" + limit);
                        }
                        break;
                    }
                    case "daylimit": {
                        player.closeInventory();
                        int limit = Integer.parseInt(setting[3]);
                        if (limit <= 0) {
                            player.sendMessage(langMap.get("NoStock"));
                            take = true;
                            noLimit = true;
                        } else {
                            limit -= 1;
                            changeMap.put(i, "s~daylimit~" + setting[2] + "~" + limit);
                        }
                        break;
                    }
                    case "playerlimit":
                        player.closeInventory();
                        int a = Integer.parseInt(setting[3]) - getPlayerKeyLimit(player.getName(), setting[2]);
                        if (a <= 0) {
                            player.sendMessage(langMap.get("NoStock"));
                            take = true;
                            noLimit = true;
                        } else {
                            takePlayerKeyLimit(player.getName(), setting[2], 1);
                        }
                        break;
                    case "player_week_limit":
                        player.closeInventory();
                        PurchaseLimit purchaseLimit = getInstance().getPurchaseLimit();
                        int number = Integer.parseInt(setting[3]) - purchaseLimit.getPurchaseLimit(player.getName(), setting[2], PurchaseLimit.LimitType.WEEKLY);
                        if (number <= 0) {
                            player.sendMessage(langMap.get("NoStock"));
                            take = true;
                            noLimit = true;
                        } else {
                            purchaseLimit.addPurchaseRecord(player.getName(), setting[2]);
                        }
                        break;
                    case "cmd":
                        if (!noLimit) {
                            switch (setting[2]) {
                                case "c":
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setting[3].replace("<player>", player.getName()).replace("<uuid>", player.getUniqueId().toString()));
                                    break;
                                case "p":
                                    player.chat("/" + setting[3].replace("<player>", player.getName()).replace("<uuid>", player.getUniqueId().toString()));
                                    break;
                                case "o":
                                    player.setOp(true);
                                    try {
                                        player.chat("/" + setting[3].replace("<player>", player.getName()).replace("<uuid>", player.getUniqueId().toString()));
                                    } catch (Throwable ignored) {
                                    }
                                    player.setOp(false);
                                    break;
                            }
                        }
                        break;
                    case "take":
                        take = true;
                        break;
                }
            }
            i++;
        }
        if (!noLimit) {
            for (String takes : takeList) {
                String[] buy = takes.split("~");
                switch (buy[1]) {
                    case "eco":
                        takeEcoNeed(Integer.parseInt(buy[2]), player);
                        break;
                    case "point":
                        takePointNeed(Integer.parseInt(buy[2]), player);
                        break;
                    case "item":
                        takeItemNeed(buy[2], Integer.parseInt(buy[3]), player, false);
                        break;
                    case "item_contains_name":
                        takeItemNeed(buy[2], Integer.parseInt(buy[3]), player, true);
                        break;
                    case "item_lore":
                        takeItemAndLoreNeed(buy[2], buy[4], Integer.parseInt(buy[3]), player);
                        break;
                    case "lore":
                        takeItemLoreNeed(buy[2], Integer.parseInt(buy[3]), player);
                        break;
                    case "cost":
                        takeCostNeed(buy[2], Integer.parseInt(buy[3]), player);
                        break;
                    case "item_id":
                        if (buy[2].contains(":")) {
                            String[] ds = buy[2].split(":");
                            int ids = Integer.parseInt(ds[0]);
                            int durs = Integer.parseInt(ds[1]);
                            takeOnlyIdNeed(ids, durs, Integer.parseInt(buy[4]), player);
                        } else {
                            takeOnlyIdNeed(Integer.parseInt(buy[2]), -1, Integer.parseInt(buy[4]), player);
                        }
                        break;
                    case "id":
                        if (buy[2].contains(":")) {
                            String[] ds = buy[2].split(":");
                            int ids = Integer.parseInt(ds[0]);
                            int durs = Integer.parseInt(ds[1]);
                            takeIdNeed(ids, durs, Integer.parseInt(buy[4]), player);
                        } else {
                            takeIdNeed(Integer.parseInt(buy[2]), -1, Integer.parseInt(buy[4]), player);
                        }
                        break;
                }
            }
            if (!changeMap.isEmpty()) {
                for (int ci : changeMap.keySet()) {
                    lores.set(ci, changeMap.get(ci));
                }
                itemMeta.setLore(lores);
                itemStack.setItemMeta(itemMeta);
                items.put(slot, itemStack);
                guiShop.setItems(items);
            }
        }
        if (!take) {
            HashMap<Integer, ItemStack> loseItems = player.getInventory().addItem(aStack);
            player.sendMessage(langMap.get("BuyOK"));
            if (!loseItems.isEmpty()) {
                for (ItemStack stack1 : loseItems.values()) {
                    player.getWorld().dropItem(player.getLocation(), stack1);
                }
                player.sendMessage(langMap.get("MaxItem"));
            }
        }
    }


    private static boolean isBuy(Player player, List<String> lores, boolean isBuy, List<String> takeList) {
        for (String lore : lores) {
            if (lore.startsWith("b~")) {
                String[] buy = lore.split("~");
                if (buy[1].equals("eco")) {
                    isBuy = checkEcoNeed(Integer.parseInt(buy[2]), player);
                    takeList.add(lore);
                } else if (buy[1].equals("point")) {
                    isBuy = checkPointNeed(Integer.parseInt(buy[2]), player);
                    takeList.add(lore);
                } else if (buy[1].equals("item")) {
                    isBuy = checkItemNeed(buy[2], Integer.parseInt(buy[3]), player);
                    takeList.add(lore);
                } else if (buy[1].equals("item_contains_name")) {
                    isBuy = checkItemContainsNameNeed(buy[2], Integer.parseInt(buy[3]), player);
                    takeList.add(lore);
                } else if (buy[1].equalsIgnoreCase("item_lore")) {
                    isBuy = checkItemAndLoreNeed(buy[2], buy[4], Integer.parseInt(buy[3]), player);
                    takeList.add(lore);
                } else if (buy[1].equals("lore")) {
                    isBuy = checkItemLoreNeed(buy[2], Integer.parseInt(buy[3]), player);
                    takeList.add(lore);
                } else if (buy[1].equals("cost")) {
                    isBuy = checkCostNeed(buy[2], Integer.parseInt(buy[3]), player);
                    takeList.add(lore);
                } else if (buy[1].equals("item_id")) {
                    if (buy[2].contains(":")) {
                        String[] ds = buy[2].split(":");
                        int ids = Integer.parseInt(ds[0]);
                        int durs = Integer.parseInt(ds[1]);
                        isBuy = checkOnlyIdNeed(ids, durs, Integer.parseInt(buy[4]), player);
                    } else {
                        isBuy = checkOnlyIdNeed(Integer.parseInt(buy[2]), -1, Integer.parseInt(buy[4]), player);
                    }
                    takeList.add(lore);
                } else if (buy[1].equals("id")) {
                    if (buy[2].contains(":")) {
                        String[] ds = buy[2].split(":");
                        int ids = Integer.parseInt(ds[0]);
                        int durs = Integer.parseInt(ds[1]);
                        isBuy = checkIdNeed(ids, durs, Integer.parseInt(buy[4]), player);
                    } else {
                        isBuy = checkIdNeed(Integer.parseInt(buy[2]), -1, Integer.parseInt(buy[4]), player);
                    }
                    takeList.add(lore);
                } else if (buy[1].equals("perm")) {
                    isBuy = player.hasPermission(buy[2]);
                } else if (buy[1].equals("none")) {
                    isBuy = true;
                } else if (buy[1].equals("show")) {
                    isBuy = false;
                }
                if (!isBuy) {
                    break;
                }
            } else if (lore.startsWith("c~")) {
                String[] check = lore.split("~");
                switch (check[1]) {
                    case "perm":
                        isBuy = player.hasPermission(check[2]);
                        break;
                    case "inv":
                        int ii = Integer.parseInt(check[2]);
                        isBuy = checkInvNeed(ii, player);
                        break;
                    case "papi":
                        String papi = PlaceholderAPI.setPlaceholders(player, check[2]);
                        double d = 0;
                        try {
                            d = Double.parseDouble(papi);
                        } catch (Throwable throwable) {
                            isBuy = false;
                        }
                        if (isBuy) {
                            double b = Double.parseDouble(check[3]);
                            isBuy = !(d < b);
                        }
                        break;
                    case "str":
                        String a = PlaceholderAPI.setPlaceholders(player, check[3]);
                        String b = PlaceholderAPI.setPlaceholders(player, check[4]);
                        if (check[2].equals("y")) {
                            isBuy = a.equals(b);
                        } else if (check[2].equals("n")) {
                            isBuy = !a.equals(b);
                        }
                        break;
                }
                if (!isBuy) {
                    break;
                }
            }
        }
        return isBuy;
    }

    public static void replaceLoreVar(Player player, List<String> aLores) {
        for (int i = 0; i < aLores.size(); i++) {
            String aLore = aLores.get(i);
            if (aLore.contains("<player>")) {
                aLores.set(i, aLore.replace("<player>", player.getName()));
            } else if (aLore.contains("<uuid>")) {
                aLores.set(i, aLore.replace("<uuid>", player.getUniqueId().toString()));
            }
        }
    }
}