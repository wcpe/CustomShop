package com.killercraft.jimy.ConfigManager;

import com.killercraft.jimy.CustomShop;
import com.killercraft.jimy.MySQL.CustomShopDatabase;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import static com.killercraft.jimy.ConfigManager.CSDataUtil.*;
import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CustomShopDatabase.*;

public class CSConfig {

    public static void update(){
        File file = new File(CustomShop.root, "config.yml");
        FileConfiguration config = load(file);
        boolean save = false;
        String checkStr = config.getString("Lang.InvCheckString");
        if(checkStr == null){
            config.set("Lang.InvCheckString","&7>> &c背包剩余&7[<value>&7]空格");
            save = true;
        }
        checkStr = config.getString("Lang.PAPICheckString");
        if(checkStr == null){
            config.set("Lang.PAPICheckString","&7>> &c<text>");
            save = true;
        }
        checkStr = config.getString("Lang.StrCheckString");
        if(checkStr == null){
            config.set("Lang.StrCheckString","&7>> &c<text>");
            save = true;
        }
        checkStr = config.getString("Lang.PlayerLimit");
        if(checkStr == null){
            config.set("Lang.PlayerLimit","&7>> &c您还可购买: &b<value> &c次");
            save = true;
        }
        if(save){
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConfigurationSection langs = config.getConfigurationSection("Lang");
        langMap = new HashMap<>();
        for(String temp:langs.getKeys(false)){
            langMap.put(temp,langs.getString(temp).replace('&', ChatColor.COLOR_CHAR));
        }
        ConfigurationSection costs = config.getConfigurationSection("Costs");
        costMap = new HashMap<>();
        for(String temp:costs.getKeys(false)){
            costMap.put(temp,costs.getString(temp).replace('&', ChatColor.COLOR_CHAR));
        }
        day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        enableMySQL = config.getBoolean("MySQL");
        if(enableMySQL) {
            csb = new CustomShopDatabase();
            csb.host = config.getString("MySQLHost");
            csb.port = config.getString("MySQLPort");
            csb.username = config.getString("MySQLUsername");
            csb.password = config.getString("MySQLPassword");
            csb.databaseName = config.getString("MySQLDatabase");
            csb.useSSL = config.getBoolean("MySQLUseSSL");
            try {
                if(csb.getConnection() == null) enableMySQL = false;
                csb.createCostsTable();
                csb.createPlayerDataTable();
                csb.createRefreshTable();
                csb.createShopsTable();
            } catch (SQLException e) {
                e.printStackTrace();
                enableMySQL = false;
            }
        }else {
            loadData();
            loadShops();
            loadRefresh();
            loadLimits();
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
