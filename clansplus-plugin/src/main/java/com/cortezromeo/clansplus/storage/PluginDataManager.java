package com.cortezromeo.clansplus.storage;

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

    public static void loadAllDatabase() {
        if (!PluginDataStorage.getAllClans().isEmpty())
            for (String clanName : PluginDataStorage.getAllClans())
                loadClanDatabase(clanName);

        if (!PluginDataStorage.getAllPlayers().isEmpty())
            for (String playerName : PluginDataStorage.getAllPlayers())
                loadPlayerDatabase(playerName);
        fixIllegalDatabase();
    }

    public static void fixIllegalDatabase() {
        if (!getClanDatabase().isEmpty())
            for (String clanName : getClanDatabase().keySet()) {
                boolean illegal = false;
                ClanData clanData = getClanDatabase(clanName);

                for (String memberName : clanData.getMembers()) {
                    PlayerData playerData = getPlayerDatabase(memberName);
                    if (!playerData.getClan().equals(clanName)) {
                        clanData.getMembers().remove(memberName);

                        if (!getClanDatabase().containsKey(playerData.getClan()))
                            clearPlayerDatabase(memberName);
                        illegal = true;
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
