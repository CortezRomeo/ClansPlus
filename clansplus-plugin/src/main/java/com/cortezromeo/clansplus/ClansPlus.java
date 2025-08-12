package com.cortezromeo.clansplus;

import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.server.VersionSupport;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.clan.skill.plugin.BoostScoreSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.CriticalHitSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.DodgeSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.LifeStealSkill;
import com.cortezromeo.clansplus.command.ClanAdminCommand;
import com.cortezromeo.clansplus.command.ClanCommand;
import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.file.inventory.*;
import com.cortezromeo.clansplus.language.English;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.language.Vietnamese;
import com.cortezromeo.clansplus.listener.*;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.storage.PluginDataStorage;
import com.cortezromeo.clansplus.support.CustomHeadSupport;
import com.cortezromeo.clansplus.support.Support;
import com.cortezromeo.clansplus.support.version.CrossVersionSupport;
import com.cortezromeo.clansplus.task.EventTask;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import static com.cortezromeo.clansplus.util.MessageUtil.log;

public class ClansPlus extends JavaPlugin {

    public static ClansPlus plugin;
    private static com.cortezromeo.clansplus.api.ClanPlus api;
    public static VersionSupport nms;
    public static DatabaseType databaseType;
    public static Support support;
    private EventTask eventTask;

    public static com.cortezromeo.clansplus.api.ClanPlus getAPI() {
        return api;
    }

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
        initSkills();
        support = new Support();
        support.setupSupports();
        initListener();
        EventManager.getWarEvent();
        PluginDataManager.loadAllCustomHeadsFromJsonFiles();
        eventTask = new EventTask();

