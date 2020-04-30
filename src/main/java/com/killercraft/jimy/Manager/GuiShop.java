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

public class GuiShop {
    private int line;
    private HashMap<Integer, ItemStack> items;
    private String shopName;
    private Inventory shopInv;

    public GuiShop(int line,String shopName){
        this.line = line;
        this.shopName = shopName;
        this.items = new HashMap<>();
        shopInv = Bukkit.createInventory(null,line*9,toWord(shopName));
    }

    public GuiShop(ResultSet result) throws SQLException {
        this.shopName = toWord(result.getString(1));
        this.line = result.getInt(2);
        String itemStrings = result.getString(3);
        items = new HashMap<>();
        if(itemStrings.length() > 0){
            atStringsSetItems(itemStrings);
        }
        shopInv = Bukkit.createInventory(null,line*9,toWord(shopName));
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
        player.openInventory(shopInv);
        cancelSet.add(shopName);
    }

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
        for(HumanEntity he:shopInv.getViewers()){
            if(he instanceof Player){
                Player player = (Player) he;
                player.sendMessage(message);
                player.closeInventory();
            }
        }
    }

    private ItemStack formatItem(ItemStack oldStack){
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
                }else{
                    buyLore = lore;
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
                }else if(loreSplit[1].equalsIgnoreCase("daylimit")){
                    if(loreSplit.length < 4){
                        noLimit = true;
                    }else {
                        mLores.add(langMap.get("DayLimit").replace("<value>", loreSplit[3]));
                    }
                }else if(loreSplit[1].equalsIgnoreCase("daytime")){
                    if(checkTimeItem(loreSplit[2], loreSplit[3])){
                        if(!lock) remove = true;
                    }else{
                        lock = true;
                        remove = false;
                    }
                }else if(loreSplit[1].equalsIgnoreCase("randomtime")){
                    if(loreSplit.length < 5) noLimit = true;
                    if(checkTimeItem(loreSplit[3], loreSplit[4])){
                        if(!lock) remove = true;
                    }else{
                        lock = true;
                        remove = false;
                    }
                }
                it.remove();
            }
        }
        if(remove || noLimit) return null;
        for(int i = 0;i<lores.size();i++){
            String lore = lores.get(i);
            if(lore.contains("<player>")){
                lores.set(i,lore.replace("<player>",langMap.get("PlayerString")));
            }
        }
        lores.add(langMap.get("SplitString"));
        lores.addAll(buyLores);
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
        refreshItems();
    }

    public void refreshItems(){
        shopInv.clear();
        for(int i:items.keySet()){
            shopInv.setItem(i,formatItem(items.get(i)));
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
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int nowTime;
        if(min < 10){
            nowTime = Integer.parseInt((hour+".0"+min).replace(".",""));
        }else{
            nowTime = Integer.parseInt((hour+"."+min).replace(".",""));
        }
        int timea = Integer.parseInt(time1.replace(".",""));
        int timeb = Integer.parseInt(time2.replace(".",""));
        if(timea < timeb){
            return nowTime <= timea || nowTime > timeb;
        }else if(timea > timeb){
            return nowTime >= timeb && nowTime < timea;
        }else{
            return nowTime == timea;
        }
        /*String[] timea = time1.split("\\.");
        int ahour = Integer.parseInt(timea[0]);
        String[] timeb = time2.split("\\.");
        int bhour = Integer.parseInt(timeb[0]);
        if(ahour < bhour) {
            if (hour >= ahour && hour <= bhour) {
                if (hour == bhour || hour == ahour) {
                    int min = Calendar.getInstance().get(Calendar.MINUTE);
                    int amin = Integer.parseInt(timea[1]);
                    int bmin = Integer.parseInt(timeb[1]);
                    return min < amin || min >= bmin;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }else if(ahour == bhour){
            if (hour <= ahour && hour >= bhour) {
                int min = Calendar.getInstance().get(Calendar.MINUTE);
                int amin = Integer.parseInt(timea[1]);
                int bmin = Integer.parseInt(timeb[1]);
                return min < amin || min >= bmin;
            } else {
                return true;
            }
        }else{
            if (hour <= ahour && hour >= bhour) {
                if (hour == bhour || hour == ahour) {
                    int min = Calendar.getInstance().get(Calendar.MINUTE);
                    int amin = Integer.parseInt(timea[1]);
                    int bmin = Integer.parseInt(timeb[1]);
                    return min < amin || min >= bmin;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }*/
    }
}
