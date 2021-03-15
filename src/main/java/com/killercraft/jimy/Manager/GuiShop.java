package com.killercraft.jimy.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CSUnicodeUtil.toWord;
import static com.killercraft.jimy.Utils.CSItemSaveUtil.getItemStack;
import static com.killercraft.jimy.Utils.CSItemSaveUtil.toData;
import static com.killercraft.jimy.Utils.CSItemUtil.getPlayerKeyLimit;
import static com.killercraft.jimy.Utils.CSUtil.openEditInv;

public class GuiShop {
    private int line;
    private HashMap<Integer, ItemStack> items;
    private String shopName;
    //private Inventory shopInv;

    public GuiShop(int line,String shopName){
        this.line = line;
        this.shopName = shopName;
        this.items = new HashMap<>();
        //shopInv = Bukkit.createInventory(null,line*9,toWord(shopName));
    }

    public GuiShop(ResultSet result) throws SQLException {
        this.shopName = toWord(result.getString(1));
        this.line = result.getInt(2);
        String itemStrings = result.getString(3);
        items = new HashMap<>();
        if(itemStrings.length() > 0){
            atStringsSetItems(itemStrings);
        }
        //shopInv = Bukkit.createInventory(null,line*9,toWord(shopName));
    }

    public void atStringsSetItems(String itemDatas){
        if(itemDatas.contains("$")) {
            String[] item = itemDatas.split("\\$");
            for (String s : item) {
                String[] data = s.split("#");
                int slot = Integer.parseInt(data[0]);
                ItemStack stack = getItemStack(data[1]);
                if (stack == null) continue;
                items.put(slot, stack);
            }
        }else{
            String[] data = itemDatas.split("#");
            int slot = Integer.parseInt(data[0]);
            ItemStack stack = getItemStack(data[1]);
            if (stack == null) return;
            items.put(slot, stack);
        }
    }

    public String getItemsString(){
        String s = "";
        int a = 0;
        for(int i:items.keySet()){
            String data = i+"#"+toData(items.get(i));
            s+=data;
            if(a != items.size()-1){
                s+="$";
            }
            a++;
        }
        return s;
    }

    public void openShop(Player player){
        if(editSet.contains(player)){
            openEditInv(player);
        }else {
            //player.openInventory(shopInv);
            Inventory inv = Bukkit.createInventory(null, line * 9, shopName);
            refreshItems(inv, player);
            player.openInventory(inv);
        /*for(int i = 0;i<shopInv.getSize();i++){
            ItemStack stack = shopInv.getItem(i);
        }*/
            cancelSet.add(shopName);
        }
    }

    /*private ItemStack formatPlayerItem(ItemStack stack,Player player){
        if(stack != null && stack.hasItemMeta()){
            ItemMeta meta = stack.getItemMeta();
            if(meta.hasLore()){
                List<String> loreList = meta.getLore();
                List<String> newLore = new ArrayList<>();
                for(String lore:loreList){
                    if(lore.contains("<playerlimit>")){
                        newLore.add(lore.replace("<playerlimit>",getPlayerKeyLimit(player,)))
                    }else{
                        newLore.add(lore);
                    }
                }
            }
        }
    }*/

    public void openEditInv(Player player){
        Inventory inv = Bukkit.createInventory(null,line*9,"[E]"+toWord(shopName));
        for(int i:items.keySet()){
            inv.setItem(i,items.get(i));
        }
        player.openInventory(inv);
    }