        log("&f--------------------------------");
        log("&2   ____ _                   ____  _           ");
        log("&2  / ___| | __ _ _ __  ___  |  _ \\| |_   _ ___ ");
        log("&2 | |   | |/ _` | '_ \\/ __| | |_) | | | | / __|");
        log("&2 | |___| | (_| | | | \\__ \\ |  __/| | |_| \\__ \\");
        log("&2  \\____|_|\\__,_|_| |_|___/ |_|   |_|\\__,_|___/");
        log("");
        log("&fVersion: &b" + getDescription().getVersion());
        log("&fAuthor: &bCortez_Romeo");
        log("&eRunning version: " + Bukkit.getServer().getClass().getName().split("\\.")[3]);
        if (support.isFoliaLibSupported()) log("      &2&lFOLIA SUPPORTED");
        log("");
        log("&fSupport:");
        log((support.isVaultSupported() ? "&2[SUPPORTED] &aVault" : "&4[UNSUPPORTED] &cVault"));
        log((support.isPlaceholderAPISupported() ? "&2[SUPPORTED] &aPlaceholderAPI" : "&4[UNSUPPORTED] &cPlaceholderAPI"));
        log((support.isPlayerPointsSupported() ? "&2[SUPPORTED] &aPlayerPoints" : "&4[UNSUPPORTED] &cPlayerPoints"));
        log((support.isMythicMobsSupported() ? "&2[SUPPORTED] &aMythicMobs" : "&4[UNSUPPORTED] &cMythicMobs"));
        log("");
        log("&f--------------------------------");

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            try {
                PluginDataManager.loadPlayerDatabase(player.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new Metrics(this, 25078);
    }

    public void initFiles() {
        // create directories
        File inventoryFolder = new File(getDataFolder() + "/inventories");
        if (!inventoryFolder.exists()) inventoryFolder.mkdirs();

        File languageFolder = new File(getDataFolder() + "/languages");
        if (!languageFolder.exists()) languageFolder.mkdirs();

        File backupFolder = new File(getDataFolder() + "/backup");
        if (!backupFolder.exists()) backupFolder.mkdirs();

        // config.yml
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, "clan-settings.creating-clan-settings.skill-level-default");
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
        MessageUtil.debug("LOADING FILE", "Loaded config.yml.");

        // inventories/clan-list-inventory.yml
        ClanListInventoryFile.setupFile();

        // inventories/no-clan-inventory.yml
        NoClanInventoryFile.setupFile();

        // inventories/clan-menu-inventory.yml
        ClanMenuInventoryFile.setupFile();

        // inventories/members-menu-inventory.yml
        MembersMenuInventoryFile.setupFile();

        // inventories/add-member-list-inventory.yml
        AddMemberListInventoryFile.setupFile();

        // inventories/member-list-inventory.yml
        MemberListInventoryFile.setupFile();

        // inventories/manage-member-inventory.yml
        ManageMemberInventoryFile.setupFile();

        // inventories/manage-member-rank-inventory.yml
        ManageMemberRankInventoryFile.setupFile();

        // inventories/allies-menu-inventory.yml
        AlliesMenuInventoryFile.setupFile();

        // inventories/add-ally-list-inventory.yml
        AddAllyListInventoryFile.setupFile();

        // inventories/ally-invitation-list-inventory.yml
        AllyInvitationInventoryFile.setupFile();

        // inventories/ally-invitation-confirm-inventory.yml
        AllyInivtationConfirmInventoryFile.setupFile();

        // inventories/ally-list-inventory.yml
        AllyListInventoryFile.setupFile();

        // inventories/manage-ally-inventory.yml
        ManageAllyInventoryFile.setupFile();

        // inventories/view-clan-inventory.yml
        ViewClanInventoryFile.setupFile();

        // inventories/upgrade-skill-list-inventory.yml
        UpgradePluginSkillListInventoryFile.setupFile();

        // inventories/upgrade-menu-inventory.yml
        UpgradeMenuInventoryFile.setupFile();

        // inventories/skills-menu-inventory.yml
        SkillsMenuInventoryFile.setupFile();

        // inventories/events-menu-inventory.yml
        EventsMenuInventoryFile.setupFile();

        // inventories/clan-settings-inventory.yml
        ClanSettingsInventoryFile.setupFile();

        // inventories/set-icon-custom-head-list-inventory.yml
        SetIconCustomHeadListInventoryFile.setupFile();

        // inventories/set-icon-material-list-inventory.yml
        SetIconMaterialListInventoryFile.setupFile();

        // inventories/set-icon-menu-inventory.yml
        SetIconMenuInventoryFile.setupFile();

        // inventories/set-permission-inventory.yml
        SetPermissionInventoryFile.setupFile();

        // inventories/disband-confirmation-inventory.yml
        DisbandConfirmationInventoryFile.setupFile();

        // inventories/leave-confirmation-inventory.yml
        LeaveConfirmationInventoryFile.setupFile();

        // inventories/storage-list-inventory.yml
        StorageListInventoryFile.setupFile();

        // inventories/clan-storage-inventory.yml
        ClanStorageInventoryFile.setupFile();

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
                ConfigUpdater.update(this, eventFileName, eventsFile, "events.clan-war-event.score-settings");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        EventsFile.reload();
        MessageUtil.debug("LOADING FILE", "Loaded events.yml.");

        // skills.yml
        String skillsFileName = "skills.yml";
        File skillsFile = new File(getDataFolder() + "/skills.yml");
        if (!skillsFile.exists()) {
            try {
                SkillsFile.setup();
                SkillsFile.saveDefault();
                ConfigUpdater.update(this, skillsFileName, skillsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                SkillsFile.setup();
                SkillsFile.saveDefault();
                ConfigUpdater.update(this, skillsFileName, skillsFile, "plugin-skills");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SkillsFile.reload();
        MessageUtil.debug("LOADING FILE", "Loaded skills.yml.");

        // upgrade.yml
        String upgradeFileName = "upgrade.yml";
        File upgradeFile = new File(getDataFolder() + "/upgrade.yml");
        if (!upgradeFile.exists()) {
            try {
                UpgradeFile.setup();
                UpgradeFile.saveDefault();
                ConfigUpdater.update(this, upgradeFileName, upgradeFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                UpgradeFile.setup();
                UpgradeFile.saveDefault();
                ConfigUpdater.update(this, upgradeFileName, upgradeFile, "upgrade.plugin-skills");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        UpgradeFile.reload();
        MessageUtil.debug("LOADING FILE", "Loaded upgrade.yml.");

        // discordsrv-warevent-starting.json
        File warEventStartingJsonFile = new File(getDataFolder(), "discordsrv-warevent-starting.json");
        if (!warEventStartingJsonFile.exists()) saveResource(warEventStartingJsonFile.getName(), false);

        // discordsrv-warevent-ending.json
        File warEventEndingJsonFile = new File(getDataFolder(), "discordsrv-warevent-ending.json");
        if (!warEventEndingJsonFile.exists()) saveResource(warEventEndingJsonFile.getName(), false);

        CustomHeadSupport.setupCustomHeadJsonFiles();
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

        // language_en.yml
        String englishFileName = "language_en.yml";
        English.setup();
        English.saveDefault();
        File englishFile = new File(getDataFolder(), "/languages/language_en.yml");
        try {
            ConfigUpdater.update(this, englishFileName, englishFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        English.reload();

        Messages.setupValue(Settings.LANGUAGE);
    }

    public void initCommands() {
        new ClanCommand();
        new ClanAdminCommand();
    }

    public void initListener() {
        new PlayerJoinListener();
        new InventoryClickListener();
        new EntityDamageListener();
        if (support.getFoliaLib().isPaper() || support.getFoliaLib().isFolia()) {
            new PaperAsyncChatListener();
            log("&e[PAPER OPTIMIZATION] USING PAPER ASYNC CHAT.");
        } else new AsyncPlayerChatListener();
        new SignChangeListener();
        new PlayerQuitListener();
        new PlayerMovementListener();
        new PlayerDeathListener();
        new EntityDeathListener();
    }

    public void initSkills() {
        CriticalHitSkill.registerSkill();
        DodgeSkill.registerSkill();
        LifeStealSkill.registerSkill();
        BoostScoreSkill.registerSkill();
    }

    public void initDatabase() {
        try {
            databaseType = DatabaseType.valueOf(Settings.DATABASE_TYPE.toUpperCase());
            PluginDataStorage.init(databaseType);
        } catch (IllegalArgumentException exception) {
            log("&c--------------------------------------");
            log("    &4ERROR");
            log("&eDatabase type &c&l" + Settings.DATABASE_TYPE + "&e does not exist!");
            log("&ePlease check it again in config.yml.");
            log("&eDatabase will automatically use &b&lYAML &eto load.");
            log("&c--------------------------------------");
            PluginDataStorage.init(DatabaseType.YAML);
            databaseType = DatabaseType.YAML;
            Settings.DATABASE_TYPE = "YAML";
        }
    }

    public EventTask getEventTask() {
        return eventTask;
    }

    @Override
    public void onDisable() {
        PluginDataManager.saveAllDatabase();
        PluginDataStorage.disableStorage();

        try {
            if (!Bukkit.getOnlinePlayers().isEmpty()) for (Player player : Bukkit.getOnlinePlayers()) {
                player.closeInventory();
            }
        } catch (IncompatibleClassChangeError exception) {
            // ignore it
        }

        log("&f--------------------------------");
        log("&c   ____ _                   ____  _           ");
        log("&c  / ___| | __ _ _ __  ___  |  _ \\| |_   _ ___ ");
        log("&c | |   | |/ _` | '_ \\/ __| | |_) | | | | / __|");
        log("&c | |___| | (_| | | | \\__ \\ |  __/| | |_| \\__ \\");
        log("&c  \\____|_|\\__,_|_| |_|___/ |_|   |_|\\__,_|___/");
        log("");
        log("&fVersion: &b" + getDescription().getVersion());
        log("&fAuthor: &bCortez_Romeo");
        log("&f--------------------------------");
    }
}
