package com.killercraft.jimy.Manager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.util.HashMap;

import static com.killercraft.jimy.CustomShop.costMap;
import static com.killercraft.jimy.CustomShop.hookPlayerData;
import static com.killercraft.jimy.Utils.CSCostUtil.checkCost;

public class CSPAPIHooker extends PlaceholderExpansion {

    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
    }

    public String getAuthor() {
        return "Jimy_Spirits";
    }

    public String getIdentifier() {
        return "cshop";
    }

    public String getPlugin() {
        return "CustomShop";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player player, String s) {
        if(s.contains("cost_")){
            String[] costSplit = s.split("_");
            if(costSplit.length < 3) return null;
            String costId = costSplit[1];
            if (costMap.containsKey(costId)) {
                String costName = costMap.get(costId);
                if (costSplit[2].equals("name")) {
                    return costName;
                } else if (costSplit[2].equals("bal")) {
                    return checkHookCost(player.getName(), costId) + "";
                } else return null;
            }
        }
        return null;
    }


    private int checkHookCost(String name,String costId){
        if (hookPlayerData.containsKey(name)) {
            HashMap<String, Integer> pCosts = hookPlayerData.get(name);
            if (pCosts.containsKey(costId)) {
                return pCosts.get(costId);
            }
        }
        return 0;
    }
}
