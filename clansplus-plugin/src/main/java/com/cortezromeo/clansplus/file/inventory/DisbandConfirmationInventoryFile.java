package com.cortezromeo.clansplus.file.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DisbandConfirmationInventoryFile {
    private static File file;
    private static FileConfiguration fileConfiguration;
    private static final String fileName = "disband-confirmation-inventory.yml";

    public static void setupFile() {
        createFileAndDir();
        saveDefault();
        File addMemberListFile = new File(ClansPlus.plugin.getDataFolder() + "/inventories/" + fileName);
        try {
            ConfigUpdater.update(ClansPlus.plugin, fileName, addMemberListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reload();
    }

    private static void createFileAndDir() {
        file = new File(ClansPlus.plugin.getDataFolder() + "/inventories/" + fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return fileConfiguration;
    }

    public static void saveDefault() {
        try {
            if (!file.exists()) {
                ClansPlus.plugin.saveResource(fileName, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
}
