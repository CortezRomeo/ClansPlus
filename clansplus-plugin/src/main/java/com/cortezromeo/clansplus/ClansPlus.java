package com.cortezromeo.clansplus;

import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.server.VersionSupport;
import com.cortezromeo.clansplus.command.ClanCommand;
import com.cortezromeo.clansplus.command.PluginTestCommand;
import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.file.inventory.ClanListInventoryFile;
import com.cortezromeo.clansplus.listener.InventoryClickListener;
import com.cortezromeo.clansplus.listener.PlayerJoinListener;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.storage.PluginDataStorage;
import com.cortezromeo.clansplus.support.version.CrossVersionSupport;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ClansPlus extends JavaPlugin {

    public static ClansPlus plugin;
    public static VersionSupport nms;
    public static DatabaseType databaseType;
    private static com.cortezromeo.clansplus.api.ClanPlus api;
    private static boolean papiSupport = false;

    @Override
    public void onLoad() {
        plugin = this;
        nms = new CrossVersionSupport(plugin);
        api = new API();
        Bukkit.getServicesManager().register(com.cortezromeo.clansplus.api.ClanPlus.class, api, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        initFiles();
        Settings.setupValue();
        initDatabase();
        PluginDataManager.loadAllDatabase();
        initCommands();
        initListener();

        // Check license key when the plugin is activated from dihoastore.net
/*        if (!DiHoaStore.doDiHoa()) {
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }*/

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            try {
                PluginDataManager.loadPlayerDatabase(player.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initFiles() {
        File inventoryFolder = new File(getDataFolder() + "/inventories");
        if (!inventoryFolder.exists())
            inventoryFolder.mkdirs();

        File languageFolder = new File(getDataFolder() + "/languages");
        if (!languageFolder.exists())
            languageFolder.mkdirs();

        // config.yml
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile,  "clan-settings.creating-clan-settings.skill-level-default", "database");
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
        MessageUtil.debug("LOADING FILE", "Loaded config.yml.");

        // inventories/clanlistinventory.yml
        String clanListInventoryFileName = "clanlistinventory.yml";
        ClanListInventoryFile.setup();
        ClanListInventoryFile.saveDefault();
        File clanListInventoryFile = new File(getDataFolder() + "/inventories/clanlistinventory.yml");
        try {
            ConfigUpdater.update(this, clanListInventoryFileName, clanListInventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClanListInventoryFile.reload();

        // events.yml
        String eventFileName = "events.yml";
        File eventsFile = new File(getDataFolder() + "/events.yml");
        if (!eventsFile.exists()) {
            try {
                EventsFile.setup();
                EventsFile.saveDefault();
                ConfigUpdater.update(this, eventFileName, eventsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                EventsFile.setup();
                EventsFile.saveDefault();
                ConfigUpdater.update(this, eventFileName, eventsFile,
                        "events.clan-war-event.score-settings");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        EventsFile.reload();
        MessageUtil.debug("LOADING FILE", "Loaded events.yml.");

    }

    public void initCommands() {
        new ClanCommand();
        new PluginTestCommand();
    }

    public void initListener() {
        new PlayerJoinListener();
        new InventoryClickListener();
    }

    public static com.cortezromeo.clansplus.api.ClanPlus getAPI() {
        return api;
    }

    public void initDatabase() {
        try {
            databaseType = DatabaseType.valueOf(Settings.DATABASE_TYPE.toUpperCase());
            PluginDataStorage.init(databaseType);
        } catch (IllegalArgumentException exception) {
            MessageUtil.log("&c--------------------------------------");
            MessageUtil.log("    &4ERROR");
            MessageUtil.log("&eDatabase type &c&l" + Settings.DATABASE_TYPE + "&e does not exist!");
            MessageUtil.log("&ePlease check it again in config.yml.");
            MessageUtil.log("&eDatabase will automatically use &b&lYAML &eto load.");
            MessageUtil.log("&c--------------------------------------");
            PluginDataStorage.init(DatabaseType.YAML);
            databaseType = DatabaseType.YAML;
            Settings.DATABASE_TYPE = "YAML";
        }
    }

    public static boolean isPapiSupport() {
        return papiSupport;
    }

    @Override
    public void onDisable() {
        PluginDataManager.saveAllDatabase();
        PluginDataStorage.disableStorage();
    }
}
