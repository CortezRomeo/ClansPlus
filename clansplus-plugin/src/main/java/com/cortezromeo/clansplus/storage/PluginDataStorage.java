package com.cortezromeo.clansplus.storage;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.enums.DatabaseType;

import java.io.File;

public class PluginDataStorage {

    private static PluginStorage STORAGE;

    public static void init(DatabaseType databaseType) {
        if (databaseType == DatabaseType.YAML) {
            File bangHoiDataFolder = new File(ClansPlus.plugin.getDataFolder() + "/banghoiData/");
            if (!bangHoiDataFolder.exists()) {
                bangHoiDataFolder.mkdirs();
            }

            File playerDataFolder = new File(ClansPlus.plugin.getDataFolder() + "/playerData/");
            if (!playerDataFolder.exists()) {
                playerDataFolder.mkdirs();
            }
            STORAGE = new PluginDataYAMLStorage();
        }

        if (databaseType == DatabaseType.H2) {
            STORAGE = new PluginDataH2Storage();
        }
    }

    public static BangHoiData getBangHoiData(String bangHoiName) {
        return STORAGE.getBangHoiData(bangHoiName);
    }

    public static void saveBangHoiData(String bangHoiName, BangHoiData bangHoiData) {
        STORAGE.saveBangHoiData(bangHoiName, bangHoiData);
    }

    public static PlayerData getPlayerData(String playerName) {
        return STORAGE.getPlayerData(playerName);
    }

    public static void savePlayerData(String playerName, PlayerData playerData) {
        STORAGE.savePlayerData(playerName, playerData);
    }
}
