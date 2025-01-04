package com.cortezromeo.clansplus;

import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.server.VersionSupport;
import com.cortezromeo.clansplus.command.ClanCommand;
import com.cortezromeo.clansplus.command.PluginTestCommand;
import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.file.inventory.*;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.language.Vietnamese;
import com.cortezromeo.clansplus.listener.InventoryClickListener;
import com.cortezromeo.clansplus.listener.PlayerChatListener;
import com.cortezromeo.clansplus.listener.PlayerJoinListener;
import com.cortezromeo.clansplus.listener.PlayerQuitListener;
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
        initLanguages();
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
            ConfigUpdater.update(this, "config.yml", configFile,  "clan-settings.creating-clan-settings.skill-level-default");
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

        // inventories/noclaninventory.yml
        String noClanFileName = "noclaninventory.yml";
        NoClanInventoryFile.setup();
        NoClanInventoryFile.saveDefault();
        File noClanFile = new File(getDataFolder() + "/inventories/noclaninventory.yml");
        try {
            ConfigUpdater.update(this, noClanFileName, noClanFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NoClanInventoryFile.reload();

        // inventories/clanmenuinventory.yml
        String clanMenuFileName = "clanmenuinventory.yml";
        ClanMenuInventoryFile.setup();
        ClanMenuInventoryFile.saveDefault();
        File clanMenuFile = new File(getDataFolder() + "/inventories/clanmenuinventory.yml");
        try {
            ConfigUpdater.update(this, clanMenuFileName, clanMenuFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClanMenuInventoryFile.reload();

        // inventories/membersmenuinventory.yml
        String membersMenuFileName = "membersmenuinventory.yml";
        MembersMenuInventoryFile.setup();
        MembersMenuInventoryFile.saveDefault();
        File membersMenuFile = new File(getDataFolder() + "/inventories/membersmenuinventory.yml");
        try {
            ConfigUpdater.update(this, membersMenuFileName, membersMenuFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MembersMenuInventoryFile.reload();

        // inventories/addmemberlistinventory.yml
        String addMemberListFileName = "addmemberlistinventory.yml";
        AddMemberListInventoryFile.setup();
        AddMemberListInventoryFile.saveDefault();
        File addMemberListFile = new File(getDataFolder() + "/inventories/addmemberlistinventory.yml");
        try {
            ConfigUpdater.update(this, addMemberListFileName, addMemberListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddMemberListInventoryFile.reload();

        // inventories/memberlistinventory.yml
        String MemberListFileName = "memberlistinventory.yml";
        MemberListInventoryFile.setup();
        MemberListInventoryFile.saveDefault();
        File MemberListFile = new File(getDataFolder() + "/inventories/memberlistinventory.yml");
        try {
            ConfigUpdater.update(this, MemberListFileName, MemberListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MemberListInventoryFile.reload();

        // inventories/managemembersinventory.yml
        String ManageMembersFileName = "managemembersinventory.yml";
        ManageMembersInventoryFile.setup();
        ManageMembersInventoryFile.saveDefault();
        File ManageMembersFile = new File(getDataFolder() + "/inventories/managemembersinventory.yml");
        try {
            ConfigUpdater.update(this, ManageMembersFileName, ManageMembersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ManageMembersInventoryFile.reload();

        // inventories/managemembersrankinventory.yml
        String ManageMembersRankFileName = "managemembersrankinventory.yml";
        ManageMembersRankInventoryFile.setup();
        ManageMembersRankInventoryFile.saveDefault();
        File ManageMembersRankFile = new File(getDataFolder() + "/inventories/managemembersrankinventory.yml");
        try {
            ConfigUpdater.update(this, ManageMembersRankFileName, ManageMembersRankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ManageMembersRankInventoryFile.reload();

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

    public void initLanguages() {
        // language_vi.yml
        String vietnameseFileName = "language_vi.yml";
        Vietnamese.setup();
        Vietnamese.saveDefault();
        File vietnameseFile = new File(getDataFolder(), "/languages/language_vi.yml");
        try {
            ConfigUpdater.update(this, vietnameseFileName, vietnameseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Vietnamese.reload();

        Messages.setupValue(Settings.LANGUAGE);
    }

    public void initCommands() {
        new ClanCommand();
        new PluginTestCommand();
    }

    public void initListener() {
        new PlayerJoinListener();
        new InventoryClickListener();
        new PlayerChatListener();
        new PlayerQuitListener();
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
