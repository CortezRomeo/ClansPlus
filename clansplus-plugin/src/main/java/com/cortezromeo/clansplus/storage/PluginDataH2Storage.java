package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.enums.IconType;
import com.cortezromeo.clansplus.enums.Rank;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.h2.jdbc.JdbcConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class PluginDataH2Storage implements PluginStorage {
    private static JdbcConnection connection;
    private static String clanTable;
    private static String playerTable;


    public PluginDataH2Storage(String fileName, String clanTableName, String playerTableName) {
        clanTable= clanTableName;
        playerTable = playerTableName;
        try {
            if (connection != null)
                disableStorage();

            connection = new JdbcConnection("jdbc:h2:./" + ClansPlus.plugin.getDataFolder()  + "/" + fileName + ";mode=MySQL", new Properties(), null, null, false);
            connection.setAutoCommit(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + clanTable + " " +
                    "(NAME VARCHAR(500) not NULL, " +
                    " CUSTOMNAME VARCHAR(500), " +
                    " OWNER VARCHAR(500), " +
                    " SCORE INT, " +
                    " WARPOINT INT, " +
                    " WARNING INT, " +
                    " MAXMEMBER INT, " +
                    " CREATEDDATE LONG, " +
                    " ICONTYPE VARCHAR(10), " +
                    " ICONVALUE VARCHAR(100), " +
                    " MEMBERS TEXT, " +
                    " SPAWNPOINTWORLD VARCHAR(500), " +
                    " SPAWNPOINTX DOUBLE, " +
                    " SPAWNPOINTY DOUBLE, " +
                    " SPAWNPOINTZ DOUBLE, " +
                    " ALLIES TEXT, " +
                    " SKILLLEVEL TEXT, " +
                    " PRIMARY KEY (NAME))";
            String sql2 = "CREATE TABLE IF NOT EXISTS " + playerTable + " " +
                    "(PLAYERNAME VARCHAR(500) not NULL, " +
                    " UUID VARCHAR(50), " +
                    " CLAN VARCHAR(500), " +
                    " RANK VARCHAR(10), " +
                    " JOINDATE LONG, " +
                    " SCORECOLLECTED LONG, " +
                    " PRIMARY KEY (PLAYERNAME))";
            statement.executeUpdate(sql);
            MessageUtil.debug("LOADING DATABASE (H2)", "Connected to clan table: " + clanTable);
            statement.executeUpdate(sql2);
            MessageUtil.debug("LOADING DATABASE (H2)", "Connected to player table: " + playerTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JdbcConnection getConnection() {
        return connection;
    }

    @Override
    public ClanData getClanData(String clanName) {
        List<String> members = new ArrayList<>();
        List<String> allies = new ArrayList<>();
        HashMap<Integer, Integer> skillLevel = new HashMap<>();

        ClanData clanData = new ClanData(
                clanName,
                null,
                null,
                0,
                0,
                0,
                Settings.CLAN_SETTING_MAXIMUM_MEMBER_DEFAULT,
                0,
                IconType.valueOf(Settings.CLAN_SETTING_ICON_DEFAULT_TYPE),
                Settings.CLAN_SETTING_ICON_DEFAULT_VALUE,
                members,
                null,
                allies,
                skillLevel);

        if (!isClanDataExisted(clanName))
            return clanData;

        String query = "select * from " + clanTable + " where NAME=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, clanName);
            ResultSet resultSet = ps.executeQuery();
            Gson gson = new Gson();

            while (resultSet.next()) {
                clanData.setCustomName(resultSet.getString("CUSTOMNAME"));
                clanData.setOwner(resultSet.getString("OWNER"));
                clanData.setScore(resultSet.getInt("SCORE"));
                clanData.setWarPoint(resultSet.getInt("WARPOINT"));
                clanData.setWarning(resultSet.getInt("WARNING"));
                clanData.setMaxMember(resultSet.getInt("MAXMEMBER"));
                clanData.setCreatedDate(resultSet.getLong("CREATEDDATE"));
                clanData.setIconType(IconType.valueOf(resultSet.getString("ICONTYPE")));
                clanData.setIconValue(resultSet.getString("ICONVALUE"));
                clanData.setMembers(gson.fromJson(resultSet.getString("MEMBERS"), new TypeToken<List<String>>(){}.getType()));
                if (resultSet.getString("SPAWNPOINTWORLD") != null)
                    clanData.setSpawnPoint(new Location(Bukkit.getWorld(resultSet.getString("SPAWNPOINTWORLD")), resultSet.getDouble("SPAWNPOINTX"), resultSet.getDouble("SPAWNPOINTY"), resultSet.getDouble("SPAWNPOINTZ")));
                clanData.setAllies(gson.fromJson(resultSet.getString("ALLIES"), new TypeToken<List<String>>(){}.getType()));
                clanData.setSkillLevel(gson.fromJson(resultSet.getString("SKILLLEVEL"), new TypeToken<HashMap<Integer, Integer>>(){}.getType()));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return clanData;
    }

    @Override
    public List<String> getAllClans() {
        java.lang.String query = "select NAME from " + clanTable;
        List<String> clans = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                clans.add(resultSet.getString("NAME"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return clans;
    }

    @Override
    public List<String> getAllPlayers() {
        java.lang.String query = "select PLAYERNAME from " + playerTable;
        List<String> players = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                players.add(resultSet.getString("PLAYERNAME"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return players;
    }

    @Override
    public void saveClanData(String clanName, ClanData clanData) {
        if (!isClanDataExisted(clanName))
            initClanData(clanName);

        String query = "UPDATE " + clanTable + " "
                + "SET NAME = ?,"
                + " CUSTOMNAME = ?,"
                + " OWNER = ?,"
                + " SCORE = ?,"
                + " WARPOINT = ?,"
                + " WARNING = ?,"
                + " MAXMEMBER = ?,"
                + " CREATEDDATE = ?,"
                + " ICONTYPE = ?,"
                + " ICONVALUE = ?,"
                + " MEMBERS = ?,"
                + " SPAWNPOINTWORLD = ?,"
                + " SPAWNPOINTX = ?,"
                + " SPAWNPOINTY = ?,"
                + " SPAWNPOINTZ = ?,"
                + " ALLIES = ?,"
                + " SKILLLEVEL = ?"
                + " WHERE NAME = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            Gson gson = new Gson();
            preparedStatement.setString(1, clanData.getName());
            preparedStatement.setString(2, clanData.getCustomName());
            preparedStatement.setString(3, clanData.getOwner());
            preparedStatement.setInt(4, clanData.getScore());
            preparedStatement.setInt(5, clanData.getWarPoint());
            preparedStatement.setInt(6, clanData.getWarning());
            preparedStatement.setInt(7, clanData.getMaxMember());
            preparedStatement.setLong(8, clanData.getCreatedDate());
            preparedStatement.setString(9, clanData.getIconType().toString().toUpperCase());
            preparedStatement.setString(10, clanData.getIconValue());
            preparedStatement.setString(11, gson.toJson(clanData.getMembers()));
            if (clanData.getSpawnPoint() != null) {
                preparedStatement.setString(12, clanData.getSpawnPoint().getWorld().getName());
                preparedStatement.setDouble(13, clanData.getSpawnPoint().getX());
                preparedStatement.setDouble(14, clanData.getSpawnPoint().getY());
                preparedStatement.setDouble(15, clanData.getSpawnPoint().getZ());
            } else {
                preparedStatement.setString(12, null);
                preparedStatement.setDouble(13, 0);
                preparedStatement.setDouble(14, 0);
                preparedStatement.setDouble(15, 0);
            }
            preparedStatement.setString(16, gson.toJson(clanData.getAllies()));
            preparedStatement.setString(17, gson.toJson(clanData.getSkillLevel()));
            preparedStatement.setString(18, clanData.getName());
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public PlayerData getPlayerData(String playerName) {
        PlayerData playerData = new PlayerData(playerName, (Bukkit.getPlayer(playerName) != null ? Bukkit.getPlayer(playerName).getUniqueId().toString() : null), null, null, 0, 0);

        if (!isPlayerDataExisted(playerName))
            return playerData;

        String query = "select * from " + playerTable + " where PLAYERNAME=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, playerName);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                playerData.setPlayerName(resultSet.getString("PLAYERNAME"));
                if (resultSet.getString("UUID") == null) {
                    if (Bukkit.getPlayer(playerName) != null)
                        playerData.setUUID(Bukkit.getPlayer(playerName).getUniqueId().toString());
                } else
                    playerData.setUUID(resultSet.getString("UUID"));
                playerData.setClan(resultSet.getString("CLAN"));
                playerData.setRank(Rank.valueOf(resultSet.getString("RANK")));
                playerData.setJoinDate(resultSet.getLong("JOINDATE"));
                playerData.setScoreCollected(resultSet.getLong("SCORECOLLECTED"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return playerData;
    }

    @Override
    public void savePlayerData(String playerName, PlayerData playerData) {
        if (!isPlayerDataExisted(playerName))
            initPlayerData(playerName);

        String query = "UPDATE " + playerTable + " "
                + "SET PLAYERNAME = ?,"
                + " UUID = ?,"
                + " CLAN = ?,"
                + " RANK = ?,"
                + " JOINDATE = ?,"
                + " SCORECOLLECTED = ?"
                + " WHERE PLAYERNAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, playerData.getUUID());
            preparedStatement.setString(3, playerData.getClan());
            preparedStatement.setString(4, playerData.getRank().toString().toUpperCase());
            preparedStatement.setLong(5, playerData.getJoinDate());
            preparedStatement.setLong(6, playerData.scoreCollected);
            preparedStatement.setString(7, playerName);
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static boolean isClanDataExisted(String clanName) {
        String query = "select * from " + clanTable + " where NAME=?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, clanName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            rs.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private static boolean isPlayerDataExisted(String playerName) {
        String query = "select * from " + playerTable + " where PLAYERNAME=?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            rs.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private static void initClanData(String clanName) {
        ArrayList<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + clanTable + " (" +
                "NAME," +
                " CUSTOMNAME," +
                " OWNER," +
                " SCORE," +
                " WARPOINT," +
                " WARNING," +
                " MAXMEMBER," +
                " CREATEDDATE," +
                " ICONTYPE," +
                " ICONVALUE," +
                " MEMBERS," +
                " SPAWNPOINTWORLD," +
                " SPAWNPOINTX," +
                " SPAWNPOINTY," +
                " SPAWNPOINTZ," +
                " ALLIES," +
                " SKILLLEVEL) values(" +
                "'" + clanName + "'," +
                " ''," +
                " ''," +
                " 0," +
                " 0," +
                " 0," +
                " 0," +
                " 0," +
                " ''," +
                " ''," +
                " ''," +
                " ''," +
                " 0," +
                " 0," +
                " 0," +
                " ''," +
                " '')");
        queries.forEach(cmd -> {
            try (PreparedStatement ps = connection.prepareStatement(cmd)) {
                ps.execute();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void initPlayerData(String playerName) {
        ArrayList<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + playerTable + " (PLAYERNAME, UUID, CLAN, RANK, JOINDATE, SCORECOLLECTED) values('" + playerName + "', '', '', '', 0, 0)");
        queries.forEach(cmd -> {
            try (PreparedStatement ps = connection.prepareStatement(cmd)) {
                ps.execute();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void disableStorage() {
        try {
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
