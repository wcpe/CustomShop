package com.killercraft.jimy.ConfigManager;

import com.killercraft.jimy.CustomShop;
import com.killercraft.jimy.Manager.GuiShop;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CustomShopDatabase.enableMySQL;
import static com.killercraft.jimy.Utils.CSItemSaveUtil.getItemStack;
import static com.killercraft.jimy.Utils.CSItemSaveUtil.toData;

public class CSDataUtil {
    public static void saveData(){
        if(enableMySQL) return;
        File file = new File(CustomShop.root, "playerdata.yml");
        FileConfiguration data = load(file);
        for (String temp : data.getKeys(false)) {
            data.set(temp, null);
        }
        for (String name : playerData.keySet()) {
            HashMap<String, Integer> dat = playerData.get(name);
            for (String key : dat.keySet()) {
                if (costMap.containsKey(key)) {
                    data.set(name + "." + key, dat.get(key));
                }
            }
        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRefresh(){
        File file = new File(CustomShop.root, "refresh.yml");
        FileConfiguration ref = load(file);
        for (String name : ref.getKeys(false)) {
            ref.set(name, null);
        }
        for(String shopName:refreshShops.keySet()){
            ref.set(shopName,refreshShops.get(shopName));
        }
        try {
            ref.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData(){
        File file = new File(CustomShop.root, "playerdata.yml");
        FileConfiguration data = load(file);
        playerData = new HashMap<>();
        for(String temp:data.getKeys(false)){
            HashMap<String,Integer> playerCost = new HashMap<>();
            ConfigurationSection sec = data.getConfigurationSection(temp);
            for(String temp2:sec.getKeys(false)){
                if(costMap.containsKey(temp2)) {
                    playerCost.put(temp2, sec.getInt(temp2));
                }
            }
            playerData.put(temp,playerCost);
        }
    }

    public static void saveShops(){
        File file = new File(CustomShop.root, "shops.yml");
        FileConfiguration shops = load(file);
        for(String temp:shops.getKeys(false)){
            shops.set(temp,null);
        }
        for(String shopName:customShops.keySet()){
            GuiShop gs = customShops.get(shopName);
            if(null == gs) continue;
            shops.set(shopName+".Line",gs.getLine());
            HashMap<Integer,ItemStack> items = gs.getItems();
            if(null == items) continue;
            for(int i:items.keySet()){
                ItemStack stack = items.get(i);
                if(stack == null || stack.getType() == Material.AIR) continue;
                String nbtData = toData(stack);
                shops.set(shopName+".Items."+i,nbtData);
                //shops.set(shopName+".Items."+i,stack);
            }
        }
        try {
            shops.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void loadShops(){
        File file = new File(CustomShop.root, "shops.yml");
        FileConfiguration shops = load(file);
        customShops = new HashMap<>();
        for(String temp:shops.getKeys(false)){
            int line = shops.getInt(temp+".Line");
            GuiShop gs = new GuiShop(line,temp);
            HashMap<Integer,ItemStack> items = new HashMap<>();
            ConfigurationSection sec = shops.getConfigurationSection(temp+".Items");
            if(sec != null) {
                for (String temp2 : sec.getKeys(false)) {
                    //ItemStack stack = sec.getItemStack(temp2);
                    ItemStack stack = getItemStack(sec.getString(temp2));
                    items.put(Integer.parseInt(temp2), stack);
                }
            }
            gs.setItems(items);
            customShops.put(temp,gs);
        }
    }

    public static void loadRefresh(){
        File file = new File(CustomShop.root, "refresh.yml");
        FileConfiguration ref = load(file);
        for(String temp:ref.getKeys(false)){
            refreshShops.put(temp,ref.getInt(temp));
        }
    }


    public static FileConfiguration load(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
