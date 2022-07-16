package com.killercraft.jimy.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CustomShopDatabase.enableMySQL;

public class CSPlayerListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        if(!enableMySQL) return;
        Player player = event.getPlayer();
        String pName = player.getName();
        HashMap<String,Integer> data = csb.selectPlayerData(pName);
        playerData.put(pName,data);
        HashMap<String,Integer> limit = csb.getLimits(pName);
        if(!limit.isEmpty()) limitData.put(pName,limit);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if(!enableMySQL) return;
        Player player = event.getPlayer();
        String pName = player.getName();
        HashMap<String,Integer> data = playerData.getOrDefault(pName,new HashMap<>());
        csb.deletePlayerData(pName);
        csb.insertData(pName,data);
        csb.deleteLimitData(pName);
        if(limitData.containsKey(pName)) csb.insertLimits(pName);
    }


}
