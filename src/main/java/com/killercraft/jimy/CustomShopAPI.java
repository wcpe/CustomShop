package com.killercraft.jimy;

import com.killercraft.jimy.Utils.CSCostUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.killercraft.jimy.ConfigManager.CSConfig.load;
import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CustomShopDatabase.enableMySQL;

public class CustomShopAPI {

    /*
    玩家名 @name 货币id @costId 数量 @i
    给予指定玩家名指定数量的指定货币
     */
    public static void giveCost(String name,String costId,int i){
        CSCostUtil.giveCost(name,costId,i);
    }

    /*
    玩家名 @name 货币id @costId 数量 @i
    拿取指定玩家名指定数量的指定货币
    返回 boolean
    true 代表 扣取成功
    false 则代表玩家指定货币数量不够 并不会扣取货币
     */
    public static boolean takeCost(String name,String costId,int i){
        return CSCostUtil.takeCost(name,costId,i);
    }

    /*
    玩家名 @name 货币id @costId 数量 @i
    删除指定玩家名指定数量的指定货币
    如果玩家货币不足此处填写的数量 则直接设为 0
    */
    public static void delCost(String name,String costId,int i){
        CSCostUtil.delCost(name,costId,i);
    }

    /*
    玩家名 @name 货币id @costId
    查看指定玩家名指定货币
    返回 int 为该货币余额
    */
    public static int checkCost(String name,String costId){
        return CSCostUtil.checkCost(name, costId);
    }

    /*
    货币id @costId 货币名 @costName
    直接创建一种新的货币 并修改该插件config新增货币
    返回 boolean
    true 代表 创建成功
    false 则代表重复的货币id已存在
    */
    public static boolean createCost(String costId,String costName){
        costName = costName.replace('&', ChatColor.COLOR_CHAR);
        if(!enableMySQL) {
            if (!costMap.containsKey(costId)) {
                costMap.put(costId, costName);
                File file = new File(CustomShop.root, "config.yml");
                FileConfiguration config = load(file);
                config.set("Costs." + costId, costName.replace(ChatColor.COLOR_CHAR, '&'));
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else return false;
        }else return csb.insertNewCost(costId,costName);
    }

    /*
    货币id @costId 货币名 @costName
    修改一种已存在的货币id的货币名 并修改该插件config改名货币
    返回 boolean
    true 代表 名称修改成功
    false 则代表货币id不存在
    */
    public static boolean renameCost(String costId,String costName){
        costName = costName.replace('&', ChatColor.COLOR_CHAR);
        if(!enableMySQL) {
            if (costMap.containsKey(costId)) {
                costMap.put(costId, costName);
                File file = new File(CustomShop.root, "config.yml");
                FileConfiguration config = load(file);
                config.set("Costs." + costId, costName.replace(ChatColor.COLOR_CHAR, '&'));
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else return false;
        }else return csb.updateCost(costId,costName);
    }

    /*
    货币id @costId  是否清理余额 @clear
    删除一种已存在的货币id 并修改该插件config删除货币
    返回 boolean
    true 代表 名称删除成功
    false 则代表货币id不存在
    */
    public static boolean deleteCost(String costId,boolean clear){
        if(!enableMySQL) {
            if (costMap.containsKey(costId)) {
                costMap.remove(costId);
                if(clear) clearCost(costId);
                File file = new File(CustomShop.root, "config.yml");
                FileConfiguration config = load(file);
                config.set("Costs." + costId, null);
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else return false;
        }else {
            if(clear) csb.clearCost(costId);
            return csb.deleteCosts(costId);
        }
    }

    /*
    货币id @costId
    清理现所有玩家的数据中的指定货币id的数量
    */
    public static void clearCost(String costId){
        Iterator<Map.Entry<String, HashMap<String, Integer>>> it1 = playerData.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry<String, HashMap<String, Integer>> e1 = it1.next();
            HashMap<String, Integer> map1 = e1.getValue();
            map1.entrySet().removeIf(e2 -> e2.getKey().equalsIgnoreCase(costId));
            if (map1.size() < 1) {
                it1.remove();
            } else {
                e1.setValue(map1);
            }
        }
    }

    /*
    货币id @costId
    获取指定货币id的名字
    返回 String 为指定货币的名字
    如果为 null 则代表指定货币不存在
    */
    public static String getCostName(String costId){
        if(costMap.containsKey(costId)){
            return costMap.get(costId);
        }
        return null;
    }

    /*
    货币名 @costName
    获取指定货币名对应的货币id
    返回 String 为指定货币的id
    如果为 null 则代表没有任何货币的名字为指定货币名
    */
    public static String getCostId(String costName){
        costName = costName.replace('&',ChatColor.COLOR_CHAR);
        for(String key:costMap.keySet()){
            String name = costMap.get(key);
            if(name.equalsIgnoreCase(costName)) return name;
        }
        return null;
    }
}
