package com.cortezromeo.clansplus.file.inventory;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LeaveConfirmationInventoryFile {
    private static File file;
    private static FileConfiguration fileConfiguration;

    public static void setup() {
        file = new File(ClansPlus.plugin.getDataFolder() + "/inventories/leave-confirmation-inventory.yml");

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
                ClansPlus.plugin.saveResource("leave-confirmation-inventory.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
}
