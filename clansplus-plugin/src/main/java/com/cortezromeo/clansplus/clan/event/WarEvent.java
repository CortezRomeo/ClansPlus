package com.cortezromeo.clansplus.clan.event;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.clan.SkillManager;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.clan.skill.SkillData;
import com.cortezromeo.clansplus.clan.skill.plugin.BoostScoreSkill;
import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.CommandUtil;
import com.cortezromeo.clansplus.util.HashMapUtil;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class WarEvent {

    public WrappedTask wrappedTask;
    public boolean ENABLED;
    public boolean PLAYER_JOIN_NOTIFICATION_ENABLED;
    public String MESSAGES_PREFIX;
    public String MESSAGES_EVENT_NOT_STARTING;
    public String MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_EVENTTIMEFRAME;
    public String MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_REQUIREDWORLDS;
    public String MESSAGES_EVENT_STARTING;
    public String MESSAGES_EVENT_STARTING_PLACEHOLDER_REQUIREDWORLDS;
    public int MESSAGES_EVENT_MAX_TOP;
    public String MESSAGES_EVENT_ENDING;
    public String MESSAGES_NOT_ENOUGH_PLAYER;
    public String MESSAGES_CLAN_BROADCAST_PREFIX;
    public String MESSAGES_CLAN_BROADCAST_GAIN_SCORE_PLAYER;
    public String MESSAGES_CLAN_BROADCAST_GAIN_SCORE_MOB;
    public String MESSAGES_CLAN_BROADCAST_PLACEHOLDER_CHECKBOOSTSCORE;
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
    public boolean ENDING_REWARD_ENABLED;
    public List<String> ENDING_COMMANDS;
    private boolean STARTING = false;
    private long MAXTIMELEFT = 0;
    private long TIMELEFT = 0;
    private HashMap<String, Long> playerDamagesCaused = new HashMap<>();
    private HashMap<String, Long> playerDamagesCollected = new HashMap<>();
    private HashMap<String, Long> clanScoreCollected = new HashMap<>();
    private HashMap<Player, BossBar> bossBarDatabase = new HashMap<>();

    public WarEvent() {
        setupValue();
    }

    public void setupValue() {
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
        MESSAGES_EVENT_MAX_TOP = eventFileConfig.getInt(eventPath + "messages.event-ending.max-top");
        MESSAGES_EVENT_ENDING = eventFileConfig.getString(eventPath + "messages.event-ending.messages");
        MESSAGES_NOT_ENOUGH_PLAYER = eventFileConfig.getString(eventPath + "messages.not-enough-player");
        MESSAGES_CLAN_BROADCAST_PREFIX = eventFileConfig.getString(eventPath + "messages.clan-broadcast.prefix");
        MESSAGES_CLAN_BROADCAST_GAIN_SCORE_PLAYER = eventFileConfig.getString(eventPath + "messages.clan-broadcast.gain-score-player");
        MESSAGES_CLAN_BROADCAST_GAIN_SCORE_MOB = eventFileConfig.getString(eventPath + "messages.clan-broadcast.gain-score-mob");
        MESSAGES_CLAN_BROADCAST_PLACEHOLDER_CHECKBOOSTSCORE = eventFileConfig.getString(eventPath + "messages.clan-broadcast-placeholders.checkBoostScore");
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
        SCORE_PLAYER = eventFileConfig.getInt(eventPath + "score-settings.player");
        STARTING_COMMANDS = eventFileConfig.getStringList(eventPath + "commands.starting-commands");
        ENDING_REWARD_ENABLED = eventFileConfig.getBoolean(eventPath + "ending-rewards.enabled");
        ENDING_COMMANDS = eventFileConfig.getStringList(eventPath + "commands.ending-commands");
    }

    // start the event
    public void runEvent(boolean checkPlayerSize) {
        if (STARTING || !ENABLED) return;

        if (checkPlayerSize) {
            if (Bukkit.getOnlinePlayers().size() < MINIMUM_PLAYER_ONLINE) {
                for (Player player : Bukkit.getOnlinePlayers())
                    sendMessage(player, MESSAGES_NOT_ENOUGH_PLAYER);
                return;
            }
        }

        TIMELEFT = EVENT_TIME;
        MAXTIMELEFT = TIMELEFT;
        STARTING = true;

        // send event starting messages and create boss bar
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendEventStatusMessage(player, true);
            createBossBar(player);
        }

        try {
            if (ClansPlus.support.getDiscordSupport() != null)
                // send discord message
                ClansPlus.support.getDiscordSupport().sendMessage(ClansPlus.support.getDiscordSupport().getWarEventStartingMessage(ClansPlus.plugin.getDataFolder() + "/discordsrv-warevent-starting.json", EventManager.getWarEvent()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        wrappedTask = ClansPlus.support.getFoliaLib().getScheduler().runTimerAsync(() -> {
            if (TIMELEFT > 0) {
                // update boss bar
                for (Player player : Bukkit.getOnlinePlayers())
                    ClansPlus.support.getFoliaLib().getScheduler().runAtEntity(player, task -> createBossBar(player));
                TIMELEFT--;
            }
            // ending event
            if (TIMELEFT <= 0 || !isStarting()) {
                endEvent(true, true, true);
            }
        }, 0, 20L);

    }

    public void endEvent(boolean sendMessage, boolean playSound, boolean reward) {
        if (!isStarting()) return;

        // set event time left to zero
        TIMELEFT = 0;

        if (wrappedTask != null) if (!wrappedTask.isCancelled()) wrappedTask.cancel();

        List<String> topClanScoreCollected = HashMapUtil.sortFromGreatestToLowestL(getClanScoreCollected());
        List<String> topPlayerDamagesCaused = HashMapUtil.sortFromGreatestToLowestL(getPlayerDamagesCaused());
        List<String> topPlayerDamagesCollected = HashMapUtil.sortFromGreatestToLowestL(getPlayerDamagesCollected());

        // send message
        String eventEndingMessage = MESSAGES_EVENT_ENDING;
        if (sendMessage) {
            for (int i = 0; i <= MESSAGES_EVENT_MAX_TOP; i++) {
                // start with 1
                int top = i + 1;

                try {
                    String clanScoreCollected = topClanScoreCollected.get(i);
                    eventEndingMessage = eventEndingMessage.replace("%topScoreClaimed_" + top + "_name%", clanScoreCollected);
                    eventEndingMessage = eventEndingMessage.replace("%topScoreClaimed_" + top + "_score%", String.valueOf(getClanScoreCollected(clanScoreCollected)));
                } catch (Exception exception) {
                    eventEndingMessage = eventEndingMessage.replace("%topScoreClaimed_" + top + "_name%", Messages.UNKNOWN);
                    eventEndingMessage = eventEndingMessage.replace("%topScoreClaimed_" + top + "_score%", "0");
                }

                try {
                    String playerDamagesCaused = topPlayerDamagesCaused.get(i);
                    eventEndingMessage = eventEndingMessage.replace("%topDamage_" + top + "_name%", playerDamagesCaused);
                    eventEndingMessage = eventEndingMessage.replace("%topDamage_" + top + "_score%", String.valueOf(getPlayerDamagesCaused(playerDamagesCaused)));
                } catch (Exception exception) {
                    eventEndingMessage = eventEndingMessage.replace("%topDamage_" + top + "_name%", Messages.UNKNOWN);
                    eventEndingMessage = eventEndingMessage.replace("%topDamage_" + top + "_score%", "0");
                }

                try {
                    String playerDamagesCollected = topPlayerDamagesCollected.get(i);
                    eventEndingMessage = eventEndingMessage.replace("%topTank_" + top + "_name%", playerDamagesCollected);
                    eventEndingMessage = eventEndingMessage.replace("%topTank_" + top + "_score%", String.valueOf(getPlayerDamagesCollected(playerDamagesCollected)));
                } catch (Exception exception) {
                    eventEndingMessage = eventEndingMessage.replace("%topTank_" + top + "_name%", Messages.UNKNOWN);
                    eventEndingMessage = eventEndingMessage.replace("%topTank_" + top + "_score%", "0");
                }
            }
            eventEndingMessage = eventEndingMessage.replace("%totalDamagesCaused%", String.valueOf(getTotalDamageCaused()));
            eventEndingMessage = eventEndingMessage.replace("%totalDamagesCollected%", String.valueOf(getTotalDamageCollected()));
            eventEndingMessage = eventEndingMessage.replace("%totalScoreCollected%", String.valueOf(getTotalScoreCollected()));
        }

        // send discord message
        try {
            if (ClansPlus.support.getDiscordSupport() != null)
                ClansPlus.support.getDiscordSupport().sendMessage(ClansPlus.support.getDiscordSupport().getWarEventEndingMessage(ClansPlus.plugin.getDataFolder() + "/discordsrv-warevent-ending.json", EventManager.getWarEvent()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sendMessage) sendMessage(player, eventEndingMessage);
            if (playSound)
                player.playSound(player.getLocation(), ClansPlus.nms.createSound(ENDING_SOUND_NAME), ENDING_SOUND_VOLUME, ENDING_SOUND_PITCH);
            removeBossBar(player);
        }

        if (ENDING_REWARD_ENABLED && reward) {
            FileConfiguration eventConfigFile = EventsFile.get();
            if (!getClanScoreCollected().isEmpty()) for (int i = 0; i <= topClanScoreCollected.size() - 1; i++) {
                int top = i + 1;
                String clanName = topClanScoreCollected.get(i);

                String configPath = "events.clan-war-event.ending-rewards.top-score-clans";

                if (eventConfigFile.getConfigurationSection(configPath) != null) {
                    if (eventConfigFile.getConfigurationSection(configPath).getKeys(false).contains(String.valueOf(top))) {
                        if (!PluginDataManager.getClanDatabase().containsKey(clanName)) continue;

                        long warPointRewarded = eventConfigFile.getLong(configPath + "." + top + ".warpoint");
                        PluginDataManager.getClanDatabase(clanName).setWarPoint(PluginDataManager.getClanDatabase(clanName).getWarPoint() + warPointRewarded);
                        PluginDataManager.saveClanDatabaseToStorage(clanName);

                        for (String commandsRewarded : eventConfigFile.getStringList(configPath + "." + top + ".commands"))
                            CommandUtil.dispatchCommand(null, commandsRewarded.replace("%clan%", clanName));
                    }
                }
            }
            if (!getPlayerDamagesCaused().isEmpty()) for (int i = 0; i <= topPlayerDamagesCaused.size() - 1; i++) {
                int top = i + 1;
                String playerName = topPlayerDamagesCaused.get(i);

                String configPath = "events.clan-war-event.ending-rewards.most-damage-caused-players";

                if (eventConfigFile.getConfigurationSection(configPath) != null) {
                    if (eventConfigFile.getConfigurationSection(configPath).getKeys(false).contains(String.valueOf(top))) {
                        if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) continue;

                        long warPointRewarded = eventConfigFile.getLong(configPath + "." + top + ".warpoint");
                        String playerClanName = PluginDataManager.getPlayerDatabase(playerName).getClan();

                        if (playerClanName == null) continue;

                        PluginDataManager.getClanDatabase(playerClanName).setWarPoint(PluginDataManager.getClanDatabase(playerClanName).getWarPoint() + warPointRewarded);
                        PluginDataManager.saveClanDatabaseToStorage(playerClanName);

                        for (String commandsRewarded : eventConfigFile.getStringList(configPath + "." + top + ".commands"))
                            CommandUtil.dispatchCommand(Bukkit.getPlayer(playerName), commandsRewarded.replace("%clan%", playerClanName).replace("%player%", playerName));
                    }
                }
            }
        }
        STARTING = false;

        // clear war data after each event
        playerDamagesCaused.clear();
        playerDamagesCollected.clear();
        clanScoreCollected.clear();
    }

    public void onJoin(PlayerJoinEvent event) {
        if (!PLAYER_JOIN_NOTIFICATION_ENABLED) return;
        Player player = event.getPlayer();
        sendEventStatusMessage(player, false);

        // if the event already started, create a boss bar for the player
        if (isStarting()) createBossBar(player);
    }

    public void onDamage(EntityDamageByEntityEvent event) {
        if (!isStarting()) return;

        if (event.isCancelled()) return;

        Entity entityDamager = event.getDamager();
        Entity entityVictim = event.getEntity();

        if (entityDamager == null || entityVictim == null) return;
        if (entityDamager.getType() != EntityType.PLAYER || entityVictim.getType() != EntityType.PLAYER) return;

        Player damager = (Player) entityDamager;
        Player victim = (Player) entityVictim;

        if (!ClanManager.isPlayerInClan(damager) || !ClanManager.isPlayerInClan(victim)) return;

        if (WORLD_REQUIREMENT_ENABLED) if (!WORLD_REQUIREMENT_WORLDS.contains(damager.getWorld().getName())) return;

        IClanData damagerClanData = PluginDataManager.getClanDatabaseByPlayerName(damager.getName());

        if (damagerClanData == null) return;

        SkillData dodgeSkillData = SkillManager.getSkillData().get(SkillManager.getSkillID(PluginSkill.DODGE));
        if (dodgeSkillData != null) {
            if (dodgeSkillData.onDamage(dodgeSkillData, event)) {
                return;
            }
        }

        getPlayerDamagesCaused().put(damager.getName(), getPlayerDamagesCaused(damager.getName()) + (long) event.getDamage());
        getPlayerDamagesCollected().put(entityVictim.getName(), getPlayerDamagesCollected(entityVictim.getName()) + (long) event.getDamage());

        for (int skillID : damagerClanData.getSkillLevel().keySet()) {
            SkillData skillData = SkillManager.getSkillData().get(skillID);
            if (skillData != null) if (SkillManager.getSkillID(PluginSkill.DODGE) != skillData.getId())
                skillData.onDamage(skillData, event);
        }
    }

    public void onPlayerDie(PlayerDeathEvent event) {
        if (!isStarting()) return;

        Entity entityKiller = event.getEntity().getKiller();
        Entity entityVictim = event.getEntity();

        if (entityKiller == null || entityVictim == null) return;
        if (entityKiller.getType() != EntityType.PLAYER || entityVictim.getType() != EntityType.PLAYER) return;

        Player killer = (Player) entityKiller;

        if (WORLD_REQUIREMENT_ENABLED) if (!WORLD_REQUIREMENT_WORLDS.contains(killer.getWorld().getName())) return;

        IClanData killerClanData = PluginDataManager.getClanDatabaseByPlayerName(killer.getName());
        IClanData victimClanData = PluginDataManager.getClanDatabaseByPlayerName(entityVictim.getName());

        if (killerClanData == null || victimClanData == null) return;

        if (killerClanData.getName().equals(victimClanData.getName())) return;

        if (!killerClanData.getAllies().isEmpty())
            if (killerClanData.getAllies().contains(victimClanData.getName())) return;

        killerClanData.setScore(killerClanData.getScore() + SCORE_PLAYER);
        // save to hash map because the database will update a lot during war event
        PluginDataManager.saveClanDatabaseToHashMap(killerClanData.getName(), killerClanData);
        clanScoreCollected.put(killerClanData.getName(), getClanScoreCollected(killerClanData.getName()) + SCORE_PLAYER);

        SkillData boostScoreSkillData = SkillManager.getSkillData().get(SkillManager.getSkillID(PluginSkill.BOOST_SCORE));
        String checkBoostScore = "";
        if (boostScoreSkillData != null) {
            boostScoreSkillData.onDie(boostScoreSkillData, killer.getName(), entityVictim.getName(), false);
            int clanBoostScoreSkillLevel = killerClanData.getSkillLevel().get(boostScoreSkillData.getId());
            if (clanBoostScoreSkillLevel > 0)
                checkBoostScore = MESSAGES_CLAN_BROADCAST_PLACEHOLDER_CHECKBOOSTSCORE.replace("%bonusScore%", String.valueOf(BoostScoreSkill.boostScoreLevel.get(clanBoostScoreSkillLevel)));
        }
        alertClan(killerClanData.getName(), MESSAGES_CLAN_BROADCAST_GAIN_SCORE_PLAYER.replace("%player%", killer.getName()).replace("%target%", entityVictim.getName()).replace("%score%", String.valueOf(SCORE_PLAYER)).replace("%checkBoostScore%", checkBoostScore));
    }

    public void onEntityDie(EntityDeathEvent event) {
        if (!isStarting()) return;

        Entity entityKiller = event.getEntity().getKiller();
        Entity entityVictim = event.getEntity();

        if (entityKiller == null || entityVictim == null) return;

        if (entityKiller.getType() != EntityType.PLAYER || entityVictim.getType() == EntityType.PLAYER) return;

        Player killer = (Player) entityKiller;
        String entityName = entityVictim.getName().toUpperCase();

        if (WORLD_REQUIREMENT_ENABLED) if (!WORLD_REQUIREMENT_WORLDS.contains(killer.getWorld().getName())) return;

        IClanData killerClanData = PluginDataManager.getClanDatabaseByPlayerName(killer.getName());

        if (killerClanData == null) return;

        int scoreAdded = 0;
        boolean isMythicMobsMob = false;
        if (ClansPlus.support.isMythicMobsSupported()) {
            try {
                if (MythicBukkit.inst().getMobManager().isMythicMob(entityVictim)) {
                    ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entityVictim);
                    String mobName = mob.getMobType();
                    if (SCORE_MYTHICMOBS_MOBS.containsKey(mobName)) {
                        scoreAdded = SCORE_MYTHICMOBS_MOBS.get(mobName);
                        isMythicMobsMob = true;
                    }
                }
            } catch (Exception exception) {
                MessageUtil.throwErrorMessage("[MythicMobs] Lối khi sử dụng API của mythicMobs! (" + exception.getMessage() + ")");
            }
        }

        if (!isMythicMobsMob) {
            if (!SCORE_VANILLA_MOBS.containsKey(entityName)) return;
            scoreAdded = SCORE_VANILLA_MOBS.get(entityName);
        }

        killerClanData.setScore(killerClanData.getScore() + scoreAdded);
        // save to hash map because the database will update a lot during war event
        PluginDataManager.saveClanDatabaseToHashMap(killerClanData.getName(), killerClanData);
        clanScoreCollected.put(killerClanData.getName(), getClanScoreCollected(killerClanData.getName()) + scoreAdded);

        SkillData boostScoreSkillData = SkillManager.getSkillData().get(SkillManager.getSkillID(PluginSkill.BOOST_SCORE));
        String checkBoostScore = "";
        if (boostScoreSkillData != null) {
            boostScoreSkillData.onDie(boostScoreSkillData, killer.getName(), entityName, true);
            int clanBoostScoreSkillLevel = killerClanData.getSkillLevel().get(boostScoreSkillData.getId());
            if (clanBoostScoreSkillLevel > 0)
                checkBoostScore = MESSAGES_CLAN_BROADCAST_PLACEHOLDER_CHECKBOOSTSCORE.replace("%bonusScore%", String.valueOf(BoostScoreSkill.boostScoreLevel.get(clanBoostScoreSkillLevel)));
        }
        alertClan(killerClanData.getName(), MESSAGES_CLAN_BROADCAST_GAIN_SCORE_MOB.replace("%player%", killer.getName()).replace("%target%", entityVictim.getName()).replace("%score%", String.valueOf(scoreAdded)).replace("%checkBoostScore%", checkBoostScore));
    }

    public void sendEventStatusMessage(Player player, boolean playSound) {
        if (!isStarting()) {
            StringBuilder eventTimeFrame = new StringBuilder();
            StringBuilder eventRequiredWorlds = new StringBuilder();
            for (String timeFrame : EVENT_TIME_FRAME) {
                eventTimeFrame.append(MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_EVENTTIMEFRAME.replace("%eventTimeFrame%", timeFrame)).append("\n");
            }
            for (String requiredWorld : WORLD_REQUIREMENT_WORLDS)
                eventRequiredWorlds.append(MESSAGES_EVENT_NOT_STARTING_PLACEHOLDER_REQUIREDWORLDS.replace("%requiredWorld%", requiredWorld)).append("\n");
            sendMessage(player, MESSAGES_EVENT_NOT_STARTING.replace("%eventTimeFrame%", eventTimeFrame.toString()).replace("%requiredWorlds%", eventRequiredWorlds.toString()).replace("%closestTimeFrame%", new SimpleDateFormat("HH:mm:ss").format(new Date(getClosestTimeFrameMillis()))).replace("%closestTimeFrameTimeLeft%", String.valueOf(StringUtil.getTimeFormat(getClosestTimeFrameTimeLeft(), Messages.TIME_FORMAT_HHMMSS, Messages.TIME_FORMAT_MMSS, Messages.TIME_FORMAT_SS))).replace("%minimumPlayerOnline%", String.valueOf(MINIMUM_PLAYER_ONLINE)));
        } else {
            StringBuilder eventRequiredWorlds = new StringBuilder();
            for (String requiredWorld : WORLD_REQUIREMENT_WORLDS)
                eventRequiredWorlds.append(MESSAGES_EVENT_STARTING_PLACEHOLDER_REQUIREDWORLDS.replace("%requiredWorld%", requiredWorld)).append("\n");
            if (playSound)
                player.playSound(player.getLocation(), ClansPlus.nms.createSound(STARTING_SOUND_NAME), STARTING_SOUND_VOLUME, STARTING_SOUND_PITCH);
            sendMessage(player, MESSAGES_EVENT_STARTING.replace("%eventTimeLeft%", StringUtil.getTimeFormat(TIMELEFT, Messages.TIME_FORMAT_HHMMSS, Messages.TIME_FORMAT_MMSS, Messages.TIME_FORMAT_SS)).replace("%requiredWorlds%", eventRequiredWorlds.toString()));
        }
    }

    public long getClosestTimeFrameMillis() {
        List<Long> timeFrameMillisList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTimeDateFormat = dateFormat.format(new Date());
        Collections.sort(timeFrameMillisList);
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
                if (timeFrameMillis > currentTimeMillis) return timeFrameMillis;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return timeFrameMillisList.get(0);
    }

    public long getClosestTimeFrameTimeLeft() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            Date currentTimeDateFormat = dateFormat.parse(dateFormat.format(new Date()));
            long closestTimeFrameTimeLeft = (getClosestTimeFrameMillis() - currentTimeDateFormat.getTime()) / 1000;
            if (closestTimeFrameTimeLeft > 0) return closestTimeFrameTimeLeft;
            else {
                Date closestTimeFrameOnTheNextDay = new Date(getClosestTimeFrameMillis());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(closestTimeFrameOnTheNextDay);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                closestTimeFrameOnTheNextDay = calendar.getTime();

                return (closestTimeFrameOnTheNextDay.getTime() - currentTimeDateFormat.getTime()) / 1000;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public void createBossBar(Player player) {
        if (!isStarting()) {
            removeBossBar(player);
            return;
        }

        if (!getBossBarDatabase().containsKey(player)) {
            BossBar newBossBar = Bukkit.createBossBar("...", BOSS_BAR_COLOR, BOSS_BAR_STYLE);
            newBossBar.setProgress(1);
            newBossBar.addPlayer(player);
            newBossBar.setVisible(true);
            getBossBarDatabase().put(player, newBossBar);
        }

        BossBar playerBossBar = getBossBarDatabase().get(player);
        playerBossBar.setTitle(ClansPlus.nms.addColor(BOSS_BAR_TITLE.replace("%timeLeft%", StringUtil.getTimeFormat(TIMELEFT, Messages.TIME_FORMAT_HHMMSS, Messages.TIME_FORMAT_MMSS, Messages.TIME_FORMAT_SS))));
        try {
            if (MAXTIMELEFT == 0) {
                if (TIMELEFT == 0) MAXTIMELEFT = 1;
                else MAXTIMELEFT = TIMELEFT;
            }
            playerBossBar.setProgress((double) TIMELEFT / (double) MAXTIMELEFT);
        } catch (Exception exception) {
            playerBossBar.setProgress(1);
        }
    }

    public void removeBossBar(Player player) {
        if (getBossBarDatabase().containsKey(player)) {
            getBossBarDatabase().get(player).removeAll();
            getBossBarDatabase().remove(player);
        }
    }

    public long getTimeLeft() {
        return TIMELEFT;
    }

    public void setTimeLeft(int seconds) {
        TIMELEFT = seconds;
        MAXTIMELEFT = TIMELEFT;
    }

    public HashMap<String, Long> getPlayerDamagesCaused() {
        return playerDamagesCaused;
    }

    public Long getPlayerDamagesCaused(String playerName) {
        return playerDamagesCaused.getOrDefault(playerName, 0L);
    }

    public HashMap<String, Long> getPlayerDamagesCollected() {
        return playerDamagesCollected;
    }

    public Long getPlayerDamagesCollected(String playerName) {
        return playerDamagesCollected.getOrDefault(playerName, 0L);
    }

    public HashMap<String, Long> getClanScoreCollected() {
        return clanScoreCollected;
    }

    public long getClanScoreCollected(String clanName) {
        return clanScoreCollected.getOrDefault(clanName, 0L);
    }

    public boolean isStarting() {
        return STARTING;
    }

    public HashMap<Player, BossBar> getBossBarDatabase() {
        return bossBarDatabase;
    }

    public void sendMessage(Player player, String message) {
        if (player == null || message == null || message.equals("")) return;

        player.sendMessage(ClansPlus.nms.addColor(message.replace("%prefix%", MESSAGES_PREFIX)));
    }

    public long getTotalDamageCaused() {
        long totalDamageCaused = 0;
        if (!playerDamagesCaused.isEmpty()) for (String player : playerDamagesCaused.keySet()) {
            totalDamageCaused = totalDamageCaused + playerDamagesCaused.get(player);
        }
        return totalDamageCaused;
    }

    public long getTotalDamageCollected() {
        long totalDamageCollected = 0;
        if (!playerDamagesCollected.isEmpty()) for (String player : playerDamagesCollected.keySet()) {
            totalDamageCollected = totalDamageCollected + playerDamagesCollected.get(player);
        }
        return totalDamageCollected;
    }

    public long getTotalScoreCollected() {
        long totalScoreCollected = 0;
        if (!clanScoreCollected.isEmpty()) for (String clan : clanScoreCollected.keySet()) {
            totalScoreCollected = totalScoreCollected + clanScoreCollected.get(clan);
        }
        return totalScoreCollected;
    }

    public void alertClan(String clanName, String message) {
        if (!ClanManager.isClanExisted(clanName) || message == null) return;

        IClanData clanData = PluginDataManager.getClanDatabase(clanName);
        for (String playerInClan : clanData.getMembers()) {
            Player player = Bukkit.getPlayer(playerInClan);
            MessageUtil.sendMessage(player, StringUtil.setClanNamePlaceholder(message.replace("%prefix%", MESSAGES_CLAN_BROADCAST_PREFIX), clanName));
        }
    }
}
