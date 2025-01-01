package com.cortezromeo.clansplus.language;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Vietnamese {
    private static File file;
    private static FileConfiguration messageFile;

    public static void setup() {
        file = new File(ClansPlus.plugin.getDataFolder() + "/languages/language_vi.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messageFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return messageFile;
    }

    public static void saveDefault() {
        try {
            if (!file.exists()) {
                ClansPlus.plugin.saveResource("language_vi.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        messageFile = YamlConfiguration.loadConfiguration(file);
    }
}
