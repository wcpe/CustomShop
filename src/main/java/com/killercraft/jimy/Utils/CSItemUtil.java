package com.killercraft.jimy.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CustomShopDatabase.enableMySQL;

public class CSItemUtil {
    public static boolean checkItemNeed(String name,int need,Player player){
        Inventory inv = player.getInventory();
        int i = 0;
        for(ItemStack stack:inv){
            if(stack != null && stack.hasItemMeta()){
                ItemMeta meta = stack.getItemMeta();
                if(meta.hasDisplayName()){
                    if(name.equals(meta.getDisplayName())){
                        i += stack.getAmount();
                    }
                }
            }
        }
        return i >= need;
    }

    public static boolean checkIdNeed(int id,int need,Player player){
        Inventory inv = player.getInventory();
        int i = 0;
        for(ItemStack stack:inv){
            if(stack != null && stack.getTypeId() == id){
                i += stack.getAmount();
            }
        }
        return i >= need;
    }


    public static void takeItemNeed(String name,int need,Player player){
        Inventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                if(meta.hasDisplayName()){
                    if(name.equals(meta.getDisplayName())){
                        int amount = stack.getAmount();
                        if(amount >= need){
                            amount -= need;
                            stack.setAmount(amount);
                            inv.setItem(i,stack);
                            break;
                        }else{
                            need -= amount;
                            inv.setItem(i,null);
                        }
                    }
                }
            }
        }
    }


    public static void takeIdNeed(int id,int need,Player player){
        Inventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if(stack != null && stack.getTypeId() == id){
                int amount = stack.getAmount();
                if(amount >= need){
                    amount -= need;
                    stack.setAmount(amount);
                    inv.setItem(i,stack);
                    break;
                }else{
                    need -= amount;
                    inv.setItem(i,null);
                }
            }
        }
    }

    public static boolean checkCostNeed(String costId,int need,Player player){
        String name = player.getName();
        if(!enableMySQL) {
            if (playerData.containsKey(name)) {
                HashMap<String, Integer> costs = playerData.get(name);
                if (costs.containsKey(costId)) {
                    return costs.get(costId) >= need;
                }
            }
        }else{
            HashMap<String,Integer> costs = csb.selectPlayerData(name);
            if(costs.containsKey(costId)){
                int left = costs.get(costId)-need;
                return left >= 0;
            } else return false;
        }
        return false;
    }

    public static void takeCostNeed(String costId,int need,Player player){
        String name = player.getName();
        if(!enableMySQL) {
            if (playerData.containsKey(name)) {
                HashMap<String, Integer> costs = playerData.get(name);
                Iterator<Map.Entry<String, Integer>> it = costs.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Integer> e = it.next();
                    if (e.getKey().equals(costId)) {
                        int left = e.getValue() - need;
                        if (left <= 0) {
                            it.remove();
                        } else {
                            e.setValue(left);
                        }
                    }
                }
                playerData.put(name, costs);
            }
        }else{
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                HashMap<String, Integer> costs = csb.selectPlayerData(name);
                int left = costs.get(costId) - need;
                csb.updatePlayerCost(name, costId, left);
            });
        }
    }

    public static boolean checkEcoNeed(int need,Player player){
        return economy.getBalance(player) >= need;
    }


    public static void takeEcoNeed(int need,Player player){
        economy.withdrawPlayer(player,need);
    }

    public static boolean checkPointNeed(int need,Player player){
        if(poiLoad) {
            return poi.look(player.getUniqueId()) >= need;
        }else{
            return false;
        }
    }

    public static void takePointNeed(int need,Player player){
        if(poiLoad) {
            poi.take(player.getUniqueId(), need);
        }
    }
}
