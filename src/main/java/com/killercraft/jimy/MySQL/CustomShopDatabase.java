package com.killercraft.jimy.MySQL;

import com.killercraft.jimy.CustomShop;
import com.killercraft.jimy.Manager.GuiShop;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

import static com.killercraft.jimy.ConfigManager.CSConfig.load;
import static com.killercraft.jimy.CustomShop.*;
import static com.killercraft.jimy.MySQL.CSUnicodeUtil.toUnicode;
import static com.killercraft.jimy.MySQL.CSUnicodeUtil.toWord;

public class CustomShopDatabase {
    public static boolean enableMySQL = false;
    private String type = "MySQL";
    public String host;
    public String port;
    public String databaseName;
    public String username;
    public String password;
    public boolean useSSL;
    //private SqlService sql;
    private String jdbcURL;

    private Connection conn;

    private String getJdbcURL() {
        if (jdbcURL == null) {
            StringBuilder builder = new StringBuilder("jdbc:").append(type.toLowerCase()).append("://");
            if ("MySQL".equalsIgnoreCase(type)) {
                //jdbc:<engine>://[<username>[:<password>]@]<host>/<database>
                builder.append(host)//.append('@').append(username).append(':').append(password)
                        .append(':').append(port).append('/')
                        .append(databaseName).append("?useSSL").append('=').append(useSSL);
            }
            jdbcURL = builder.toString();
        }
        return jdbcURL;
    }

    public Connection getConnection() throws SQLException {
        conn = DriverManager.getConnection(getJdbcURL(),username,password);
        return conn;
        //return DriverManager.getConnection(getJdbcURL());
    }

