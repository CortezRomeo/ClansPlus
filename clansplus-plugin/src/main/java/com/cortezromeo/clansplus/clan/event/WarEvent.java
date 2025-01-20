package com.cortezromeo.clansplus.clan.event;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class WarEvent {

    private BukkitTask warEventTask;
    private boolean STARTING = false;
    private int TIMELEFT = 0;

    public boolean ENABLED;
    public boolean PLAYER_JOIN_NOTIFICATION_ENABLED;
    public String MESSAGES_PREFIX;
    public String MESSAGES_EVENT_NOT_STARTING;
    public String MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_EVENTTIMEFRAME;
    public String MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_REQUIREDWORLDS;
    public String MESSAGES_EVENT_STARTING;
    public String MESSAGES_EVENT_STARTING_PLACEHOLDER_REQUIREDWORLDS;
    public String MESSAGES_EVENT_ENDING;
    public String MESSAGES_NOT_ENOUGH_PLAYER;
    public int MINIMUM_PLAYER_ONLINE;
    public boolean WORLD_REQUIREMENT_ENABLED;
    public List<String> WORLD_REQUIREMENT_WORLDS;
    public int EVENT_TIME;
    public boolean COMBAT_COMMAND_COOLDOWN_ENABLED;
    public int COMBAT_COMMANDS_COOLDOWN_SECONDS;
    public List<String> EVENT_TIME_FRAME;
    public boolean BOSS_BAR_ENABLED;
    public String BOSS_BAR_TITLE;
    public BarColor BOSS_BAR_COLOR;
    public BarStyle BOSS_BAR_STYLE;
    public String STARTING_SOUND_NAME;
    public int STARTING_SOUND_PITCH;
    public int STARTING_SOUND_VOLUME;
    public String ENDING_SOUND_NAME;
    public int ENDING_SOUND_PITCH;
    public int ENDING_SOUND_VOLUME;
    public HashMap<String, Integer> SCORE_VANILLA_MOBS = new HashMap<>();
    public HashMap<String, Integer> SCORE_MYTHICMOBS_MOBS = new HashMap<>();
    public int SCORE_PLAYER;
    public List<String> STARTING_COMMANDS;
    public List<String> ENDING_COMMANDS;

    public WarEvent() {
        FileConfiguration eventFileConfig = EventsFile.get();

        String eventPath = "events.clan-war-event.";
        ENABLED = eventFileConfig.getBoolean(eventPath + "enabled");
        PLAYER_JOIN_NOTIFICATION_ENABLED = eventFileConfig.getBoolean(eventPath + "player-join-notification.enabled");
        MESSAGES_PREFIX = eventFileConfig.getString(eventPath + "messages.prefix");
        MESSAGES_EVENT_NOT_STARTING = eventFileConfig.getString(eventPath + "messages.event-not-starting");
        MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_EVENTTIMEFRAME = eventFileConfig.getString(eventPath + "messages.event-not-starting-placeholders.eventTimeFrame");
        MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_REQUIREDWORLDS = eventFileConfig.getString(eventPath + "messages.event-not-starting-placeholders.requiredWorlds");
        MESSAGES_EVENT_STARTING = eventFileConfig.getString(eventPath + "messages.event-starting");
        MESSAGES_EVENT_STARTING_PLACEHOLDER_REQUIREDWORLDS = eventFileConfig.getString(eventPath + "messages.event-starting-placeholders.requiredWorlds");
        MESSAGES_EVENT_ENDING = eventFileConfig.getString(eventPath + "messages.event-ending");
        MESSAGES_NOT_ENOUGH_PLAYER = eventFileConfig.getString(eventPath + "messages.not-enough-player");
        MINIMUM_PLAYER_ONLINE = eventFileConfig.getInt(eventPath + "minimum-player-online");
        WORLD_REQUIREMENT_ENABLED = eventFileConfig.getBoolean(eventPath + "world-requirement.enabled");
        WORLD_REQUIREMENT_WORLDS = eventFileConfig.getStringList(eventPath + "world-requirement.worlds");
        EVENT_TIME = eventFileConfig.getInt(eventPath + "event-time");
        COMBAT_COMMAND_COOLDOWN_ENABLED = eventFileConfig.getBoolean(eventPath + "combat-command-cooldown.enabled");
        COMBAT_COMMANDS_COOLDOWN_SECONDS = eventFileConfig.getInt(eventPath + "combat-command-cooldown.seconds");
        EVENT_TIME_FRAME = eventFileConfig.getStringList(eventPath + "event-time-frame");
        BOSS_BAR_ENABLED = eventFileConfig.getBoolean(eventPath + "event-boss-bar-settings.enabled");
        BOSS_BAR_TITLE = eventFileConfig.getString(eventPath + "event-boss-bar-settings.title");
        BOSS_BAR_COLOR = BarColor.valueOf(eventFileConfig.getString(eventPath + "event-boss-bar-settings.color"));
        BOSS_BAR_STYLE = BarStyle.valueOf(eventFileConfig.getString(eventPath + "event-boss-bar-settings.style"));
        STARTING_SOUND_NAME = eventFileConfig.getString(eventPath + "sound-settings.starting-sound.name");
        STARTING_SOUND_PITCH = eventFileConfig.getInt(eventPath + "sound-settings.starting-sound.pitch");
        STARTING_SOUND_VOLUME = eventFileConfig.getInt(eventPath + "sound-settings.starting-sound.volume");
        ENDING_SOUND_NAME = eventFileConfig.getString(eventPath + "sound-settings.ending-sound.name");
        ENDING_SOUND_PITCH = eventFileConfig.getInt(eventPath + "sound-settings.ending-sound.pitch");
        ENDING_SOUND_VOLUME = eventFileConfig.getInt(eventPath + "sound-settings.ending-sound.volume");
        for (String vanillaMob : eventFileConfig.getConfigurationSection(eventPath + "score-settings.vanilla-mobs").getKeys(false)) {
            vanillaMob = vanillaMob.toUpperCase();
            SCORE_VANILLA_MOBS.put(vanillaMob, eventFileConfig.getInt(eventPath + "score-settings.vanilla-mobs." + vanillaMob));
        }
        for (String mythicMob : eventFileConfig.getConfigurationSection(eventPath + "score-settings.mythicmobs-mobs").getKeys(false))
            SCORE_MYTHICMOBS_MOBS.put(mythicMob, eventFileConfig.getInt(eventPath + "score-settings.mythicmobs-mobs." + mythicMob));
        SCORE_PLAYER = eventFileConfig.getInt("score-settings.player");
        STARTING_COMMANDS = eventFileConfig.getStringList("commands.starting-commands");
        ENDING_COMMANDS = eventFileConfig.getStringList("commands.ending-commands");
    }

    public void runEvent(boolean checkPlayerSize) {
        if (STARTING)
            return;

        if (checkPlayerSize) {
            if (Bukkit.getOnlinePlayers().size() < MINIMUM_PLAYER_ONLINE) {
                for (Player player : Bukkit.getOnlinePlayers())
                    sendMessage(player, MESSAGES_NOT_ENOUGH_PLAYER);
                return;
            }
        }

        TIMELEFT = EVENT_TIME;

        // send event starting messages
        for (Player player : Bukkit.getOnlinePlayers()) {
            StringBuilder eventRequiredWorlds = new StringBuilder();
            for (String requiredWorld : WORLD_REQUIREMENT_WORLDS)
                eventRequiredWorlds.append(MESSAGES_EVENT_STARTING_PLACEHOLDER_REQUIREDWORLDS.replace("%requiredWord%", requiredWorld)).append("\n");
            MessageUtil.sendMessage(player, MESSAGES_EVENT_STARTING
                    .replace("%eventTimeLeft%", StringUtil.getTimeFormat(TIMELEFT))
                    .replace("%requiredWorlds%", eventRequiredWorlds.toString()));
        }

        warEventTask = new BukkitRunnable() {
            @Override
            public void run() {
                STARTING = true;

                if (TIMELEFT > 0)
                    TIMELEFT--;
                // ending event
                if (TIMELEFT <= 0) {
                    TIMELEFT = 0;
                    STARTING = false;
                }
            }
        }.runTaskTimer(ClansPlus.plugin, 0, 20);
    }

    public void onJoin(PlayerJoinEvent event) {
        if (!PLAYER_JOIN_NOTIFICATION_ENABLED)
            return;
        sendEventStatusMessage(event.getPlayer(), false);
    }

    public void onDamage(EntityDamageByEntityEvent event) {
        if (!isStarting())
            return;

        Entity entityDamager = event.getDamager();
        Entity entityVictim = event.getEntity();

        if (entityDamager == null || entityVictim == null)
            return;
        if (entityDamager.getType() != EntityType.PLAYER || entityVictim.getType() != EntityType.PLAYER)
            return;

        Player damager = (Player) entityDamager;

        if (WORLD_REQUIREMENT_ENABLED)
            if (!WORLD_REQUIREMENT_WORLDS.contains(damager.getWorld().getName()))
                return;

        IClanData damagerClanData = PluginDataManager.getClanDatabaseByPlayerName(damager.getName());
        IClanData victimClanData = PluginDataManager.getClanDatabaseByPlayerName(entityVictim.getName());

        if (victimClanData != null) {
            SkillData skillData = SkillManager.getSkillData().get(SkillManager.getSkillID(PluginSkill.DODGE));
            if (skillData != null)
                skillData.onDamage(skillData, event);
            return;
        }

        if (damagerClanData == null)
            return;

        for (int skillID : damagerClanData.getSkillLevel().keySet()) {
            SkillData skillData = SkillManager.getSkillData().get(skillID);
            if (skillData != null)
                if (SkillManager.getSkillID(PluginSkill.DODGE) != skillData.getId())
                    skillData.onDamage(skillData, event);
        }
    }

    public void sendEventStatusMessage(Player player, boolean playingSound) {
        if (!isStarting()) {
            StringBuilder eventTimeFrame = new StringBuilder();
            StringBuilder eventRequiredWorlds = new StringBuilder();
            for (String timeFrame : EVENT_TIME_FRAME) {
                eventTimeFrame.append(MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_EVENTTIMEFRAME.replace("%eventTimeFrame%", timeFrame)).append("\n");
            }
            for (String requiredWorld : WORLD_REQUIREMENT_WORLDS)
                eventRequiredWorlds.append(MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_REQUIREDWORLDS.replace("%requiredWord%", requiredWorld)).append("\n");
            MessageUtil.sendMessage(player, MESSAGES_EVENT_NOT_STARTING
                    .replace("%eventTimeFrame%", eventTimeFrame.toString())
                    .replace("%requiredWorlds%", eventRequiredWorlds.toString())
                    .replace("%closestTimeFrame%", new SimpleDateFormat("HH:mm:ss").format(new Date(getClosestTimeFrameMillis())))
                    .replace("%closestTimeFrameTimeLeft%", String.valueOf(StringUtil.getTimeFormat(getClosestTimeFrameTimeLeft())))
                    .replace("%minimumPlayerOnline%", String.valueOf(MINIMUM_PLAYER_ONLINE)));
        }
    }

    public long getClosestTimeFrameMillis() {
        List<Long> timeFrameMillisList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTimeDateFormat = dateFormat.format(new Date());
        long closestTimeFrameMillis = 0;
        for (String timeFrame : EVENT_TIME_FRAME) {
            try {
                timeFrameMillisList.add(dateFormat.parse(timeFrame).getTime());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        Collections.sort(timeFrameMillisList);
        for (long timeFrameMillis : timeFrameMillisList) {
            try {
                long currentTimeMillis = dateFormat.parse(currentTimeDateFormat).getTime();
                if (timeFrameMillis > currentTimeMillis) {
                    closestTimeFrameMillis = timeFrameMillis;
                    break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return closestTimeFrameMillis;
    }

    public long getClosestTimeFrameTimeLeft() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            return (getClosestTimeFrameMillis() - dateFormat.parse(dateFormat.format(new Date())).getTime()) / 1000;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean isStarting() {
        return STARTING;
    }

    public int getEventTimeLeft() {
        return TIMELEFT;
    }

    public BukkitTask getWarEventTask() {
        return warEventTask;
    }

    public int getWarEventTaskID() {
        return warEventTask.getTaskId();
    }

    public void sendMessage(Player player, String message) {
        if (player == null || message == null || message.equals(""))
            return;

        player.sendMessage(ClansPlus.nms.addColor(message.replace("%prefix%", MESSAGES_PREFIX)));
    }
}
