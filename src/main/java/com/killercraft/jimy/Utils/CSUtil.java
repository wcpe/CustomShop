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

    public static void openShop(Player player, String shopName) {
        shopName = shopName.replace('&', ChatColor.COLOR_CHAR);
        if (customShops.containsKey(shopName)) {
            GuiShop gs = customShops.get(shopName);
            boolean refresh = false;
            if (refreshShops.containsKey(shopName)) {
                int rday = refreshShops.get(shopName);
                if (day != rday) {
                    refreshShops.put(shopName, day);
                    refresh = true;
                }
            } else {
                refreshShops.put(shopName, day);
                refresh = true;
            }
            if (refresh) {
                gs.refresh();
            }
            gs.openShop(player);
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
            boolean refresh = false;
            if (refreshShops.containsKey(shopName)) {
                int rday = refreshShops.get(shopName);
                if (day != rday) {
                    refreshShops.put(shopName, day);
                    refresh = true;
                }
            } else {
                refreshShops.put(shopName, day);
                refresh = true;
            }
            if (refresh) {
                gs.refresh();
            }
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
            GuiShop gs = customShops.get(shopName);
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
        if (customShops.containsKey(shopName)) {
            GuiShop gs = customShops.get(shopName);
            HashMap<Integer, ItemStack> items = new HashMap<>();
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (stack != null && stack.hasItemMeta()) {
                    items.put(i, inv.getItem(i));
                }
            }
            gs.setItems(items);
            customShops.put(shopName, gs);
        }
    }

    public static void clickShop(String title, Player player, int slot) {
        if (InventoryUtil.getContentsEmptySlot(player.getInventory()) < 1) {
            player.sendMessage(langMap.get("inventory-full"));
            return;
        }
        GuiShop gs = customShops.get(title);
        HashMap<Integer, ItemStack> items = gs.getItems();
        if (items.containsKey(slot)) {
            ItemStack stack = items.get(slot);
            if (stack != null && stack.hasItemMeta()) {
                ItemMeta oMeta = stack.getItemMeta();
                if (!oMeta.hasLore()) {
                    return;
                }
                List<String> lores = oMeta.getLore();
                boolean isBuy = false;
                List<String> takeList = new ArrayList<>();
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
                        } else if (buy[1].equalsIgnoreCase("item_lore")) {
                            isBuy = checkItemAndLoreNeed(buy[2], buy[4], Integer.parseInt(buy[3]), player);
                            takeList.add(lore);
                        } else if (buy[1].equals("lore")) {
                            isBuy = checkItemLoreNeed(buy[2], Integer.parseInt(buy[3]), player);
                            takeList.add(lore);
                        } else if (buy[1].equals("cost")) {
                            isBuy = checkCostNeed(buy[2], Integer.parseInt(buy[3]), player);
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
                        if (check[1].equals("perm")) {
                            isBuy = player.hasPermission(check[2]);
                        } else if (check[1].equals("inv")) {
                            int ii = Integer.parseInt(check[2]);
                            isBuy = checkInvNeed(ii, player);
                        } else if (check[1].equals("papi")) {
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
                        } else if (check[1].equals("str")) {
                            String a = PlaceholderAPI.setPlaceholders(player, check[3]);
                            String b = PlaceholderAPI.setPlaceholders(player, check[4]);
                            if (check[2].equals("y")) {
                                isBuy = a.equals(b);
                            } else if (check[2].equals("n")) {
                                isBuy = !a.equals(b);
                            }
                        }
                        if (!isBuy) {
                            break;
                        }
                    }
                }
                if (isBuy) {
                    boolean noLimit = false;
                    boolean take = false;
                    ItemStack aStack = stack.clone();
                    ItemMeta meta = aStack.getItemMeta();
                    List<String> aLores = meta.getLore();
                    for (int i = 0; i < aLores.size(); i++) {
                        String aLore = aLores.get(i);
                        if (aLore.contains("<player>")) {
                            aLores.set(i, aLore.replace("<player>", player.getName()));
                        } else if (aLore.contains("<uuid>")) {
                            aLores.set(i, aLore.replace("<uuid>", player.getUniqueId().toString()));
                        }
                    }
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
                            if (setting[1].equals("close")) {
                                player.closeInventory();
                            } else if (setting[1].equals("open")) {
                                openShop(player, setting[2]);
                            } else if (setting[1].equals("limit")) {
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
                            } else if (setting[1].equals("daylimit")) {
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
                            } else if (setting[1].equals("playerlimit")) {
                                player.closeInventory();
                                int a = Integer.parseInt(setting[3]) - getPlayerKeyLimit(player.getName(), setting[2]);
                                if (a <= 0) {
                                    player.sendMessage(langMap.get("NoStock"));
                                    take = true;
                                    noLimit = true;
                                } else {
                                    takePlayerKeyLimit(player.getName(), setting[2], 1);
                                }
                            } else if (setting[1].equals("cmd")) {
                                if (!noLimit) {
                                    if (setting[2].equals("c")) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setting[3].replace("<player>", player.getName()).replace("<uuid>", player.getUniqueId().toString()));
                                    } else if (setting[2].equals("p")) {
                                        player.chat("/" + setting[3].replace("<player>", player.getName()).replace("<uuid>", player.getUniqueId().toString()));
                                    } else if (setting[2].equals("o")) {
                                        player.setOp(true);
                                        try {
                                            player.chat("/" + setting[3].replace("<player>", player.getName()).replace("<uuid>", player.getUniqueId().toString()));
                                        } catch (Throwable ignored) {
                                        }
                                        player.setOp(false);
                                    }
                                }
                            } else if (setting[1].equals("take")) {
                                take = true;
                            }
                        }
                        i++;
                    }
                    if (!noLimit) {
                        for (String takes : takeList) {
                            String[] buy = takes.split("~");
                            if (buy[1].equals("eco")) {
                                takeEcoNeed(Integer.parseInt(buy[2]), player);
                            } else if (buy[1].equals("point")) {
                                takePointNeed(Integer.parseInt(buy[2]), player);
                            } else if (buy[1].equals("item")) {
                                takeItemNeed(buy[2], Integer.parseInt(buy[3]), player);
                            } else if (buy[1].equals("item_lore")) {
                                takeItemAndLoreNeed(buy[2], buy[4], Integer.parseInt(buy[3]), player);
                            } else if (buy[1].equals("lore")) {
                                takeItemLoreNeed(buy[2], Integer.parseInt(buy[3]), player);
                            } else if (buy[1].equals("cost")) {
                                takeCostNeed(buy[2], Integer.parseInt(buy[3]), player);
                            } else if (buy[1].equals("id")) {
                                if (buy[2].contains(":")) {
                                    String[] ds = buy[2].split(":");
                                    int ids = Integer.parseInt(ds[0]);
                                    int durs = Integer.parseInt(ds[1]);
                                    takeIdNeed(ids, durs, Integer.parseInt(buy[4]), player);
                                } else {
                                    takeIdNeed(Integer.parseInt(buy[2]), -1, Integer.parseInt(buy[4]), player);
                                }
                            }
                        }
                        if (changeMap.size() >= 1) {
                            for (int ci : changeMap.keySet()) {
                                lores.set(ci, changeMap.get(ci));
                            }
                            oMeta.setLore(lores);
                            stack.setItemMeta(oMeta);
                            items.put(slot, stack);
                            gs.setItems(items);
                        }
                    }
                    if (!take) {
                        HashMap<Integer, ItemStack> loseItems = player.getInventory().addItem(aStack);
                        player.sendMessage(langMap.get("BuyOK"));
                        if (loseItems.size() >= 1) {
                            for (ItemStack stack1 : loseItems.values()) {
                                player.getWorld().dropItem(player.getLocation(), stack1);
                            }
                            player.sendMessage(langMap.get("MaxItem"));
                        }
                    }
                } else {
                    player.sendMessage(langMap.get("NoNeed"));
                }
            }
        }
    }
}