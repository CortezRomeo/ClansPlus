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

        // inventories/clan-list-inventory.yml
        String clanListInventoryFileName = "clan-list-inventory.yml";
        ClanListInventoryFile.setup();
        ClanListInventoryFile.saveDefault();
        File clanListInventoryFile = new File(getDataFolder() + "/inventories/clan-list-inventory.yml");
        try {
            ConfigUpdater.update(this, clanListInventoryFileName, clanListInventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClanListInventoryFile.reload();

        // inventories/no-clan-inventory.yml
        String noClanFileName = "no-clan-inventory.yml";
        NoClanInventoryFile.setup();
        NoClanInventoryFile.saveDefault();
        File noClanFile = new File(getDataFolder() + "/inventories/no-clan-inventory.yml");
        try {
            ConfigUpdater.update(this, noClanFileName, noClanFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NoClanInventoryFile.reload();

        // inventories/clan-menu-inventory.yml
        String clanMenuFileName = "clan-menu-inventory.yml";
        ClanMenuInventoryFile.setup();
        ClanMenuInventoryFile.saveDefault();
        File clanMenuFile = new File(getDataFolder() + "/inventories/clan-menu-inventory.yml");
        try {
            ConfigUpdater.update(this, clanMenuFileName, clanMenuFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClanMenuInventoryFile.reload();

        // inventories/members-menu-inventory.yml
        String membersMenuFileName = "members-menu-inventory.yml";
        MembersMenuInventoryFile.setup();
        MembersMenuInventoryFile.saveDefault();
        File membersMenuFile = new File(getDataFolder() + "/inventories/members-menu-inventory.yml");
        try {
            ConfigUpdater.update(this, membersMenuFileName, membersMenuFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MembersMenuInventoryFile.reload();

        // inventories/add-member-list-inventory.yml
        String addMemberListFileName = "add-member-list-inventory.yml";
        AddMemberListInventoryFile.setup();
        AddMemberListInventoryFile.saveDefault();
        File addMemberListFile = new File(getDataFolder() + "/inventories/add-member-list-inventory.yml");
        try {
            ConfigUpdater.update(this, addMemberListFileName, addMemberListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddMemberListInventoryFile.reload();

        // inventories/member-list-inventory.yml
        String MemberListFileName = "member-list-inventory.yml";
        MemberListInventoryFile.setup();
        MemberListInventoryFile.saveDefault();
        File MemberListFile = new File(getDataFolder() + "/inventories/member-list-inventory.yml");
        try {
            ConfigUpdater.update(this, MemberListFileName, MemberListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MemberListInventoryFile.reload();

        // inventories/manage-member-inventory.yml
        String ManageMembersFileName = "manage-member-inventory.yml";
        ManageMemberInventoryFile.setup();
        ManageMemberInventoryFile.saveDefault();
        File ManageMembersFile = new File(getDataFolder() + "/inventories/manage-member-inventory.yml");
        try {
            ConfigUpdater.update(this, ManageMembersFileName, ManageMembersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ManageMemberInventoryFile.reload();

        // inventories/manage-member-rank-inventory.yml
        String ManageMemberRankFileName = "manage-member-rank-inventory.yml";
        ManageMemberRankInventoryFile.setup();
        ManageMemberRankInventoryFile.saveDefault();
        File ManageMemberRankFile = new File(getDataFolder() + "/inventories/manage-member-rank-inventory.yml");
        try {
            ConfigUpdater.update(this, ManageMemberRankFileName, ManageMemberRankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ManageMemberRankInventoryFile.reload();

        // inventories/allies-menu-inventory.yml
        String alliesMenuFileName = "allies-menu-inventory.yml";
        AlliesMenuInventoryFile.setup();
        AlliesMenuInventoryFile.saveDefault();
        File alliesMenuFile = new File(getDataFolder() + "/inventories/allies-menu-inventory.yml");
        try {
            ConfigUpdater.update(this, alliesMenuFileName, alliesMenuFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AlliesMenuInventoryFile.reload();

        // inventories/add-ally-list-inventory.yml
        String addAllyListFileName = "add-ally-list-inventory.yml";
        AddAllyListInventoryFile.setup();
        AddAllyListInventoryFile.saveDefault();
        File addAllyListFile = new File(getDataFolder() + "/inventories/add-ally-list-inventory.yml");
        try {
            ConfigUpdater.update(this, addAllyListFileName, addAllyListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddAllyListInventoryFile.reload();

        // inventories/ally-invitation-list-inventory.yml
        String allyInvitationListFileName = "ally-invitation-list-inventory.yml";
        AllyInvitationInventoryFile.setup();
        AllyInvitationInventoryFile.saveDefault();
        File allyInvitationListFile = new File(getDataFolder() + "/inventories/ally-invitation-list-inventory.yml");
        try {
            ConfigUpdater.update(this, allyInvitationListFileName, allyInvitationListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AllyInvitationInventoryFile.reload();

        // inventories/ally-invitation-confirm-inventory.yml
        String allyInvitationConfirmFileName = "ally-invitation-confirm-inventory.yml";
        AllyInivtationConfirmInventoryFile.setup();
        AllyInivtationConfirmInventoryFile.saveDefault();
        File allyInvitationConfirmFile = new File(getDataFolder() + "/inventories/ally-invitation-confirm-inventory.yml");
        try {
            ConfigUpdater.update(this, allyInvitationConfirmFileName, allyInvitationConfirmFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AllyInivtationConfirmInventoryFile.reload();

        // inventories/ally-list-inventory.yml
        String allyListFileName = "ally-list-inventory.yml";
        AllyListInventoryFile.setup();
        AllyListInventoryFile.saveDefault();
        File allyListFile = new File(getDataFolder() + "/inventories/ally-list-inventory.yml");
        try {
            ConfigUpdater.update(this, allyListFileName, allyListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AllyListInventoryFile.reload();

        // inventories/manage-ally-inventory.yml
        String manageAllyFileName = "manage-ally-inventory.yml";
        ManageAllyInventoryFile.setup();
        ManageAllyInventoryFile.saveDefault();
        File manageAllyFile = new File(getDataFolder() + "/inventories/manage-ally-inventory.yml");
        try {
            ConfigUpdater.update(this, manageAllyFileName, manageAllyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ManageAllyInventoryFile.reload();

        // inventories/view-clan-inventory.yml
        String viewClanFileName = "view-clan-inventory.yml";
        ViewClanInventoryFile.setup();
        ViewClanInventoryFile.saveDefault();
        File viewClanFile = new File(getDataFolder() + "/inventories/view-clan-inventory.yml");
        try {
            ConfigUpdater.update(this, viewClanFileName, viewClanFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ViewClanInventoryFile.reload();

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
