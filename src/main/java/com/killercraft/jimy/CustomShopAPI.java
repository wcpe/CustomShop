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
    ����� @name ����id @costId ���� @i
    ����ָ�������ָ��������ָ������
     */
    public static void giveCost(String name,String costId,int i){
        CSCostUtil.giveCost(name,costId,i);
    }

    /*
    ����� @name ����id @costId ���� @i
    ��ȡָ�������ָ��������ָ������
    ���� boolean
    true ���� ��ȡ�ɹ�
    false ��������ָ�������������� �������ȡ����
     */
    public static boolean takeCost(String name,String costId,int i){
        return CSCostUtil.takeCost(name,costId,i);
    }

    /*
    ����� @name ����id @costId ���� @i
    ɾ��ָ�������ָ��������ָ������
    �����һ��Ҳ���˴���д������ ��ֱ����Ϊ 0
    */
    public static void delCost(String name,String costId,int i){
        CSCostUtil.delCost(name,costId,i);
    }

    /*
    ����� @name ����id @costId
    �鿴ָ�������ָ������
    ���� int Ϊ�û������
    */
    public static int checkCost(String name,String costId){
        return CSCostUtil.checkCost(name, costId);
    }

    /*
    ����id @costId ������ @costName
    ֱ�Ӵ���һ���µĻ��� ���޸ĸò��config��������
    ���� boolean
    true ���� �����ɹ�
    false ������ظ��Ļ���id�Ѵ���
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
    ����id @costId ������ @costName
    �޸�һ���Ѵ��ڵĻ���id�Ļ����� ���޸ĸò��config��������
    ���� boolean
    true ���� �����޸ĳɹ�
    false ��������id������
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
    ����id @costId  �Ƿ�������� @clear
    ɾ��һ���Ѵ��ڵĻ���id ���޸ĸò��configɾ������
    ���� boolean
    true ���� ����ɾ���ɹ�
    false ��������id������
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
    ����id @costId
    ������������ҵ������е�ָ������id������
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
    ����id @costId
    ��ȡָ������id������
    ���� String Ϊָ�����ҵ�����
    ���Ϊ null �����ָ�����Ҳ�����
    */
    public static String getCostName(String costId){
        if(costMap.containsKey(costId)){
            return costMap.get(costId);
        }
        return null;
    }

    /*
    ������ @costName
    ��ȡָ����������Ӧ�Ļ���id
    ���� String Ϊָ�����ҵ�id
    ���Ϊ null �����û���κλ��ҵ�����Ϊָ��������
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
