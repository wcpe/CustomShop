package com.killercraft.jimy.MySQL;

import com.killercraft.jimy.Manager.GuiShop;

import java.sql.*;
import java.util.HashMap;

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
    //private SqlService sql;
    private String jdbcURL;

    private String getJdbcURL() {
        if (jdbcURL == null) {
            StringBuilder builder = new StringBuilder("jdbc:").append(type.toLowerCase()).append("://");
            if ("MySQL".equalsIgnoreCase(type)) {
                //jdbc:<engine>://[<username>[:<password>]@]<host>/<database>
                builder.append(host)//.append('@').append(username).append(':').append(password)
                        .append(':').append(port).append('/')
                        .append(databaseName);//.append("?useSSL").append('=').append(useSSL);
            }
            jdbcURL = builder.toString();
        }
        return jdbcURL;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcURL(),username,password);
        //return DriverManager.getConnection(getJdbcURL());
    }

    public void createPlayerDataTable() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();

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
                            "  `playername` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `costid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `costbal` bigint(19) NULL DEFAULT NULL" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException e){
            System.out.print("[CustomShop]MySQL create error");
            closeQuietly(conn);
        }
    }

    public void createRefreshTable() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();

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
                            "  `shopname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL," +
                            "  `day` int(3) NULL DEFAULT NULL," +
                            "  UNIQUE INDEX `shopname`(`shopname`) USING BTREE" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException e){
            System.out.print("[CustomShop]MySQL create error");
            closeQuietly(conn);
        }
    }

    public void createCostsTable() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();

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
            System.out.print("[CustomShop]MySQL create error");
            closeQuietly(conn);
        }
    }


    public void createShopsTable() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();

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
                            "  `shopitems` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL," +
                            "  UNIQUE INDEX `shopname`(`shopname`) USING BTREE" +
                            ")");
                    statement.close();
                }
            }
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL create error");
            closeQuietly(conn);
        }
    }

    public void deleteShopData(String shopName){
        shopName = toUnicode(shopName);
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `shops` WHERE `shopname`='"+shopName+"'");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public void deleteAllShop(){
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE `shops`");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public void deleteAllRefresh(){
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE `refreshshops`");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public void deleteAllPlayerData(){
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE `playerdata`");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public void deleteRefresh(String shopName){
        shopName = toUnicode(shopName);
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `refreshshops` WHERE `shopname`='"+shopName+"'");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public boolean deleteCosts(String costId){
        Connection conn = null;
        try {
            conn = getConnection();
            if(getCosts().containsKey(costId)) {
                PreparedStatement statement = conn.prepareStatement("DELETE FROM `costs` WHERE `costid`='"+costId+"'");
                statement.execute();
                clearCost(costId);
                return true;
            }
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
        return false;
    }

    public void clearPlayerData(String playerName){
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `playerdata` WHERE `playername`='"+playerName+"'");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public void clearCost(String costId){
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `playerdata` WHERE `costid`='"+costId+"'");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public void clearPlayerCost(String playerName,String costId){
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("DELETE FROM `playerdata` WHERE `playername`='"+playerName+"' AND `costid`='"+costId+"'");
            statement.execute();
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL delete error");
            closeQuietly(conn);
        }
    }

    public GuiShop selectShop(String shopName){
        shopName = toUnicode(shopName);
        Connection conn = null;
        GuiShop gs = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `shops` WHERE `shopname`='"+shopName+"'");
            ResultSet result = statement.executeQuery();
            if(result.next()){
                gs = new GuiShop(result);
            }
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL select error");
            closeQuietly(conn);
        }
        return gs;
    }

    public int selectRefresh(String shopName){
        shopName = toUnicode(shopName);
        System.out.print(shopName);
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `refreshshops` WHERE `shopname`='"+shopName+"'");
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return result.getInt(2);
            }
            return -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.print("[CustomShop]MySQL select error");
            closeQuietly(conn);
        }
        return -1;
    }

    public HashMap<String,String> getCosts(){
        Connection conn = null;
        HashMap<String,String> costs = new HashMap<>();
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `costs`");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                costs.put(result.getString(1),toWord(result.getString(2)));
            }
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL select error");
            closeQuietly(conn);
        }
        return costs;
    }

    public HashMap<String,Integer> selectPlayerData(String playerName){
        Connection conn = null;
        HashMap<String,Integer> costs = new HashMap<>();
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `playerdata` WHERE `playername`='"+playerName+"'");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                costs.put(result.getString(2),result.getInt(3));
            }
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL select error");
            closeQuietly(conn);
        }
        return costs;
    }

    public HashMap<String,HashMap<String,Integer>> selectAllPlayerData(){
        Connection conn = null;
        HashMap<String,HashMap<String,Integer>> data = new HashMap<>();
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `playerdata`");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                String playerName = result.getString(1);
                String costId = result.getString(2);
                int costNum = result.getInt(3);
                HashMap<String,Integer> costs = new HashMap<>();
                if(data.containsKey(playerName)){
                    costs = data.get(playerName);
                }
                costs.put(costId,costNum);
                data.put(playerName,costs);
            }
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL select error");
            closeQuietly(conn);
        }
        return data;
    }

    public HashMap<String,GuiShop> getShops(){
        Connection conn = null;
        GuiShop gs;
        HashMap<String,GuiShop> shops = new HashMap<>();
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `shops`");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                gs = new GuiShop(result);
                shops.put(gs.getShopName(),gs);
            }
        } catch (SQLException ex) {
            System.out.print("[CustomShop]MySQL select error");
            closeQuietly(conn);
        }
        return shops;
    }

    public void insertShop(GuiShop gs) {
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `shops` " +
                    "(`shopname`, `shopline`, `shopitems`) VALUES (?,?,?)");
            prepareStatement.setString(1, toUnicode(gs.getShopName()));
            prepareStatement.setInt(2, gs.getLine());
            prepareStatement.setString(3, gs.getItemsString());
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            System.out.print("[CustomShop]MySQL insert shop error");
            closeQuietly(conn);
        }
    }

    public void insertRefresh(String shopName,int day) {
        shopName = toUnicode(shopName);
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `refreshshops` " +
                    "(`shopname`, `day`) VALUES (?,?)");
            prepareStatement.setString(1, shopName);
            prepareStatement.setInt(2, day);
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            System.out.print("[CustomShop]MySQL insert refresh error");
            closeQuietly(conn);
        }
    }

    public void insertCost(String playerName, String costId, int bal) {
        if(bal <= 0) return;
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `playerdata` " +
                    "(`playername`, `costid`, `costbal`) VALUES (?,?,?)");
            prepareStatement.setString(1, playerName);
            prepareStatement.setString(2, costId);
            prepareStatement.setInt(3, bal);
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            System.out.print("[CustomShop]MySQL insert data error player name is :"+playerName);
            closeQuietly(conn);
        }
    }

    public boolean insertNewCost(String costId,String costName) {
        costName = toUnicode(costName);
        Connection conn = null;
        try {
            conn = getConnection();
            if(getCosts().containsKey(costId)) return false;
            PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO `costs` " +
                    "(`costid`, `costname`) VALUES (?,?)");
            prepareStatement.setString(1, costId);
            prepareStatement.setString(2, costName);
            prepareStatement.execute();
            return true;
        } catch (SQLException sqlEx) {
            System.out.print("[CustomShop]MySQL insert cost error");
            closeQuietly(conn);
        }
        return false;
    }


    public void updateShop(GuiShop gs) {
        Connection conn = null;
        try {
            conn = getConnection();

            PreparedStatement prepareStatement = conn.prepareStatement("UPDATE `shops`"
                    + " SET `shopitems`=? WHERE `shopname`=?");

            prepareStatement.setString(1, gs.getItemsString());
            prepareStatement.setString(2, toUnicode(gs.getShopName()));

            prepareStatement.execute();
        } catch (SQLException ex){
            System.out.print("[CustomShop]MySQL update error");
            closeQuietly(conn);
        }
    }

    public boolean updateCost(String costId,String costName) {
        costName = toUnicode(costName);
        Connection conn = null;
        try {
            conn = getConnection();
            if(getCosts().containsKey(costId)) {
                PreparedStatement prepareStatement = conn.prepareStatement("UPDATE `costs`"
                        + " SET `costname`=? WHERE `costid`=?");

                prepareStatement.setString(1, costName);
                prepareStatement.setString(2, costId);
                prepareStatement.execute();
                return true;
            }
        } catch (SQLException ex){
            System.out.print("[CustomShop]MySQL update error");
            closeQuietly(conn);
        }
        return false;
    }

    public void updatePlayerCost(String playerName,String costId,int newBal) {
        if(newBal <= 0) {
            clearPlayerCost(playerName,costId);
            return;
        }
        Connection conn = null;
        try {
            conn = getConnection();

            PreparedStatement prepareStatement = conn.prepareStatement("UPDATE `playerdata`"
                    + " SET `costbal`="+newBal+" WHERE `playername`=? AND `costid`=?");

            prepareStatement.setString(1, playerName);
            prepareStatement.setString(2, costId);

            prepareStatement.execute();
        } catch (SQLException ex){
            System.out.print("[CustomShop]MySQL update error");
            closeQuietly(conn);
        }
    }


    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                //
            }
        }
    }
}
