package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.enums.DatabaseType;
import com.cortezromeo.clansplus.util.FileNameUtil;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

public class PluginDataManager {

    public static HashMap<String, PlayerData> playerDatabase = new HashMap<>();
    public static TreeMap<String, BangHoiData> bangHoiDatabase = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static HashMap<String, PlayerData> getPlayerDatabase() {
        return playerDatabase;
    }

    public static TreeMap<String, BangHoiData> getBangHoiDatabase() {
        return bangHoiDatabase;
    }

    public static BangHoiData getBangHoiDatabase(String bangHoiName) {
        return bangHoiDatabase.get(bangHoiName);
    }

    public static PlayerData getPlayerDatabase(String playerName) {
        return playerDatabase.get(playerName);
    }

    public static void loadBangHoiDatabase(String bangHoiName) {
        if (getBangHoiDatabase().containsKey(bangHoiName))
            return;
        getBangHoiDatabase().put(bangHoiName, PluginDataStorage.getBangHoiData(bangHoiName));
    }

    public static void loadPlayerDatabase(String playerName) {
        if (getPlayerDatabase().containsKey(playerName))
            return;
        getPlayerDatabase().put(playerName, PluginDataStorage.getPlayerData(playerName));
    }

    public static void saveBangHoiDatabaseToHashMap(String bangHoiName, BangHoiData bangHoiData) {
        getBangHoiDatabase().put(bangHoiName, bangHoiData);
    }

    public static void savePlayerDatabaseToHashMap(String playerName, PlayerData playerData) {
        getPlayerDatabase().put(playerName, playerData);
    }

    public static void saveBangHoiDatabaseToStorage(String bangHoiName, BangHoiData bangHoiData) {
        saveBangHoiDatabaseToHashMap(bangHoiName, bangHoiData);
        PluginDataStorage.saveBangHoiData(bangHoiName, bangHoiData);
    }

    public static void saveBangHoiDatabaseToStorage(String bangHoiName) {
        if (getBangHoiDatabase().containsKey(bangHoiName))
            PluginDataStorage.saveBangHoiData(bangHoiName, getBangHoiDatabase().get(bangHoiName));
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
        if (ClansPlus.databaseType == DatabaseType.YAML) {
            File bangHoiFolder = new File(ClansPlus.plugin.getDataFolder() + "/banghoiData");
            File[] listOfFilesBangHoi = bangHoiFolder.listFiles();

            if (listOfFilesBangHoi == null)
                return;

            for (File file : listOfFilesBangHoi) {
                try {
                    if (file.isFile()) {
                        String bangHoiName = FileNameUtil.removeExtension(file.getName());
                        loadBangHoiDatabase(bangHoiName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            File playerFolder = new File(ClansPlus.plugin.getDataFolder() + "/playerData");
            File[] listOfFilesPlayer = playerFolder.listFiles();
            if (listOfFilesPlayer == null) return;

            for (File file : listOfFilesPlayer) {
                try {
                    if (file.isFile()) {
                        String playerName = FileNameUtil.removeExtension(file.getName());
                        loadPlayerDatabase(playerName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        fixIllegalDatabase();
    }

    public static void fixIllegalDatabase() {
        if (!getBangHoiDatabase().isEmpty())
            for (String bangHoiName : getBangHoiDatabase().keySet()) {
                boolean illegal = false;
                BangHoiData bangHoiData = getBangHoiDatabase(bangHoiName);

                for (String memberName : bangHoiData.getMembers()) {
                    PlayerData playerData = getPlayerDatabase(memberName);
                    if (!playerData.getClan().equals(bangHoiName)) {
                        bangHoiData.getMembers().remove(memberName);

                        if (!getBangHoiDatabase().containsKey(playerData.getClan()))
                            clearPlayerDatabase(memberName);
                        illegal = true;
                    }
                }
                if (illegal)
                    saveBangHoiDatabaseToStorage(bangHoiName, bangHoiData);
            }

        if (!getPlayerDatabase().isEmpty())
            for (String playerName : getPlayerDatabase().keySet()) {
                PlayerData playerData = getPlayerDatabase(playerName);

                if (!getBangHoiDatabase().containsKey(playerData.getClan()))
                   clearPlayerDatabase(playerName);
            }
    }

}