    public void refresh(){
        HashMap<Integer,ItemStack> itemsChange = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> e : items.entrySet()) {
            ItemStack stack = e.getValue();
            if (stack == null) continue;
            ItemMeta meta = stack.getItemMeta();
            List<String> loreList = meta.getLore();
            HashMap<Integer, String> changeMap = new HashMap<>();
            int i = 0;
            for (String lore : loreList) {
                if (lore.startsWith("s")) {
                    String[] loreSplit = lore.split("~");
                    if (loreSplit[1].equalsIgnoreCase("daylimit")) {
                        changeMap.put(i, "s~daylimit~" + loreSplit[2] + "~" + loreSplit[2]);
                    } else if (loreSplit[1].equalsIgnoreCase("randomtime")) {
                        String[] time = loreSplit[2].split("\\.");
                        int hour = Integer.parseInt(time[0]);
                        int min = Integer.parseInt(time[1]);
                        int randomHour = (int) (Math.random() * 24);
                        int randomMin = (int) (Math.random() * 60);
                        int nextHour = randomHour + hour;
                        int nextMin = randomMin + min;
                        if (nextMin >= 60) {
                            nextMin -= 60;
                            nextHour += 1;
                        }
                        if (nextHour >= 24) nextHour -= 24;
                        String randomS = randomHour + "." + randomMin;
                        String nextS = nextHour + "." + nextMin;
                        changeMap.put(i, "s~randomtime~" + loreSplit[2] + "~" + randomS + "~" + nextS);
                    }
                }
                i++;
            }
            for (int mi : changeMap.keySet()) {
                loreList.set(mi, changeMap.get(mi));
            }
            meta.setLore(loreList);
            stack.setItemMeta(meta);
            itemsChange.put(e.getKey(),stack);
        }
        for(int i:itemsChange.keySet()){
            items.put(i,itemsChange.get(i));
        }
    }

    public void closeAllInv(String message){
        /*for(HumanEntity he:shopInv.getViewers()){
            if(he instanceof Player){
                Player player = (Player) he;
                player.sendMessage(message);
                player.closeInventory();
            }
        }*/
    }

    private ItemStack formatItem(ItemStack oldStack,Player player){
        if(oldStack == null || oldStack.getType() == Material.AIR) return null;
        ItemStack stack = oldStack.clone();
        ItemMeta meta = stack.getItemMeta();
        List<String> lores = meta.getLore();
        List<String> buyLores = new ArrayList<>();
        Iterator it = lores.iterator();
        List<String> mLores = new ArrayList<>();
        boolean remove = false;
        boolean noLimit = false;
        boolean lock = false;
        boolean noLore = false;
        while (it.hasNext()){
            String lore = (String) it.next();
            if(lore.startsWith("b")){
                String[] loreSplit = lore.split("~");
                String buyLore = "";
                if(loreSplit[1].equalsIgnoreCase("eco")){
                    buyLore = langMap.get("EcoString").replace("<value>",loreSplit[2]);
                }else if(loreSplit[1].equalsIgnoreCase("point")){
                    buyLore = langMap.get("PointString").replace("<value>",loreSplit[2]);
                }else if(loreSplit[1].equalsIgnoreCase("item")){
                    buyLore = langMap.get("ItemString").replace("<item>",loreSplit[2]).replace("<value>",loreSplit[3]);
                }else if(loreSplit[1].equalsIgnoreCase("lore")){
                    buyLore = langMap.get("LoreString").replace("<lore>",loreSplit[2]).replace("<value>",loreSplit[3]);
                }else if(loreSplit[1].equalsIgnoreCase("id")){
                    buyLore = langMap.get("IdString").replace("<item>",loreSplit[3]).replace("<value>",loreSplit[4]);
                }else if(loreSplit[1].equalsIgnoreCase("none")){
                    buyLore = langMap.get("NoneString");
                }else if(loreSplit[1].equalsIgnoreCase("perm")){
                    buyLore = langMap.get("PermString").replace("<perm>",loreSplit[3]);
                }else if(loreSplit[1].equalsIgnoreCase("cost")) {
                    if (costMap.containsKey(loreSplit[2])) {
                        buyLore = langMap.get("CostString").replace("<cost>", costMap.get(loreSplit[2])).replace("<value>", loreSplit[3]);
                    }
                }else if (loreSplit[1].equalsIgnoreCase("show")){
                    noLore = true;
                }else{
                    buyLore = lore;
                }
                buyLores.add(buyLore);
                it.remove();
            }else if(lore.startsWith("c")){
                String[] loreSplit = lore.split("~");
                String buyLore = "";
                if(loreSplit[1].equalsIgnoreCase("papi")){
                    buyLore = langMap.get("PAPICheckString").replace("<text>",loreSplit[4]);
                }else if(loreSplit[1].equalsIgnoreCase("str")){
                    buyLore = langMap.get("StrCheckString").replace("<text>",loreSplit[4]);
                }else if(loreSplit[1].equalsIgnoreCase("inv")){
                    buyLore = langMap.get("InvCheckString").replace("<value>",loreSplit[2]);
                }else if(loreSplit[1].equalsIgnoreCase("perm")){
                    buyLore = langMap.get("PermString").replace("<perm>",loreSplit[3]);
                }
                buyLores.add(buyLore);
                it.remove();
            }else if(lore.startsWith("m")){
                if(lore.contains("~")) {
                    String[] loreSplit = lore.split("~");
                    mLores.add(loreSplit[1]);
                }else{
                    buyLores.add(lore);
                }
                it.remove();
            }else if(lore.startsWith("s")){
                String[] loreSplit = lore.split("~");
                if(loreSplit[1].equalsIgnoreCase("limit")){
                    mLores.add(langMap.get("Limit").replace("<value>",loreSplit[2]));
                }else if(loreSplit[1].equalsIgnoreCase("playerlimit")){
                    // s~playerlimit~key~5
                    if(loreSplit.length < 4){
                        noLimit = true;
                    }else {
                        int a = Integer.parseInt(loreSplit[3])-getPlayerKeyLimit(player.getName(),loreSplit[2]);
                        if(a < 0) a = 0;
                        mLores.add(langMap.get("PlayerLimit").replace("<value>", a+""));
                    }
                }else if(loreSplit[1].equalsIgnoreCase("daylimit")){
                    if(loreSplit.length < 4){
                        noLimit = true;
                    }else {
                        mLores.add(langMap.get("DayLimit").replace("<value>", loreSplit[3]));
                    }
                }else if(loreSplit[1].equalsIgnoreCase("daytime")){
                    if(checkTimeItem(loreSplit[2], loreSplit[3])){
                        lock = true;
                        remove = false;
                    }else{
                        if(!lock) remove = true;
                    }
                }else if(loreSplit[1].equalsIgnoreCase("randomtime")){
                    if(loreSplit.length < 5) {
                        noLimit = true;
                    }else {
                        if (checkTimeItem(loreSplit[3], loreSplit[4])) {
                            lock = true;
                            remove = false;
                        } else {
                            if (!lock) remove = true;
                        }
                    }
                }
                it.remove();
            }
        }
        if(remove || noLimit) return null;
        for(int i = 0;i<lores.size();i++){
            String lore = lores.get(i);
            if(lore.contains("<player>")){
                lores.set(i,lore.replace("<player>",player.getName()));
            }
        }
        if(!noLore) {
            lores.add(langMap.get("SplitString"));
            lores.addAll(buyLores);
        }
        if(mLores.size() > 0) {
            lores.add(langMap.get("SplitMsgString"));
            lores.addAll(mLores);
        }

        meta.setLore(lores);
        stack.setItemMeta(meta);
        return stack;
    }

    public HashMap<Integer, ItemStack> getItems() {
        return items;
    }

    public int getLine() {
        return line;
    }

    public void setItems(HashMap<Integer, ItemStack> items) {
        this.items = items;
    }

    public void refreshItems(Inventory inv,Player player){
        inv.clear();
        for(int i:items.keySet()){
            inv.setItem(i,formatItem(items.get(i),player));
        }
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setLine(int line) {
        this.line = line;
    }

    private boolean checkTimeItem(String time1,String time2){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int nowTime;
        if(min < 10){
            nowTime = Integer.parseInt(hour+"0"+min);
        }else{
            nowTime = Integer.parseInt(hour+""+min);
        }
        int timea = Integer.parseInt(time1.replace(".",""));
        int timeb = Integer.parseInt(time2.replace(".",""));
        //return timea <= nowTime && nowTime <= timeb;
        //timea 12.05
        //nowtime 12.50
        //timeb 13.50
        if(timea < timeb){
            return timea <= nowTime && nowTime <= timeb;
        }else{
            return timeb <= nowTime && nowTime <= timea;
        }
    }
}
