package com.killercraft.jimy;

import com.killercraft.jimy.Listeners.CSItemUseListener;
import com.killercraft.jimy.Listeners.CSInvListener;
import com.killercraft.jimy.Manager.CSPAPIHooker;
import com.killercraft.jimy.Manager.GuiShop;
import com.killercraft.jimy.MySQL.CustomShopDatabase;
import com.killercraft.jimy.MySQL.CustomShopSQLUpdate;
import com.killercraft.jimy.Runnables.CSInvCooldown;
import com.killercraft.jimy.Runnables.CSRefreshRunnable;
import com.killercraft.jimy.Runnables.CSSaveDataRunnable;
import com.killercraft.jimy.Utils.CSUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;

import static com.killercraft.jimy.ConfigManager.CSConfig.update;
import static com.killercraft.jimy.ConfigManager.CSDataUtil.*;
import static com.killercraft.jimy.CustomShopAPI.*;
import static com.killercraft.jimy.MySQL.CustomShopDatabase.enableMySQL;
import static com.killercraft.jimy.Utils.CSCostUtil.delCost;
import static com.killercraft.jimy.Utils.CSCostUtil.giveCost;
import static com.killercraft.jimy.Utils.CSUtil.*;

public final class CustomShop extends JavaPlugin {

    public static String root;

    public static CustomShopDatabase csb;

    public static Economy economy;
    public static PlayerPointsAPI poi;
    public static boolean poiLoad;
    public static Plugin plugin;

    public static HashSet<String> cancelSet = new HashSet<>();
    public static HashMap<Player,Integer> invClickCooldownMap = new HashMap<>();

    public static HashMap<String,String> langMap = new HashMap<>();
    public static HashMap<String,String> costMap = new HashMap<>();
    public static HashMap<String,HashMap<String,Integer>> playerData = new HashMap<>();
    public static HashMap<String,HashMap<String,Integer>> hookPlayerData = new HashMap<>();
    public static HashMap<String,GuiShop> customShops = new HashMap<>();
    public static HashMap<String,Integer> refreshShops = new HashMap<>();

    private static HashSet<String> safetyLock = new HashSet<>();
    private static HashSet<String> safetyLock2 = new HashSet<>();

    public static int day;


