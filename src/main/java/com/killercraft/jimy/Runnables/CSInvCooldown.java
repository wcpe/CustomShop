package com.killercraft.jimy.Runnables;

import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

import static com.killercraft.jimy.CustomShop.invClickCooldownMap;

public class CSInvCooldown implements Runnable {
    @Override
    public void run() {
        Iterator<Map.Entry<Player,Integer>> it = invClickCooldownMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Player,Integer> e = it.next();
            int i = e.getValue()-1;
            if(i <= 0){
                it.remove();
            }else{
                e.setValue(i);
            }
        }
    }
}
