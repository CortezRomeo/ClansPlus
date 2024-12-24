package com.cortezromeo.clansplus;

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
    public static String DATABASE_SETTINGS_H2_TABLE_BANGHOI;
    public static String DATABASE_SETTINGS_H2_TABLE_PLAYER;
    public static boolean AUTO_SAVE_ENABLED;
    public static int AUTO_SAVE_SECONDS;
    public static String BANG_HOI_OPTION_CREATE_CURRENCY;
    public static int BANG_HOI_OPTION_CREATE_VALUE;
    public static int BANG_HOI_OPTION_TIME_TO_ACCEPT;
    public static int BANG_HOI_OPTION_MAXIMUM_MEMBER_DEFAULT;
    public static List<String> BANG_HOI_OPTION_PROHIBITED_NAME = new ArrayList<>();
    public static List<String> BANG_HOI_OPTION_PROHIBITED_CHARACTER = new ArrayList<>();
    public static int BANG_HOI_OPTION_MINIMUM_LENGTH;
    public static int BANG_HOI_OPTION_MAXIMUM_LENGTH;
    public static String BANG_HOI_RANK_FORMAT_LEADER;
    public static String BANG_HOI_RANK_FORMAT_MANAGER;
    public static String BANG_HOI_RANK_FORMAT_MEMBER;
    public static boolean BANG_HOI_WAR_OPTION_JOIN_NOTIFICATION;
    public static String BANG_HOI_WAR_OPTION_WORLD;
    public static List<String> BANG_HOI_WAR_OPTION_WORLDS = new ArrayList<>();
    public static int BANG_HOI_WAR_OPTION_EVENT_TIME;
    public static int BANG_HOI_WAR_OPTION_COMMAND_COOLDOWN;
    public static List<String> BANG_HOI_WAR_OPTION_TIME_FRAME = new ArrayList<>();
    public static int BANG_HOI_WAR_OPTION_MINIMUM_PLAYER_ONLINE;
    public static String BANG_HOI_WAR_OPTION_BOSS_BAR_TITLE;
    public static String BANG_HOI_WAR_OPTION_BOSS_BAR_COLOR;
    public static String BANG_HOI_WAR_OPTION_BOSS_BAR_STYLE;
    public static String BANG_HOI_WAR_OPTION_SOUND_NAME;
    public static int BANG_HOI_WAR_OPTION_SOUND_VOLUME;
    public static HashMap<String, Integer> BANG_HOI_WAR_OPTION_POINTS_MOBS = new HashMap<>();
    public static HashMap<String, Integer> BANG_HOI_WAR_OPTION_POINTS_MYTHICMOBS = new HashMap<>();
    public static int BANG_HOI_WAR_OPTION_POINTS_PLAYER;
    public static List<String> BANG_HOI_WAR_START_COMMANDS = new ArrayList<>();
    public static String DEPEND_PLACEHOLDERAPI_HOMELESS;
    public static String DEPEND_PLACEHOLDERAPI_BANG_HOI_TOP;

    public static void setupValue() {
        MessageUtil.debug("SETTINGS", "Loading settings from config yaml...");
        FileConfiguration configuration = ClansPlus.plugin.getConfig();

        DEBUG_ENABLED = configuration.getBoolean("debug.enabled");
        DEBUG_PREFIX = configuration.getString("debug.prefix");
        DATABASE_TYPE = configuration.getString("database.type");
        DATABASE_SETTINGS_H2_FILE_NAME = configuration.getString("database.settings.h2.file-name");
        DATABASE_SETTINGS_H2_TABLE_BANGHOI = configuration.getString("database.settings.h2.table.banghoi");
        DATABASE_SETTINGS_H2_TABLE_PLAYER = configuration.getString("database.settings.h2.table.player");
        AUTO_SAVE_ENABLED = configuration.getBoolean("auto-save.enabled");
        AUTO_SAVE_SECONDS = configuration.getInt("auto-save.seconds");
        BANG_HOI_OPTION_CREATE_CURRENCY = configuration.getString("bang-hoi-options.create-bang-hoi.currency");
        BANG_HOI_OPTION_CREATE_VALUE = configuration.getInt("bang-hoi-options.create-bang-hoi.value");
        BANG_HOI_OPTION_TIME_TO_ACCEPT = configuration.getInt("bang-hoi-options.time-to-accept");
        BANG_HOI_OPTION_MAXIMUM_MEMBER_DEFAULT = configuration.getInt("bang-hoi-options.maximum-member-default");
        if (!BANG_HOI_OPTION_PROHIBITED_NAME.isEmpty())
            BANG_HOI_OPTION_PROHIBITED_NAME.clear();
        BANG_HOI_OPTION_PROHIBITED_NAME.addAll(configuration.getStringList("bang-hoi-options.prohibited-name"));
        if (!BANG_HOI_OPTION_PROHIBITED_CHARACTER.isEmpty())
            BANG_HOI_OPTION_PROHIBITED_CHARACTER.clear();
        BANG_HOI_OPTION_PROHIBITED_CHARACTER.addAll(configuration.getStringList("bang-hoi-options.prohibited-character"));
        BANG_HOI_OPTION_MINIMUM_LENGTH = configuration.getInt("bang-hoi-options.minimum-length");
        BANG_HOI_OPTION_MAXIMUM_LENGTH = configuration.getInt("bang-hoi-options.maximum-length");
        BANG_HOI_RANK_FORMAT_LEADER = configuration.getString("bang-hoi-options.rank-name-format.LEADER");
        BANG_HOI_RANK_FORMAT_MANAGER = configuration.getString("bang-hoi-options.rank-name-format.MANAGER");
        BANG_HOI_RANK_FORMAT_MEMBER = configuration.getString("bang-hoi-options.rank-name-format.MEMBER");
        BANG_HOI_WAR_OPTION_JOIN_NOTIFICATION = configuration.getBoolean("bang-hoi-war.join-notification");
        BANG_HOI_WAR_OPTION_WORLD = configuration.getString("bang-hoi-war.world");
        if (!BANG_HOI_WAR_OPTION_WORLDS.isEmpty())
            BANG_HOI_WAR_OPTION_WORLDS.clear();
        BANG_HOI_WAR_OPTION_WORLDS.addAll(configuration.getStringList("bang-hoi-war-multiple-worlds"));
        BANG_HOI_WAR_OPTION_EVENT_TIME = configuration.getInt("bang-hoi-war.event-time");
        BANG_HOI_WAR_OPTION_COMMAND_COOLDOWN = configuration.getInt("bang-hoi-war.command-cooldown");
        if (!BANG_HOI_WAR_OPTION_TIME_FRAME.isEmpty())
            BANG_HOI_WAR_OPTION_TIME_FRAME.clear();
        BANG_HOI_WAR_OPTION_TIME_FRAME.addAll(configuration.getStringList("bang-hoi-war.time-frame"));
        BANG_HOI_WAR_OPTION_MINIMUM_PLAYER_ONLINE = configuration.getInt("bang-hoi-war.minimum-player-online");
        BANG_HOI_WAR_OPTION_BOSS_BAR_TITLE = configuration.getString("bang-hoi-war.boss-bar.title");
        BANG_HOI_WAR_OPTION_BOSS_BAR_COLOR = configuration.getString("bang-hoi-war.boss-bar.color");
        BANG_HOI_WAR_OPTION_BOSS_BAR_STYLE = configuration.getString("bang-hoi-war.boss-bar.style");
        BANG_HOI_WAR_OPTION_SOUND_NAME = configuration.getString("bang-hoi-war.sound.name");
        BANG_HOI_WAR_OPTION_SOUND_VOLUME = configuration.getInt("bang-hoi-war.sound.volume");
        for (String mob : configuration.getConfigurationSection("bang-hoi-war.points.mobs").getKeys(false)) {
            if (!BANG_HOI_WAR_OPTION_POINTS_MOBS.isEmpty())
                BANG_HOI_WAR_OPTION_POINTS_MOBS.clear();
            BANG_HOI_WAR_OPTION_POINTS_MOBS.put(mob, configuration.getInt("bang-hoi-war.points.mobs." + mob));
        }
        for (String mythicmob : configuration.getConfigurationSection("bang-hoi-war.points.mythic-mobs").getKeys(false)) {
            if (!BANG_HOI_WAR_OPTION_POINTS_MYTHICMOBS.isEmpty())
                BANG_HOI_WAR_OPTION_POINTS_MYTHICMOBS.clear();
            BANG_HOI_WAR_OPTION_POINTS_MYTHICMOBS.put(mythicmob, configuration.getInt("bang-hoi-war.points.mythic-mobs." + mythicmob));
        }
        BANG_HOI_WAR_OPTION_POINTS_PLAYER = configuration.getInt("bang-hoi-war.points.player");
        if (!BANG_HOI_WAR_START_COMMANDS.isEmpty())
            BANG_HOI_WAR_START_COMMANDS.clear();
        BANG_HOI_WAR_START_COMMANDS.addAll(configuration.getStringList("bang-hoi-war.start-commands"));
        DEPEND_PLACEHOLDERAPI_HOMELESS = configuration.getString("soft-depends.placeholderapi.homeless");
        DEPEND_PLACEHOLDERAPI_BANG_HOI_TOP = configuration.getString("soft-depends.placeholderapi.bang-hoi-top");
    }
}
