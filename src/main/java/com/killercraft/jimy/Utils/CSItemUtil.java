package com.killercraft.jimy.Utils;

import com.killercraft.jimy.CustomShopAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

import static com.killercraft.jimy.CustomShop.*;

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


    public static boolean checkItemLoreNeed(String loreNeed,int need,Player player){
        Inventory inv = player.getInventory();
        int i = 0;
        for(ItemStack stack:inv){
            if(stack != null && stack.hasItemMeta()){
                ItemMeta meta = stack.getItemMeta();
                if(meta.hasLore()){
                    for(String lore:meta.getLore()){
                        if(lore.contains(loreNeed)){
                            i += stack.getAmount();
                            break;
                        }
                    }
                }
            }
        }
        return i >= need;
    }

    public static boolean checkIdNeed(int id,int dur,int need,Player player){
        Inventory inv = player.getInventory();
        int i = 0;
        for(ItemStack stack:inv){
            if(stack != null && stack.getTypeId() == id){
                if(dur >= 0){
                    int d = stack.getDurability();
                    if(d == dur){
                        i += stack.getAmount();
                    }
                }else {
                    i += stack.getAmount();
                }
            }
        }
        return i >= need;
    }

    public static boolean checkInvNeed(int need,Player player){
        PlayerInventory inv = player.getInventory();
        int a = 0;
        for(int i = 0;i<inv.getSize();i++){
            ItemStack stack = inv.getItem(i);
            if(stack == null || stack.getType() == Material.AIR){
                a ++;
            }
        }
        return a >= need;
    }


    public static void takeItemLoreNeed(String loreNeed,int need,Player player){
        Inventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                if(meta.hasLore()){
                    for(String lore:meta.getLore()){
                        if(lore.contains(loreNeed)){
                            int amount = stack.getAmount();
                            if(amount > need){
                                amount -= need;
                                stack.setAmount(amount);
                                inv.setItem(i,stack);
                                break;
                            }else{
                                need -= amount;
                                inv.setItem(i,null);
                            }
                            break;
                        }
                    }
                }
            }
        }
        PlayerInventory pi = player.getInventory();
        ItemStack deputy = pi.getItemInOffHand();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasLore()){
                for(String lore:meta.getLore()){
                    if(lore.contains(loreNeed)){
                        int amount = deputy.getAmount();
                        if(amount > need){
                            amount -= need;
                            deputy.setAmount(amount);
                            pi.setItemInOffHand(deputy);
                            break;
                        }else{
                            pi.setItemInOffHand(null);
                        }
                        break;
                    }
                }
            }
        }
        deputy = pi.getHelmet();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasLore()){
                for(String lore:meta.getLore()){
                    if(lore.contains(loreNeed)){
                        int amount = deputy.getAmount();
                        if(amount > need){
                            amount -= need;
                            deputy.setAmount(amount);
                            pi.setHelmet(deputy);
                            break;
                        }else{
                            pi.setHelmet(null);
                        }
                        break;
                    }
                }
            }
        }
        deputy = pi.getChestplate();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasLore()){
                for(String lore:meta.getLore()){
                    if(lore.contains(loreNeed)){
                        int amount = deputy.getAmount();
                        if(amount > need){
                            amount -= need;
                            deputy.setAmount(amount);
                            pi.setChestplate(deputy);
                            break;
                        }else{
                            pi.setChestplate(null);
                        }
                        break;
                    }
                }
            }
        }
        deputy = pi.getLeggings();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasLore()){
                for(String lore:meta.getLore()){
                    if(lore.contains(loreNeed)){
                        int amount = deputy.getAmount();
                        if(amount > need){
                            amount -= need;
                            deputy.setAmount(amount);
                            pi.setLeggings(deputy);
                            break;
                        }else{
                            pi.setLeggings(null);
                        }
                        break;
                    }
                }
            }
        }
        deputy = pi.getBoots();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasLore()){
                for(String lore:meta.getLore()){
                    if(lore.contains(loreNeed)){
                        int amount = deputy.getAmount();
                        if(amount > need){
                            amount -= need;
                            deputy.setAmount(amount);
                            pi.setBoots(deputy);
                            break;
                        }else{
                            pi.setBoots(null);
                        }
                        break;
                    }
                }
            }
        }
    }
    public static void takePlayerKeyLimit(String pName,String key,int i){
        HashMap<String,Integer> map = limitData.getOrDefault(pName,new HashMap<>());
        if(map.containsKey(key)){
            map.put(key,map.get(key)+i);
        }else{
            map.put(key,1);
        }
        limitData.put(pName,map);
    }

    public static int getPlayerKeyLimit(String pName,String key){
        if(limitData.containsKey(pName)){
            HashMap<String,Integer> map = limitData.get(pName);
            if(map.containsKey(key)){
                return map.get(key);
            }
        }
        return 0;
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
                        if(amount > need){
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
        PlayerInventory pi = player.getInventory();
        ItemStack deputy = pi.getItemInOffHand();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasDisplayName()){
                if(name.equals(meta.getDisplayName())){
                    int amount = deputy.getAmount();
                    if(amount > need){
                        amount -= need;
                        deputy.setAmount(amount);
                        pi.setItemInOffHand(deputy);
                    }else{
                        pi.setItemInOffHand(null);
                    }
                }
            }
        }
        deputy = pi.getHelmet();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasDisplayName()){
                if(name.equals(meta.getDisplayName())){
                    int amount = deputy.getAmount();
                    if(amount > need){
                        amount -= need;
                        deputy.setAmount(amount);
                        pi.setHelmet(deputy);
                    }else{
                        pi.setHelmet(null);
                    }
                }
            }
        }
        deputy = pi.getChestplate();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasDisplayName()){
                if(name.equals(meta.getDisplayName())){
                    int amount = deputy.getAmount();
                    if(amount > need){
                        amount -= need;
                        deputy.setAmount(amount);
                        pi.setChestplate(deputy);
                    }else{
                        pi.setChestplate(null);
                    }
                }
            }
        }
        deputy = pi.getLeggings();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasDisplayName()){
                if(name.equals(meta.getDisplayName())){
                    int amount = deputy.getAmount();
                    if(amount > need){
                        amount -= need;
                        deputy.setAmount(amount);
                        pi.setLeggings(deputy);
                    }else{
                        pi.setLeggings(null);
                    }
                }
            }
        }
        deputy = pi.getBoots();
        if (deputy != null && deputy.hasItemMeta()) {
            ItemMeta meta = deputy.getItemMeta();
            if(meta.hasDisplayName()){
                if(name.equals(meta.getDisplayName())){
                    int amount = deputy.getAmount();
                    if(amount > need){
                        amount -= need;
                        deputy.setAmount(amount);
                        pi.setBoots(deputy);
                    }else{
                        pi.setBoots(null);
                    }
                }
            }
        }
    }


    public static void takeIdNeed(int id,int dur,int need,Player player){
        Inventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if(stack != null && stack.getTypeId() == id){
                if(dur >= 0){
                    int d = stack.getDurability();
                    if(d == dur){
                        int amount = stack.getAmount();
                        if (amount > need) {
                            amount -= need;
                            stack.setAmount(amount);
                            inv.setItem(i, stack);
                            break;
                        } else {
                            need -= amount;
                            inv.setItem(i, null);
                        }
                    }
                }else {
                    int amount = stack.getAmount();
                    if (amount > need) {
                        amount -= need;
                        stack.setAmount(amount);
                        inv.setItem(i, stack);
                        break;
                    } else {
                        need -= amount;
                        inv.setItem(i, null);
                    }
                }
            }
        }
        PlayerInventory pi = player.getInventory();
        ItemStack deputy = pi.getItemInOffHand();
        if(deputy != null && deputy.getTypeId() == id){
            int amount = deputy.getAmount();
            if(amount > need){
                amount -= need;
                deputy.setAmount(amount);
                pi.setItemInOffHand(deputy);
            }else{
                pi.setItemInOffHand(null);
            }
        }
        deputy = pi.getHelmet();
        if(deputy != null && deputy.getTypeId() == id){
            int amount = deputy.getAmount();
            if(amount > need){
                amount -= need;
                deputy.setAmount(amount);
                pi.setHelmet(deputy);
            }else{
                pi.setHelmet(null);
            }
        }
        deputy = pi.getChestplate();
        if(deputy != null && deputy.getTypeId() == id){
            int amount = deputy.getAmount();
            if(amount > need){
                amount -= need;
                deputy.setAmount(amount);
                pi.setChestplate(deputy);
            }else{
                pi.setChestplate(null);
            }
        }
        deputy = pi.getLeggings();
        if(deputy != null && deputy.getTypeId() == id){
            int amount = deputy.getAmount();
            if(amount > need){
                amount -= need;
                deputy.setAmount(amount);
                pi.setLeggings(deputy);
            }else{
                pi.setLeggings(null);
            }
        }
        deputy = pi.getBoots();
        if(deputy != null && deputy.getTypeId() == id){
            int amount = deputy.getAmount();
            if(amount > need){
                amount -= need;
                deputy.setAmount(amount);
                pi.setBoots(deputy);
            }else{
                pi.setBoots(null);
            }
        }
    }

    public static boolean checkCostNeed(String costId,int need,Player player){
        String name = player.getName();
        if (playerData.containsKey(name)) {
            HashMap<String, Integer> costs = playerData.get(name);
            if (costs.containsKey(costId)) {
                return costs.get(costId) >= need;
            }
        }
        return false;
    }

    public static void takeCostNeed(String costId,int need,Player player){
        CustomShopAPI.delCost(player.getName(),costId,need);
        /*String name = player.getName();
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
        }*/
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
