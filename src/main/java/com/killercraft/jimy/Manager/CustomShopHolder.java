package com.killercraft.jimy.Manager;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomShopHolder implements InventoryHolder {

    private String title;

    public CustomShopHolder(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public Inventory getInventory() {
        return null;
    }
}
