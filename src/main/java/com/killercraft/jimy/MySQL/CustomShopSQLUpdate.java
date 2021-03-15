package com.killercraft.jimy.MySQL;

import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CustomShopDatabase.enableMySQL;

public class CustomShopSQLUpdate implements Runnable {
    @Override
    public void run() {
        if(enableMySQL) {
            costMap = csb.getCosts();
        }
    }
}
