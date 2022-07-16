package com.killercraft.jimy;

import com.killercraft.jimy.Listeners.CSItemUseListener;
import com.killercraft.jimy.Listeners.CSInvListener;
import com.killercraft.jimy.Listeners.CSPlayerListener;
import com.killercraft.jimy.Manager.CSPAPIHooker;
import com.killercraft.jimy.Manager.GuiShop;
import com.killercraft.jimy.MySQL.CustomShopDatabase;
import com.killercraft.jimy.MySQL.CustomShopSQLUpdate;
import com.killercraft.jimy.Runnables.CSInvCooldown;
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
import org.bukkit.inventory.ItemStack;
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

    public static HashSet<Player> editSet = new HashSet<>();

    public static HashSet<String> cancelSet = new HashSet<>();
    public static HashMap<Player,Integer> invClickCooldownMap = new HashMap<>();

    public static HashMap<String,String> langMap = new HashMap<>();
    public static HashMap<String,String> costMap = new HashMap<>();
    public static HashMap<String,HashMap<String,Integer>> playerData = new HashMap<>();
    public static HashMap<String,GuiShop> customShops = new HashMap<>();
    public static HashMap<String,Integer> refreshShops = new HashMap<>();

    public static HashMap<String,HashMap<String,Integer>> limitData = new HashMap<>();

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
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new CSSaveDataRunnable(),6000,6000);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new CustomShopSQLUpdate(),40,400);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new CSInvCooldown(),2,2);
        Bukkit.getPluginManager().registerEvents(new CSInvListener(),this);
        Bukkit.getPluginManager().registerEvents(new CSItemUseListener(),this);
        Bukkit.getPluginManager().registerEvents(new CSPlayerListener(),this);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("cshop")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length < 1){
                    if(player.isOp()){
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                        player.sendMessage(ChatColor.AQUA+"/cshop reload "+ChatColor.GRAY+"- ���ز������");
                        player.sendMessage(ChatColor.AQUA+"/cshop list "+ChatColor.GRAY+"- �鿴��ǰ���е��̵�");
                        player.sendMessage(ChatColor.AQUA+"/cshop open ��e�̵�����b"+ChatColor.GRAY+"- ��ָ���̵�");
                        player.sendMessage(ChatColor.AQUA+"/cshop open ��e�̵�����b ��9�������b"+ChatColor.GRAY+"- Ϊָ����Ҵ�ָ���̵�");
                        player.sendMessage(ChatColor.AQUA+"/cshop edit ��e�̵�����b"+ChatColor.GRAY+"- �༭ָ���̵�");
                        player.sendMessage(ChatColor.AQUA+"/cshop editmode"+ChatColor.GRAY+"- ����/�ر��̵�༭ģʽ���༭ģʽ�������κ��̵���ֱ�Ӵ򿪴��̵�ı༭����");
                        player.sendMessage(ChatColor.AQUA+"/cshop refresh ��e�̵�����b"+ChatColor.GRAY+"- ˢ�¸��̵�");
                        player.sendMessage(ChatColor.AQUA+"/cshop create ��e�̵�����b ��6������b"+ChatColor.GRAY+"- ����һ��������ָ���������̵꣬ע���̵��������ᱨ��");
                        player.sendMessage(ChatColor.AQUA+"/cshop delete ��e�̵�����b"+ChatColor.GRAY+"- ɾ��ָ���̵�");
                        player.sendMessage(ChatColor.AQUA+"/cshop costs"+ChatColor.GRAY+"- �鿴�Լ���ӵ�е����л���");
                        player.sendMessage(ChatColor.AQUA+"/cshop givecost ��9�������b ��e����id��b ��6������b"+ChatColor.GRAY+"- Ϊָ��������ָ������id�Ļ���");
                        player.sendMessage(ChatColor.AQUA+"/cshop takecost ��9�������b ��e����id��b ��6������b"+ChatColor.GRAY+"- Ϊָ����Ҽ���ָ������id�Ļ���");
                        player.sendMessage(ChatColor.AQUA+"/cshop seecost ��9�������b ��e����id��b"+ChatColor.GRAY+"- �鿴ָ����ҵ�ָ������id�Ļ������");
                        player.sendMessage(ChatColor.AQUA+"/cshop cost list"+ChatColor.GRAY+"- �鿴���еĻ���id���Ӧ�Ļ�����");
                        player.sendMessage(ChatColor.AQUA+"/cshop cost create ��e����id��b ��d��������b"+ChatColor.GRAY+"- ����һ���µĻ���");
                        player.sendMessage(ChatColor.AQUA+"/cshop cost rename ��e����id��b ��d��������b"+ChatColor.GRAY+"- �޸�һ�ֻ��ҵ�����");
                        player.sendMessage(ChatColor.AQUA+"/cshop cost delete ��e����id��b true/false[�Ƿ�������]"+ChatColor.GRAY+"- ɾ��һ���Ѵ��ڵĻ���");
                        player.sendMessage(ChatColor.AQUA+"/cshop cost clear ��e����id��b"+ChatColor.GRAY+"- ������е���������е�ָ���������");
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                        player.sendMessage(ChatColor.RED+"��9����QQ:��b2506678176 ��7- ��a������������ϡ�c[������Ϣ�����Ա�����OP�ɼ�]");
                    }else{
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                        player.sendMessage(ChatColor.AQUA+"/cshop open ��e�̵�����b"+ChatColor.GRAY+"- ��ָ���̵�");
                        player.sendMessage(ChatColor.AQUA+"/cshop costs"+ChatColor.GRAY+"- �鿴�Լ���ӵ�е����л���");
                        player.sendMessage(ChatColor.GREEN+"=============CustomShop=============");
                    }
                }
                if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
                    if(!player.isOp()) return true;
                    CSUtil.reloadConfig(player);
                }else if(args.length == 1 && args[0].equalsIgnoreCase("list")){
                    if(!player.isOp()) return true;
                    sendList(player,null);
                }else if(args.length == 2 && args[0].equalsIgnoreCase("list")){
                    if(!player.isOp()) return true;
                    sendList(player,args[1]);
                }else if(args.length == 2 && args[0].equalsIgnoreCase("open")){
                    //openShop(player,args[1]);
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
                }else if(args.length == 2 && args[0].equalsIgnoreCase("testprint")){
                    if(!player.isOp()) return true;
                    ItemStack stack = player.getItemInHand();
                    try {
                        player.sendMessage(stack.getItemMeta().getDisplayName().replace(ChatColor.COLOR_CHAR+"","#"));
                    }catch (Throwable e){
                        player.sendMessage("wu pin mei you ming zi");
                    }
                }else if(args.length == 2 && args[0].equalsIgnoreCase("refresh")){
                    if(!player.isOp()) return true;
                    refreshShop(player,args[1],true);
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
                }else if(args.length == 1 && args[0].equalsIgnoreCase("editmode")){
                    if(!player.isOp()) return true;
                    if(editSet.contains(player)){
                        editSet.remove(player);
                        player.sendMessage("��f[��aCustomShop��f]��a���� ��c�ر� ��a�༭ģʽ��");
                    }else{
                        editSet.add(player);
                        player.sendMessage("��f[��aCustomShop��f]��a���� ��b���� ��a�༭ģʽ��");

                    }
                }else if(args.length == 4 && args[0].equalsIgnoreCase("givecost")){
                    if(!player.isOp()) return true;
                    String msg = giveCost(args[1],args[2],Integer.parseInt(args[3]));
                    if(msg == null) return true;
                    player.sendMessage(msg);
                }else if(args.length == 3 && args[0].equalsIgnoreCase("seecost")){
                    if(!player.isOp()) return true;
                    int i = checkCost(args[1],args[2]);
                    player.sendMessage("��f[��aCustomShop��f]��b����Ҹû�����ʣ ��a"+i);
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
                }else if(args.length == 1 && args[0].equalsIgnoreCase("download")){
                    if(!player.isOp()) return true;
                    csb.allPut();
                }
            }else{
                if(!sender.isOp()) return true;
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
                if(args.length >= 2 && args[0].equalsIgnoreCase("cost")){
                    if(sender.isOp()) {
                        if(args[1].equalsIgnoreCase("create") && args.length == 4){
                            if(createCost(args[2],args[3])){
                                sender.sendMessage(langMap.get("CostCreate"));
                            }else sender.sendMessage(langMap.get("CostCreateNull"));
                        }else if(args[1].equalsIgnoreCase("delete") && args.length == 4){
                            if(deleteCost(args[2],Boolean.getBoolean(args[3]))){
                                sender.sendMessage(langMap.get("CostDelete"));
                            }else sender.sendMessage(langMap.get("CostDeleteNull"));
                        }else if(args[1].equalsIgnoreCase("clear") && args.length == 3){
                            clearCost(args[2]);
                            sender.sendMessage(langMap.get("CostClear"));
                        }else if(args[1].equalsIgnoreCase("rename") && args.length == 4){
                            if(renameCost(args[2],args[3])){
                                sender.sendMessage(langMap.get("CostRename"));
                            }else sender.sendMessage(langMap.get("CostRenameNull"));
                        }else if(args[1].equalsIgnoreCase("list")){
                            sendCostList(sender);
                        }
                    }
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
        if(enableMySQL){
            for(Player player:Bukkit.getOnlinePlayers()){
                String pName = player.getName();
                HashMap<String,Integer> data = playerData.getOrDefault(pName,new HashMap<>());
                csb.deletePlayerData(pName);
                csb.insertData(pName,data);
            }
            csb.closeConnectionQuietly();
        }
    }
    private void setupEconomy(){
        RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = ((Economy)economyProvider.getProvider());
        }
    }
}
