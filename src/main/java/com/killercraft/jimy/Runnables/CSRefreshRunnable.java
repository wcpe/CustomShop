package com.killercraft.jimy.Runnables;

import com.killercraft.jimy.Manager.GuiShop;

import static com.killercraft.jimy.CustomShop.customShops;

public class CSRefreshRunnable implements Runnable {
    @Override
    public void run() {
        for(GuiShop gs:customShops.values()){
            gs.refreshItems();
        }
    }
}
