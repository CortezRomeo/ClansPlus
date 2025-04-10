package com.cortezromeo.clansplus;

import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings {

    public static boolean DEBUG_ENABLED;
    public static String DEBUG_PREFIX;
    public static String LANGUAGE;
    public static String DATABASE_TYPE;
    public static boolean DATABASE_SETTING_SMART_LOADING_ENABLED;
    public static boolean DATABASAE_SETTING_FIX_BUG_DATABASE_ENABLED;
    public static boolean DATABASE_AUTO_SAVE_ENABLED;
    public static int DATABASE_AUTO_SAVE_SECONDS;
    public static String DATABASE_BACK_UP_FILE_NAME_FORMAT;
    public static String DATABASE_SETTINGS_H2_FILE_NAME;
    public static String DATABASE_SETTINGS_H2_TABLE_CLAN;
    public static String DATABASE_SETTINGS_H2_TABLE_PLAYER;
    public static int PROGRESS_BAR_TOTAL_BARS;
    public static String PROGRESS_BAR_SYMBOL_COMPLETED;
    public static String PROGRESS_BAR_SYMBOL_NOTCOMPLETED;
    public static boolean CLAN_SETTING_CREATE_ENABLED;
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
    public static int CLAN_SETTING_CUSTOM_NAME_MINIMUM_LENGTH;
    public static int CLAN_SETTING_CUSTOM_NAME_MAXIMUM_LENGTH;
    public static HashMap<Subject, Rank> CLAN_SETTING_PERMISSION_DEFAULT = new HashMap<>();
    public static boolean CLAN_SETTING_PERMISSION_DEFAULT_FORCED;
    public static boolean CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_ENABLED;
    public static List<String> CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_WORLDS;
    public static boolean CLAN_SETTING_SPAWN_COUNTDOWN_ENABLED;
    public static int CLAN_SETTING_SPAWN_COUNTDOWN_SECONDS;

    public static String SOFT_DEPEND_PLACEHOLDERAPI_NO_CLAN;
    public static String SOFT_DEPEND_PLACEHOLDERAPI_TOP_SCORE_NAME;
    public static String SOFT_DEPEND_PLACEHOLDERAPI_TOP_SCORE_VALUE;
    public static String SOFT_DEPEND_DISCORDWEBHOOK_URL;

    public static void setupValue() {
        MessageUtil.debug("SETTINGS", "Loading settings from config yaml...");
        FileConfiguration configuration = ClansPlus.plugin.getConfig();

        DEBUG_ENABLED = configuration.getBoolean("debug.enabled");
        DEBUG_PREFIX = configuration.getString("debug.prefix");
        LANGUAGE = configuration.getString("language");
        DATABASE_TYPE = configuration.getString("database.type");
        DATABASE_SETTING_SMART_LOADING_ENABLED = configuration.getBoolean("database.smart-loading.enabled");
        DATABASAE_SETTING_FIX_BUG_DATABASE_ENABLED = configuration.getBoolean("database.fix-bug-database.enabled");
        DATABASE_AUTO_SAVE_ENABLED = configuration.getBoolean("database.auto-save.enabled");
        DATABASE_AUTO_SAVE_SECONDS = configuration.getInt("database.auto-save.seconds");
        DATABASE_BACK_UP_FILE_NAME_FORMAT = configuration.getString("database.backup-settings.file-name-date-format");
        DATABASE_SETTINGS_H2_FILE_NAME = configuration.getString("database.settings.h2.file-name");
        DATABASE_SETTINGS_H2_TABLE_CLAN = configuration.getString("database.settings.h2.table.clan");
        DATABASE_SETTINGS_H2_TABLE_PLAYER = configuration.getString("database.settings.h2.table.player");
        PROGRESS_BAR_TOTAL_BARS = configuration.getInt("progress-bar.total-bars");
        PROGRESS_BAR_SYMBOL_COMPLETED = configuration.getString("progress-bar.symbol.completed");
        PROGRESS_BAR_SYMBOL_NOTCOMPLETED = configuration.getString("progress-bar.symbol.not-completed");
        CLAN_SETTING_CREATE_ENABLED = configuration.getBoolean("clan-settings.creating-clan-settings.currency-requirement.enabled");
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
        CLAN_SETTING_NAME_MINIMUM_LENGTH = configuration.getInt("clan-settings.clan-name-settings.minimum-length.clan-name");
        CLAN_SETTING_NAME_MAXIMUM_LENGTH = configuration.getInt("clan-settings.clan-name-settings.maximum-length.clan-name");
        CLAN_SETTING_CUSTOM_NAME_MINIMUM_LENGTH = configuration.getInt("clan-settings.clan-name-settings.minimum-length.clan-custom-name");
        CLAN_SETTING_CUSTOM_NAME_MAXIMUM_LENGTH = configuration.getInt("clan-settings.clan-name-settings.maximum-length.clan-custom-name");
        if (!CLAN_SETTING_PERMISSION_DEFAULT.isEmpty())
            CLAN_SETTING_PERMISSION_DEFAULT.clear();
        for (Subject subject : Subject.values())
            CLAN_SETTING_PERMISSION_DEFAULT.put(subject, Rank.valueOf(configuration.getString("clan-settings.creating-clan-settings.permission-default." + subject)));
        CLAN_SETTING_PERMISSION_DEFAULT_FORCED = configuration.getBoolean("clan-settings.permission-default-forced");
        CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_ENABLED = configuration.getBoolean("clan-settings.set-spawn-settings.blacklist-worlds.enabled");
        CLAN_SETTING_SET_SPAWN_BLACKLIST_WORLDS_WORLDS = configuration.getStringList("clan-settings.set-spawn-settings.blacklist-worlds.worlds");
        CLAN_SETTING_SPAWN_COUNTDOWN_ENABLED = configuration.getBoolean("clan-settings.spawn-countdown.enabled");
        CLAN_SETTING_SPAWN_COUNTDOWN_SECONDS = configuration.getInt("clan-settings.spawn-countdown.seconds");
        SOFT_DEPEND_PLACEHOLDERAPI_NO_CLAN = configuration.getString("soft-depends.placeholderapi.no-clan");
        SOFT_DEPEND_PLACEHOLDERAPI_TOP_SCORE_NAME = configuration.getString("soft-depends.placeholderapi.top-score-name");
        SOFT_DEPEND_PLACEHOLDERAPI_TOP_SCORE_VALUE = configuration.getString("soft-depends.placeholderapi.top-score-value");
        SOFT_DEPEND_DISCORDWEBHOOK_URL = configuration.getString("soft-depends.discordWebhook.webhookURL");
    }
}