    public void createLimitTable() throws SQLException {
        try {
            boolean tableExists = false;
            try {
                //check if the table already exists
                Statement statement = conn.createStatement();
                statement.execute("SELECT 1 FROM `limits`");
                statement.close();

                tableExists = true;
            } catch (SQLException sqlEx) {
                System.out.print("[CustomShop]Create Limit Table!");
            }


            if (!tableExists) {
                if ("MySQL".equalsIgnoreCase(type)){
                    Statement statement = conn.createStatement();
                    statement.execute("CREATE TABLE `limits`  (" +
                            "  `playername` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `limitkey` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `num` tinyint(10) NULL DEFAULT NULL" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException e){
        }
    }

    public void createPlayerDataTable() throws SQLException {
        try {
            boolean tableExists = false;
            try {
                //check if the table already exists
                Statement statement = conn.createStatement();
                statement.execute("SELECT 1 FROM `playerdata`");
                statement.close();

                tableExists = true;
            } catch (SQLException sqlEx) {
                System.out.print("[CustomShop]Create Player Data Table!");
            }


            if (!tableExists) {
                if ("MySQL".equalsIgnoreCase(type)){
                    Statement statement = conn.createStatement();
                    statement.execute("CREATE TABLE `playerdata`  (" +
                            "  `playername` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `costid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `costbal` bigint(19) NULL DEFAULT NULL" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException e){
        }
    }

    public void createRefreshTable() throws SQLException {
        try {
            boolean tableExists = false;
            try {
                //check if the table already exists
                Statement statement = conn.createStatement();
                statement.execute("SELECT 1 FROM `refreshshops`");
                statement.close();

                tableExists = true;
            } catch (SQLException sqlEx) {
                System.out.print("[CustomShop]Create Refresh Table!");
            }

            if (!tableExists) {
                if ("MySQL".equalsIgnoreCase(type)){
                    Statement statement = conn.createStatement();
                    statement.execute("CREATE TABLE `refreshshops`  (" +
                            "  `shopname` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `day` int(3) NULL DEFAULT NULL," +
                            "  UNIQUE INDEX `shopname`(`shopname`) USING BTREE" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException e){
        }
    }

    public void createCostsTable() throws SQLException {
        try {

            boolean tableExists = false;
            try {
                //check if the table already exists
                Statement statement = conn.createStatement();
                statement.execute("SELECT 1 FROM `costs`");
                statement.close();

                tableExists = true;
            } catch (SQLException sqlEx) {
                System.out.print("[CustomShop]Create Costs Table!");
            }

            if (!tableExists) {
                if ("MySQL".equalsIgnoreCase(type)){
                    Statement statement = conn.createStatement();
                    statement.execute("CREATE TABLE `costs`  (" +
                            "  `costid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `costname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  UNIQUE INDEX `costid`(`costid`) USING BTREE" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException e){
        }
    }


    public void createShopsTable() throws SQLException {
        try {

            boolean tableExists = false;
            try {
                Statement statement = conn.createStatement();
                statement.execute("SELECT 1 FROM `shops`");
                statement.close();

                tableExists = true;
            } catch (SQLException sqlEx) {
                System.out.print("[CustomShop]Create Shop Table!");
            }

            if (!tableExists) {
                if ("MySQL".equalsIgnoreCase(type)){
                    Statement statement = conn.createStatement();
                    statement.execute("CREATE TABLE `shops`  (" +
                            "  `shopname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `shopline` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `shopitems` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL," +
                            "  UNIQUE INDEX `shopname`(`shopname`) USING BTREE" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException ex) {
        }
    }


    public void deleteKey(String key){
        try {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `limits` WHERE `limitkey`='"+key+"'");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }
    public void deleteLimitData(String playerName){
        try {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `limits` WHERE `playername`='"+toUnicode(playerName)+"'");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }

    public void deleteShopData(String shopName){
        shopName = toUnicode(shopName);
        try {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `shops` WHERE `shopname`='"+shopName+"'");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }

    public void allPut(){
        HashSet<String> shopNames = new HashSet<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `shops`");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                shopNames.add(toWord(result.getString(1)));
            }
            result.close();
            statement.close();
        } catch (SQLException ignored) {
        }
        customShops = new HashMap<>();
        for(String name:shopNames){
            customShops.put(name,selectShop(name));
        }
        costMap = new HashMap<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `costs`");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                costMap.put(result.getString(1),toWord(result.getString(2)));
            }
            result.close();
            statement.close();
        } catch (SQLException ignored) {
        }
        File file = new File(CustomShop.root, "config.yml");
        FileConfiguration config = load(file);
        for(String id:costMap.keySet()) {
            config.set("Costs." + id, costMap.get(id).replace(ChatColor.COLOR_CHAR, '&'));
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllShop(){
        try {
            PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE `shops`");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }



    public void deleteAllRefresh(){
        try {
            PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE `refreshshops`");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }

    public void deleteAllPlayerData(){
        try {
            PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE `playerdata`");
            statement.execute();
        } catch (SQLException ex) {
        }
    }

    public void deletePlayerData(String playerNameb){
        String playerName = toUnicode(playerNameb);
        try {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `playerdata` WHERE `playername`='"+playerName+"'");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }

    public void insertData(String pNameb,HashMap<String,Integer> data) {
        String pName = toUnicode(pNameb);
        try {
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `playerdata` " +
                    "(`playername`, `costid`, `costbal`) VALUES (?,?,?)");
            prepareStatement.setString(1,pName);
            for(String id:data.keySet()){
                prepareStatement.setString(2, id);
                prepareStatement.setInt(3, data.get(id));
                prepareStatement.execute();
            }
            prepareStatement.close();
        } catch (SQLException sqlEx) {
        }
    }


    public void deleteRefresh(String shopNameOld){
        String shopName = toUnicode(shopNameOld);
        try {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `refreshshops` WHERE `shopname`='"+shopName+"'");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }

    public boolean deleteCosts(String costId){
        try {
            if(getCosts().containsKey(costId)) {
                PreparedStatement statement = conn.prepareStatement("DELETE FROM `costs` WHERE `costid`='"+costId+"'");
                statement.execute();
                clearCost(costId);
                statement.close();
                return true;
            }
        } catch (SQLException ex) {
        }
        return false;
    }

    public void clearCost(String costId){
        try {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `playerdata` WHERE `costid`='"+costId+"'");
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
        }
    }

    public GuiShop selectShop(String shopName){
        shopName = toUnicode(shopName);
        GuiShop gs = null;
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `shops` WHERE `shopname`='"+shopName+"'");
            ResultSet result = statement.executeQuery();
            if(result.next()){
                gs = new GuiShop(result);
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
        }
        return gs;
    }

    public int selectRefresh(String shopNameOld){
        String shopName = toUnicode(shopNameOld);
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `refreshshops` WHERE `shopname`='"+shopName+"'");
            ResultSet result = statement.executeQuery();
            if(result.next()){
                int a = result.getInt(2);
                result.close();
                statement.close();
                return a;
            }
            result.close();
            statement.close();
            return -1;
        } catch (SQLException ex) {
        }
        return -1;
    }

    public HashMap<String,String> getCosts(){
        HashMap<String,String> costs = new HashMap<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `costs`");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                costs.put(result.getString(1),toWord(result.getString(2)));
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
        }
        return costs;
    }

    public HashMap<String,Integer> selectPlayerData(String playerName){
        playerName = toUnicode(playerName);
        HashMap<String,Integer> costs = new HashMap<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `playerdata` WHERE `playername`='"+playerName+"'");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                costs.put(result.getString(2),result.getInt(3));
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
        }
        return costs;
    }

    public HashMap<String,GuiShop> getShops(){
        GuiShop gs;
        HashMap<String,GuiShop> shops = new HashMap<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `shops`");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                gs = new GuiShop(result);
                shops.put(gs.getShopName(),gs);
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
        }
        return shops;
    }

    public void insertShop(GuiShop gs) {
        try {
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `shops` " +
                    "(`shopname`, `shopline`, `shopitems`) VALUES (?,?,?)");
            prepareStatement.setString(1, toUnicode(gs.getShopName()));
            prepareStatement.setInt(2, gs.getLine());
            prepareStatement.setString(3, gs.getItemsString());
            prepareStatement.execute();
            prepareStatement.close();
        } catch (SQLException sqlEx) {
        }
    }

    public void insertRefresh(String shopNameOld,int day) {
        String shopName = toUnicode(shopNameOld);
        try {
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `refreshshops` " +
                    "(`shopname`, `day`) VALUES (?,?)");
            prepareStatement.setString(1, shopName);
            prepareStatement.setInt(2, day);
            prepareStatement.execute();
            prepareStatement.close();
        } catch (SQLException sqlEx) {
        }
    }

    public HashMap<String,Integer> getLimits(String playerName){
        HashMap<String,Integer> limits = new HashMap<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `limits` WHERE `playername`='"+toUnicode(playerName)+"'");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                limits.put(result.getString(2),result.getInt(3));
            }
            result.close();
            statement.close();
        } catch (SQLException ex) {
        }
        return limits;
    }

    public void insertLimits(String playerName) {
        if(limitData.containsKey(playerName)) {
            HashMap<String,Integer> limit = limitData.get(playerName);
            try {
                PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `limits` " +
                        "(`playername`, `limitkey`, `num`) VALUES (?,?,?)");
                prepareStatement.setString(1, toUnicode(playerName));
                for(String key:limit.keySet()){
                    prepareStatement.setString(2, key);
                    prepareStatement.setInt(3, limit.get(key));
                    prepareStatement.execute();
                }
                prepareStatement.close();
            } catch (SQLException sqlEx) {
            }
        }
    }

    public void insertCost(String playerName, String costId, int bal) {
        if(bal <= 0) return;
        try {
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `playerdata` " +
                    "(`playername`, `costid`, `costbal`) VALUES (?,?,?)");
            prepareStatement.setString(1, toUnicode(playerName));
            prepareStatement.setString(2, costId);
            prepareStatement.setInt(3, bal);
            prepareStatement.execute();
            prepareStatement.close();
        } catch (SQLException sqlEx) {
        }
    }

    public boolean insertNewCost(String costId,String costName) {
        costName = toUnicode(costName);
        try {
            if(getCosts().containsKey(costId)) return false;
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `costs` " +
                    "(`costid`, `costname`) VALUES (?,?)");
            prepareStatement.setString(1, costId);
            prepareStatement.setString(2, costName);
            prepareStatement.execute();
            prepareStatement.close();
            return true;
        } catch (SQLException sqlEx) {
        }
        return false;
    }


    public void updateShop(GuiShop gs) {
        try {

            PreparedStatement prepareStatement = conn.prepareStatement("UPDATE `shops`"
                    + " SET `shopitems`=? WHERE `shopname`=?");

            prepareStatement.setString(1, gs.getItemsString());
            prepareStatement.setString(2, toUnicode(gs.getShopName()));

            prepareStatement.execute();
            prepareStatement.close();
        } catch (SQLException ex){
        }
    }

    public boolean updateCost(String costId,String costName) {
        costName = toUnicode(costName);
        try {
            if(getCosts().containsKey(costId)) {
                PreparedStatement prepareStatement = conn.prepareStatement("UPDATE `costs`"
                        + " SET `costname`=? WHERE `costid`=?");

                prepareStatement.setString(1, costName);
                prepareStatement.setString(2, costId);
                prepareStatement.execute();
                prepareStatement.close();
                return true;
            }
        } catch (SQLException ex){
        }
        return false;
    }

    public void closeConnectionQuietly() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                //
            }
        }
    }
}
