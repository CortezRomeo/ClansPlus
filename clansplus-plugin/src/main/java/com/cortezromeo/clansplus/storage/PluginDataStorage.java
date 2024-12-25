package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.enums.DatabaseType;

import java.io.File;
import java.util.List;

public class PluginDataStorage {

    private static PluginStorage STORAGE;

    public static void init(DatabaseType databaseType) {
        if (databaseType == DatabaseType.YAML) {
            File clanDataFolder = new File(ClansPlus.plugin.getDataFolder() + "/banghoiData/");
            if (!clanDataFolder.exists()) {
                clanDataFolder.mkdirs();
            }

            File playerDataFolder = new File(ClansPlus.plugin.getDataFolder() + "/playerData/");
            if (!playerDataFolder.exists()) {
                playerDataFolder.mkdirs();
            }
            STORAGE = new PluginDataYAMLStorage();
        }

        if (databaseType == DatabaseType.H2) {
            STORAGE = new PluginDataH2Storage(Settings.DATABASE_SETTINGS_H2_FILE_NAME, Settings.DATABASE_SETTINGS_H2_TABLE_CLAN, Settings.DATABASE_SETTINGS_H2_TABLE_PLAYER);
        }
    }

    public static ClanData getClanData(String clanName) {
        return STORAGE.getClanData(clanName);
    }

    public static void saveClanData(String clanName, ClanData clanData) {
        STORAGE.saveClanData(clanName, clanData);
    }

    public static PlayerData getPlayerData(String playerName) {
        return STORAGE.getPlayerData(playerName);
    }

    public static void savePlayerData(String playerName, PlayerData playerData) {
        STORAGE.savePlayerData(playerName, playerData);
    }

    public static List<String> getAllClans() {
        return STORAGE.getAllClans();
    }

    public static List<String> getAllPlayers() {
        return STORAGE.getAllPlayers();
    }

    public static void disableStorage() {
        STORAGE.disableStorage();
    }
}
