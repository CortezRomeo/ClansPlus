package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.enums.DatabaseType;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class PluginDataManager {

    public static HashMap<String, PlayerData> playerDatabase = new HashMap<>();
    public static TreeMap<String, ClanData> clanDatabase = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static HashMap<String, PlayerData> getPlayerDatabase() {
        return playerDatabase;
    }

    public static TreeMap<String, ClanData> getClanDatabase() {
        return clanDatabase;
    }

    public static ClanData getClanDatabase(String clanName) {
        return clanDatabase.get(clanName);
    }

    public static PlayerData getPlayerDatabase(String playerName) {
        return playerDatabase.get(playerName);
    }

    public static void loadClanDatabase(String clanName) {
        if (getClanDatabase().containsKey(clanName))
            return;
        getClanDatabase().put(clanName, PluginDataStorage.getClanData(clanName));
    }

    public static void loadPlayerDatabase(String playerName) {
        if (getPlayerDatabase().containsKey(playerName))
            return;
        getPlayerDatabase().put(playerName, PluginDataStorage.getPlayerData(playerName));
    }

    public static void saveClanDatabaseToHashMap(String clanName, ClanData clanData) {
        getClanDatabase().put(clanName, clanData);
    }

    public static void savePlayerDatabaseToHashMap(String playerName, PlayerData playerData) {
        getPlayerDatabase().put(playerName, playerData);
    }

    public static void saveClanDatabaseToStorage(String clanName, ClanData clanData) {
        saveClanDatabaseToHashMap(clanName, clanData);
        PluginDataStorage.saveClanData(clanName, clanData);
    }

    public static void saveClanDatabaseToStorage(String clanName) {
        if (getClanDatabase().containsKey(clanName))
            PluginDataStorage.saveClanData(clanName, getClanDatabase().get(clanName));
    }

    public static void savePlayerDatabaseToStorage(String playerName, PlayerData playerData) {
        savePlayerDatabaseToHashMap(playerName, playerData);
        PluginDataStorage.savePlayerData(playerName, playerData);
    }

    public static void savePlayerDatabaseToStorage(String playerName) {
        if (getPlayerDatabase().containsKey(playerName))
            PluginDataStorage.savePlayerData(playerName, getPlayerDatabase().get(playerName));
    }

    public static void clearPlayerDatabase(String playerName) {
        if (!getPlayerDatabase().containsKey(playerName))
            return;
        getPlayerDatabase(playerName).setClan(null);
        getPlayerDatabase(playerName).setRank(null);
        getPlayerDatabase(playerName).setJoinDate(0);
        getPlayerDatabase(playerName).setScoreCollected(0);
    }

    public static void transferDatabase(CommandSender commandSender, DatabaseType toDatabaseType) {
        if (ClansPlus.databaseType != toDatabaseType) {
            if (commandSender != null)
                commandSender.sendMessage("Transferring database from " + ClansPlus.databaseType + " to " + toDatabaseType + "...");
            Bukkit.getScheduler().runTaskAsynchronously(ClansPlus.plugin, () -> {
                PluginDataStorage.disableStorage();
                PluginDataStorage.init(toDatabaseType);
                if (!PluginDataManager.getClanDatabase().isEmpty()) {
                    for (String clanName : PluginDataManager.getClanDatabase().keySet()) {
                        PluginDataManager.saveClanDatabaseToStorage(clanName);
                    }
                }
                if (!PluginDataManager.getPlayerDatabase().isEmpty()) {
                    for (String playerName : PluginDataManager.getPlayerDatabase().keySet()) {
                        PluginDataManager.savePlayerDatabaseToStorage(playerName);
                    }
                }
                ClansPlus.databaseType = toDatabaseType;

                // config.yml
                ClansPlus.plugin.saveDefaultConfig();
                File configFile = new File(ClansPlus.plugin.getDataFolder(), "config.yml");
                ClansPlus.plugin.getConfig().set("database.type", toDatabaseType.toString().toUpperCase());
                try {
                    ClansPlus.plugin.getConfig().save(configFile);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    ConfigUpdater.update(ClansPlus.plugin, "config.yml", configFile,  "clan-settings.creating-clan-settings.skill-level-default", "database");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ClansPlus.plugin.reloadConfig();

                if (commandSender != null)
                    commandSender.sendMessage("Successfully transferred database from " + ClansPlus.databaseType + " to " + toDatabaseType);
            });
        }
    }

    public static void loadAllDatabase() {
        if (!PluginDataStorage.getAllClans().isEmpty())
            for (String clanName : PluginDataStorage.getAllClans()) {
                loadClanDatabase(clanName);
                if (Settings.DATABASE_SETTING_SMART_LOADING_ENABLED) {
                    ClanData clanData = getClanDatabase(clanName);
                    for (String playerName : clanData.getMembers()) {
                        loadPlayerDatabase(playerName);
                    }
                }
            }

        if (!Settings.DATABASE_SETTING_SMART_LOADING_ENABLED) {
            if (!PluginDataStorage.getAllPlayers().isEmpty())
                for (String playerName : PluginDataStorage.getAllPlayers())
                    loadPlayerDatabase(playerName);
        }
        //fixIllegalDatabase();
    }

    public static void fixIllegalDatabase() {
        if (!getClanDatabase().isEmpty())
            for (String clanName : getClanDatabase().keySet()) {
                boolean illegal = false;
                ClanData clanData = getClanDatabase(clanName);

                if (!clanData.getMembers().isEmpty()) {
                    for (String memberName : clanData.getMembers()) {
                        PlayerData playerData = getPlayerDatabase(memberName);
                        if (!playerData.getClan().equals(clanName)) {
                            clanData.getMembers().remove(memberName);

                            if (!getClanDatabase().containsKey(playerData.getClan()))
                                clearPlayerDatabase(memberName);
                            illegal = true;
                        }
                    }
                }
                if (illegal)
                    saveClanDatabaseToStorage(clanName, clanData);
            }

        if (!getPlayerDatabase().isEmpty())
            for (String playerName : getPlayerDatabase().keySet()) {
                PlayerData playerData = getPlayerDatabase(playerName);

                if (!getClanDatabase().containsKey(playerData.getClan()))
                   clearPlayerDatabase(playerName);
            }
    }

}
