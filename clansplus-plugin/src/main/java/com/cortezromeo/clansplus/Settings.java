package com.cortezromeo.clansplus;

import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings {

    public static boolean DEBUG_ENABLED;
    public static String DEBUG_PREFIX;
    public static String DATABASE_TYPE;
    public static String DATABASE_SETTINGS_H2_FILE_NAME;
    public static String DATABASE_SETTINGS_H2_TABLE_CLAN;
    public static String DATABASE_SETTINGS_H2_TABLE_PLAYER;
    public static boolean AUTO_SAVE_ENABLED;
    public static int AUTO_SAVE_SECONDS;
    public static String CLAN_SETTING_CREATE_TYPE;
    public static long CLAN_SETTING_CREATE_CURRENCY;
    public static int CLAN_SETTING_CREATE_VALUE;
    public static int CLAN_SETTING_TIME_TO_ACCEPT;
    public static int CLAN_SETTING_MAXIMUM_MEMBER_DEFAULT;
    public static String CLAN_SETTING_ICON_DEFAULT_TYPE;
    public static String CLAN_SETTING_ICON_DEFAULT_VALUE;
    public static HashMap<Integer, Integer> CLAN_SETTING_SKILL_DEFAULT = new HashMap<>();
    public static List<String> CLAN_SETTING_PROHIBITED_NAME = new ArrayList<>();
    public static List<String> CLAN_SETTING_PROHIBITED_CHARACTER = new ArrayList<>();
    public static int CLAN_SETTING_NAME_MINIMUM_LENGTH;
    public static int CLAN_SETTING_NAME_MAXIMUM_LENGTH;

    public static boolean EVENT_CLAN_WAR_SETTING_JOIN_NOTIFICATION;
    public static int EVENT_CLAN_WAR_SETTING_MINIMUM_PLAYER_ONLINE;
    public static boolean EVENT_CLAN_WAR_SETTING_WORLDS_ENABLED;
    public static List<String> EVENT_CLAN_WAR_SETTING_WORLDS = new ArrayList<>();
    public static int EVENT_CLAN_WAR_SETTING_EVENT_TIME;
    public static boolean EVENT_CLAN_WAR_SETTING_COMBAT_COMMAND_COOLDOWN_ENABLED;
    public static int EVENT_CLAN_WAR_SETTING_COMBAT_COMMAND_COOLDOWN_SECONDS;
    public static List<String> EVENT_CLAN_WAR_SETTING_TIME_FRAME = new ArrayList<>();
    public static boolean EVENT_CLAN_WAR_SETTING_BOSS_BAR_ENABLED;
    public static String EVENT_CLAN_WAR_SETTING_BOSS_BAR_TITLE;
    public static String EVENT_CLAN_WAR_SETTING_BOSS_BAR_COLOR;
    public static String EVENT_CLAN_WAR_SETTING_BOSS_BAR_STYLE;
    public static String EVENT_CLAN_WAR_SETTING_STARTING_SOUND_NAME;
    public static int EVENT_CLAN_WAR_SETTING_STARTING_SOUND_VOLUME;
    public static int EVENT_CLAN_WAR_SETTING_STARTING_SOUND_PITCH;
    public static String EVENT_CLAN_WAR_SETTING_ENDING_SOUND_NAME;
    public static int EVENT_CLAN_WAR_SETTING_ENDING_SOUND_VOLUME;
    public static int EVENT_CLAN_WAR_SETTING_ENDING_SOUND_PITCH;
    public static HashMap<String, Integer> EVENT_CLAN_WAR_SETTING_POINTS_VANILLA_MOBS = new HashMap<>();
    public static HashMap<String, Integer> EVENT_CLAN_WAR_SETTING_POINTS_MYTHIC_MOBS = new HashMap<>();
    public static int EVENT_CLAN_WAR_SETTING_POINTS_PLAYER;
    public static List<String> CLAN_WAR_STARTING_COMMANDS = new ArrayList<>();
    public static List<String> CLAN_WAR_ENDING_COMMANDS = new ArrayList<>();

    public static String DEPEND_PLACEHOLDERAPI_CLANLESS;
    public static String DEPEND_PLACEHOLDERAPI_CLAN_TOP;

    public static void setupValue() {
        MessageUtil.debug("SETTINGS", "Loading settings from config yaml...");
        FileConfiguration configuration = ClansPlus.plugin.getConfig();

        DEBUG_ENABLED = configuration.getBoolean("debug.enabled");
        DEBUG_PREFIX = configuration.getString("debug.prefix");
        DATABASE_TYPE = configuration.getString("database.type");
        DATABASE_SETTINGS_H2_FILE_NAME = configuration.getString("database.settings.h2.file-name");
        DATABASE_SETTINGS_H2_TABLE_CLAN = configuration.getString("database.settings.h2.table.banghoi");
        DATABASE_SETTINGS_H2_TABLE_PLAYER = configuration.getString("database.settings.h2.table.player");
        AUTO_SAVE_ENABLED = configuration.getBoolean("auto-save.enabled");
        AUTO_SAVE_SECONDS = configuration.getInt("auto-save.seconds");
        CLAN_SETTING_CREATE_TYPE = configuration.getString("clan-settings.creating-clan-settings.currency-requirement.type");
        CLAN_SETTING_CREATE_CURRENCY = configuration.getLong("clan-settings.creating-clan-settings.currency-requirement.value");
        CLAN_SETTING_CREATE_VALUE = configuration.getInt("clan-settings.creating-bang-hoi.value");
        CLAN_SETTING_MAXIMUM_MEMBER_DEFAULT = configuration.getInt("clan-settings.creating-clan-settings.maximum-member-default");
        CLAN_SETTING_ICON_DEFAULT_TYPE = configuration.getString("clan-settings.creating-clan-settings.icon-default.type");
        CLAN_SETTING_ICON_DEFAULT_VALUE = configuration.getString("clan-settings.creating-clan-settings.icon-default.value");
        if (!CLAN_SETTING_SKILL_DEFAULT.isEmpty())
            CLAN_SETTING_SKILL_DEFAULT.clear();
        for (String skillIDString : configuration.getConfigurationSection("clan-settings.creating-clan-settings.skill-level-default").getKeys(false)) {
            try {
                int skillID = Integer.parseInt(skillIDString);
                CLAN_SETTING_SKILL_DEFAULT.put(skillID, configuration.getInt("clan-settings.creating-clan-settings.skill-level-default." + skillID));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        CLAN_SETTING_TIME_TO_ACCEPT = configuration.getInt("clan-settings.invite-settings.time-to-accept");
        if (!CLAN_SETTING_PROHIBITED_NAME.isEmpty())
            CLAN_SETTING_PROHIBITED_NAME.clear();
        CLAN_SETTING_PROHIBITED_NAME.addAll(configuration.getStringList("clan-settings.clan-name-settings.prohibited-name"));
        if (!CLAN_SETTING_PROHIBITED_CHARACTER.isEmpty())
            CLAN_SETTING_PROHIBITED_CHARACTER.clear();
        CLAN_SETTING_PROHIBITED_CHARACTER.addAll(configuration.getStringList("clan-settings.clan-name-settings.prohibited-character"));
        CLAN_SETTING_NAME_MINIMUM_LENGTH = configuration.getInt("clan-settings.clan-name-settings.minimum-length");
        CLAN_SETTING_NAME_MAXIMUM_LENGTH = configuration.getInt("clan-settings.clan-name-settings.maximum-length");

        String clanWarEventPath = "events.clan-war-event.";
        FileConfiguration eventFileConfiguration = EventsFile.get();
        EVENT_CLAN_WAR_SETTING_JOIN_NOTIFICATION = eventFileConfiguration.getBoolean(clanWarEventPath + "player-join-notification");
        EVENT_CLAN_WAR_SETTING_MINIMUM_PLAYER_ONLINE = eventFileConfiguration.getInt("bang-hoi-war.minimum-player-online");
        if (!EVENT_CLAN_WAR_SETTING_WORLDS.isEmpty())
            EVENT_CLAN_WAR_SETTING_WORLDS.clear();
        EVENT_CLAN_WAR_SETTING_WORLDS_ENABLED = eventFileConfiguration.getBoolean(clanWarEventPath + "world-requirement.enabled");
        EVENT_CLAN_WAR_SETTING_WORLDS.addAll(eventFileConfiguration.getStringList(clanWarEventPath + "world-requirement.worlds"));
        EVENT_CLAN_WAR_SETTING_EVENT_TIME = eventFileConfiguration.getInt(clanWarEventPath + "event-time");
        EVENT_CLAN_WAR_SETTING_COMBAT_COMMAND_COOLDOWN_ENABLED = eventFileConfiguration.getBoolean(clanWarEventPath + "combat-command-cooldown.enabled");
        EVENT_CLAN_WAR_SETTING_COMBAT_COMMAND_COOLDOWN_SECONDS = eventFileConfiguration.getInt(clanWarEventPath + "combat-command-cooldown.seconds");
        if (!EVENT_CLAN_WAR_SETTING_TIME_FRAME.isEmpty())
            EVENT_CLAN_WAR_SETTING_TIME_FRAME.clear();
        EVENT_CLAN_WAR_SETTING_TIME_FRAME.addAll(eventFileConfiguration.getStringList(clanWarEventPath + "time-frame"));
        EVENT_CLAN_WAR_SETTING_BOSS_BAR_ENABLED = eventFileConfiguration.getBoolean(clanWarEventPath + "event-boss-bar-settings.enabled");
        EVENT_CLAN_WAR_SETTING_BOSS_BAR_TITLE = eventFileConfiguration.getString(clanWarEventPath + "event-boss-bar-settings.title");
        EVENT_CLAN_WAR_SETTING_BOSS_BAR_COLOR = eventFileConfiguration.getString(clanWarEventPath + "event-boss-bar-settings.color");
        EVENT_CLAN_WAR_SETTING_BOSS_BAR_STYLE = eventFileConfiguration.getString(clanWarEventPath + "event-boss-bar-settings.style");
        EVENT_CLAN_WAR_SETTING_STARTING_SOUND_NAME = eventFileConfiguration.getString(clanWarEventPath + "sound-settings.starting-sound.name");
        EVENT_CLAN_WAR_SETTING_STARTING_SOUND_VOLUME = eventFileConfiguration.getInt(clanWarEventPath + "sound-settings.starting-sound.volume");
        EVENT_CLAN_WAR_SETTING_STARTING_SOUND_PITCH = eventFileConfiguration.getInt(clanWarEventPath + "sound-settings.starting-sound.pitch");
        EVENT_CLAN_WAR_SETTING_ENDING_SOUND_NAME = eventFileConfiguration.getString(clanWarEventPath + "sound-settings.ending-sound.name");
        EVENT_CLAN_WAR_SETTING_ENDING_SOUND_VOLUME = eventFileConfiguration.getInt(clanWarEventPath + "sound-settings.ending-sound.volume");
        EVENT_CLAN_WAR_SETTING_ENDING_SOUND_PITCH = eventFileConfiguration.getInt(clanWarEventPath + "sound-settings.ending-sound.pitch");
        for (String mob : eventFileConfiguration.getConfigurationSection(clanWarEventPath + "score-settings.vanilla-mobs").getKeys(false)) {
            if (!EVENT_CLAN_WAR_SETTING_POINTS_VANILLA_MOBS.isEmpty())
                EVENT_CLAN_WAR_SETTING_POINTS_VANILLA_MOBS.clear();
            EVENT_CLAN_WAR_SETTING_POINTS_VANILLA_MOBS.put(mob, eventFileConfiguration.getInt(clanWarEventPath + "score-settings.vanilla-mobs." + mob));
        }
        for (String mythicmob : eventFileConfiguration.getConfigurationSection(clanWarEventPath + "score-settings.mythicmobs-mobs").getKeys(false)) {
            if (!EVENT_CLAN_WAR_SETTING_POINTS_MYTHIC_MOBS.isEmpty())
                EVENT_CLAN_WAR_SETTING_POINTS_MYTHIC_MOBS.clear();
            EVENT_CLAN_WAR_SETTING_POINTS_MYTHIC_MOBS.put(mythicmob, eventFileConfiguration.getInt(clanWarEventPath + "score-settings.mythicmobs-mobs." + mythicmob));
        }
        EVENT_CLAN_WAR_SETTING_POINTS_PLAYER = eventFileConfiguration.getInt(clanWarEventPath + "score-settings.player");
        if (!CLAN_WAR_STARTING_COMMANDS.isEmpty())
            CLAN_WAR_STARTING_COMMANDS.clear();
        CLAN_WAR_STARTING_COMMANDS.addAll(eventFileConfiguration.getStringList(clanWarEventPath + "commands.starting-commands"));
        if (!CLAN_WAR_ENDING_COMMANDS.isEmpty())
            CLAN_WAR_ENDING_COMMANDS.clear();
        CLAN_WAR_ENDING_COMMANDS.addAll(eventFileConfiguration.getStringList(clanWarEventPath + "commands.ending-commands"));
        DEPEND_PLACEHOLDERAPI_CLANLESS = eventFileConfiguration.getString("soft-depends.placeholderapi.clanless");
        DEPEND_PLACEHOLDERAPI_CLAN_TOP = eventFileConfiguration.getString("soft-depends.placeholderapi.bang-hoi-top");
    }
}