    @Override
    public void onEnable() {
        plugin = Bukkit.getPluginManager().getPlugin("CustomShop");
        Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if(papi != null){
            boolean isLoadPAPI = new CSPAPIHooker().register();
            if(isLoadPAPI){
                System.out.println("[CustomShop]Placeholder API Loaded!");
            }else{
                System.out.println("[CustomShop]Placeholder API Unloaded!");
            }
        }
        root = getDataFolder().getAbsolutePath();
        setupEconomy();
        PlayerPoints points = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
        if(points != null){
            poi = points.getAPI();
            poiLoad = true;
            System.out.println("[CustomShop]Player Points Loaded!");
        }else{
            poiLoad = false;
            System.out.println("[CustomShop]Player Points Unloaded!");
        }
        saveDefaultConfig();
        update();
        Bukkit.getScheduler().runTaskTimer(this,new CSRefreshRunnable(),1200,1200);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new CSSaveDataRunnable(),6000,6000);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new CustomShopSQLUpdate(),30,30);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new CSInvCooldown(),2,2);
        Bukkit.getPluginManager().registerEvents(new CSInvListener(),this);
        Bukkit.getPluginManager().registerEvents(new CSItemUseListener(),this);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("cshop")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length < 1){
                    if(player.isOp()){
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                        player.sendMessage(ChatColor.BLUE+"/cshop reload "+ChatColor.GRAY+"- ���ز������");
                        player.sendMessage(ChatColor.BLUE+"/cshop list "+ChatColor.GRAY+"- �鿴��ǰ���е��̵�");
                        player.sendMessage(ChatColor.BLUE+"/cshop open �̵���"+ChatColor.GRAY+"- ��ָ���̵�");
                        player.sendMessage(ChatColor.BLUE+"/cshop open �̵��� �����"+ChatColor.GRAY+"- Ϊָ����Ҵ�ָ���̵�");
                        player.sendMessage(ChatColor.BLUE+"/cshop edit �̵���"+ChatColor.GRAY+"- �༭ָ���̵�");
                        player.sendMessage(ChatColor.BLUE+"/cshop refresh �̵���"+ChatColor.GRAY+"- ˢ�¸��̵�");
                        player.sendMessage(ChatColor.BLUE+"/cshop create �̵��� ����"+ChatColor.GRAY+"- ����һ��������ָ���������̵꣬ע���̵��������ᱨ��");
                        player.sendMessage(ChatColor.BLUE+"/cshop delete �̵���"+ChatColor.GRAY+"- ɾ��ָ���̵�");
                        player.sendMessage(ChatColor.BLUE+"/cshop costs"+ChatColor.GRAY+"- �鿴�Լ���ӵ�е����л���");
                        player.sendMessage(ChatColor.BLUE+"/cshop givecost ����� ����id ����"+ChatColor.GRAY+"- Ϊָ���������ָ������id�Ļ���");
                        player.sendMessage(ChatColor.BLUE+"/cshop takecost ����� ����id ����"+ChatColor.GRAY+"- Ϊָ����Ҽ���ָ������id�Ļ���");
                        player.sendMessage(ChatColor.BLUE+"/cshop cost list"+ChatColor.GRAY+"- �鿴���еĻ���id���Ӧ�Ļ�����");
                        player.sendMessage(ChatColor.BLUE+"/cshop cost create ����id ������"+ChatColor.GRAY+"- ����һ���µĻ���");
                        player.sendMessage(ChatColor.BLUE+"/cshop cost rename ����id ������"+ChatColor.GRAY+"- �޸�һ�ֻ��ҵ�����");
                        player.sendMessage(ChatColor.BLUE+"/cshop cost delete ����id true/false[�Ƿ�������]"+ChatColor.GRAY+"- ɾ��һ���Ѵ��ڵĻ���");
                        player.sendMessage(ChatColor.BLUE+"/cshop cost clear ����id"+ChatColor.GRAY+"- ������е���������е�ָ���������");
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                        player.sendMessage(ChatColor.RED+"����QQ:2506678176 - �������������[������Ϣ�����Ա�����OP�ɼ�]");
                    }else{
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                        player.sendMessage(ChatColor.BLUE+"/cshop open �̵���"+ChatColor.GRAY+"- ��ָ���̵�");
                        player.sendMessage(ChatColor.BLUE+"/cshop costs"+ChatColor.GRAY+"- �鿴�Լ���ӵ�е����л���");
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                    }
                }
                if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
                    if(!player.isOp()) return true;
                    CSUtil.reloadConfig(player);
                }else if(args.length == 1 && args[0].equalsIgnoreCase("list")){
                    if(!player.isOp()) return true;
                    sendList(player);
                }else if(args.length == 2 && args[0].equalsIgnoreCase("open")){
                    if(player.hasPermission("customshop.open."+args[1])) {
                        openShop(player,args[1]);
                    }else player.sendMessage(langMap.get("NoPermisson").replace("<perm>","customshop.open."+args[1]));
                }else if(args.length == 3 && args[0].equalsIgnoreCase("open")){
                    if(!player.isOp()) return true;
                    Player players = Bukkit.getPlayer(args[2]);
                    if(players == null) return true;
                    openShop(players,args[1]);
                }else if(args.length == 2 && args[0].equalsIgnoreCase("edit")){
                    if(!player.isOp()) return true;
                    openEditInv(player,args[1]);
                }else if(args.length == 2 && args[0].equalsIgnoreCase("refresh")){
                    if(!player.isOp()) return true;
                    refreshShop(player,args[1]);
                }else if(args.length == 3 && args[0].equalsIgnoreCase("create")){
                    if(!player.isOp()) return true;
                    createShop(player,args);
                }else if(args.length == 2 && args[0].equalsIgnoreCase("delete")){
                    if(!player.isOp()) return true;
                    deleteShop(player,args[1]);
                }else if(args.length == 1 && args[0].equalsIgnoreCase("costs")){
                    sendCosts(player);
                }else if(args.length >= 2 && args[0].equalsIgnoreCase("cost")){
                    if(player.isOp()) {
                        if(args[1].equalsIgnoreCase("create") && args.length == 4){
                            if(createCost(args[2],args[3])){
                                player.sendMessage(langMap.get("CostCreate"));
                            }else player.sendMessage(langMap.get("CostCreateNull"));
                        }else if(args[1].equalsIgnoreCase("delete") && args.length == 4){
                            if(deleteCost(args[2],Boolean.getBoolean(args[3]))){
                                player.sendMessage(langMap.get("CostDelete"));
                            }else player.sendMessage(langMap.get("CostDeleteNull"));
                        }else if(args[1].equalsIgnoreCase("clear") && args.length == 3){
                            clearCost(args[2]);
                            player.sendMessage(langMap.get("CostClear"));
                        }else if(args[1].equalsIgnoreCase("rename") && args.length == 4){
                            if(renameCost(args[2],args[3])){
                                player.sendMessage(langMap.get("CostRename"));
                            }else player.sendMessage(langMap.get("CostRenameNull"));
                        }else if(args[1].equalsIgnoreCase("list")){
                            sendCostList(player);
                        }
                    }
                }else if(args.length == 4 && args[0].equalsIgnoreCase("givecost")){
                    if(!player.isOp()) return true;
                    String msg = giveCost(args[1],args[2],Integer.parseInt(args[3]));
                    if(msg == null) return true;
                    player.sendMessage(msg);
                }else if(args.length == 4 && args[0].equalsIgnoreCase("takecost")){
                    if(!player.isOp()) return true;
                    String msg = delCost(args[1],args[2],Integer.parseInt(args[3]));
                    if(msg == null) return true;
                    player.sendMessage(msg);
                }else if(args.length == 2 && args[0].equalsIgnoreCase("upload")){
                    if(!player.isOp()) return true;
                    String pName = player.getName();
                    if(args[1].equalsIgnoreCase("shops")) {
                        if (!safetyLock.contains(pName)) {
                            player.sendMessage(ChatColor.GREEN + "=============CustomShop=============");
                            player.sendMessage(ChatColor.RED + "����:�ù��ܵ������ǽ���÷���˵��̵����������");
                            player.sendMessage(ChatColor.RED + "(���̵����������)�ϴ������ݿ��в�ɾ��ԭ������");
                            player.sendMessage(ChatColor.BLUE + "�ù���ֻ���ṩ���ӵ���תΪ���ݿ���û���ʹ�õģ�");
                            player.sendMessage(ChatColor.RED + "���������������κ��̵��Լ��������벻Ҫʹ�øù���");
                            player.sendMessage(ChatColor.RED + "������ܻᵼ�����ݿ����̵꼰���ұ���ջ��߱��޸ģ�");
                            player.sendMessage(ChatColor.GREEN + "���ȷ���������ݿ�����ѡ��֮ǰ��������̵꼰��������");
                            player.sendMessage(ChatColor.GREEN + "������Щ����������Ҫ�ϴ������ݿ���̵꼰�������õĻ�");
                            player.sendMessage(ChatColor.GREEN + "��ô������������" + ChatColor.AQUA + " /cshop upload shops" + ChatColor.GREEN + "��ִ���ϴ�");
                            player.sendMessage(ChatColor.GREEN + "=============CustomShop=============");
                            safetyLock.add(pName);
                        } else {
                            safetyLock.remove(pName);
                            if (enableMySQL) {
                                upLoadShopCostData();
                                player.sendMessage(ChatColor.GREEN + "�ϴ���ϣ�");
                            } else player.sendMessage(ChatColor.RED + "����û�п������ݿ�����ѡ�");
                        }
                    }else if(args[1].equalsIgnoreCase("playerdata")){
                        if (!safetyLock2.contains(pName)) {
                            player.sendMessage(ChatColor.GREEN + "=============CustomShop=============");
                            player.sendMessage(ChatColor.RED + "����:�ù��ܵ������ǽ���÷���˵���ҵĻ�������");
                            player.sendMessage(ChatColor.RED + "(����ҵĻ�������)�ϴ������ݿ��в�ɾ��ԭ������");
                            player.sendMessage(ChatColor.BLUE + "�ù���ֻ���ṩ���ӵ���תΪ���ݿ���û���ʹ�õģ�");
                            player.sendMessage(ChatColor.RED + "���������������κ���һ����������벻Ҫʹ�øù���");
                            player.sendMessage(ChatColor.RED + "������ܻᵼ�����ݿ�����ҵĻ��ұ���ջ��߱��޸ģ�");
                            player.sendMessage(ChatColor.GREEN + "���ȷ���������ݿ�����ѡ��֮ǰ���������ҵĻ�������");
                            player.sendMessage(ChatColor.GREEN + "������Щ����������Ҫ�ϴ������ݿ����ҵĻ������õĻ�");
                            player.sendMessage(ChatColor.RED + "ע��:���ϴ��������֮ǰ�����ϴ��̵����������,������ܵ��²��ֻ��Ҳ�����ʾ[��ʵ�ʴ���]");
                            player.sendMessage(ChatColor.GREEN + "��ô������������" + ChatColor.AQUA + " /cshop upload playerdata" + ChatColor.GREEN + "��ִ���ϴ�");
                            player.sendMessage(ChatColor.GREEN + "=============CustomShop=============");
                            safetyLock2.add(pName);
                        } else {
                            safetyLock2.remove(pName);
                            if (enableMySQL) {
                                upLoadPlayerData();
                                player.sendMessage(ChatColor.GREEN + "�ϴ���ϣ�");
                            } else player.sendMessage(ChatColor.RED + "����û�п������ݿ�����ѡ�");
                        }
                    }
                }
            }else{
                if(args.length == 3 && args[0].equalsIgnoreCase("open")){
                    Player player = Bukkit.getPlayer(args[2]);
                    if(player == null) return true;
                    openShop(player,args[1]);
                }else if(args.length == 4 && args[0].equalsIgnoreCase("givecost")){
                    String msg = giveCost(args[1],args[2],Integer.parseInt(args[3]));
                    if(msg == null) return true;
                    sender.sendMessage(msg);
                }else if(args.length == 4 && args[0].equalsIgnoreCase("takecost")){
                    String msg = delCost(args[1],args[2],Integer.parseInt(args[3]));
                    if(msg == null) return true;
                    sender.sendMessage(msg);
                }
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        saveData();
        saveShops();
        saveRefresh();
    }
    private void setupEconomy(){
        RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = ((Economy)economyProvider.getProvider());
        }
    }
}