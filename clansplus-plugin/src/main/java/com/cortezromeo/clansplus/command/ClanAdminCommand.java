package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.enums.ItemType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.clan.skill.plugin.BoostScoreSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.CriticalHitSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.DodgeSkill;
import com.cortezromeo.clansplus.clan.skill.plugin.LifeStealSkill;
import com.cortezromeo.clansplus.file.EventsFile;
import com.cortezromeo.clansplus.file.SkillsFile;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.file.inventory.*;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.language.Vietnamese;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.support.DiscordSupport;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClanAdminCommand implements CommandExecutor, TabExecutor {

    public static List<CommandSender> commandConfirmation = new ArrayList<>();
    public static List<CommandSender> transferDataCommandNotifying = new ArrayList<>();

    public ClanAdminCommand() {
        ClansPlus.plugin.getCommand("clanadmin").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            String commandPermission = "clanplus.admin";
            if (!player.hasPermission(commandPermission)) {
                MessageUtil.sendMessage(player, Messages.PERMISSION_REQUIRED.replace("%permission%", commandPermission));
                return false;
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                long time = System.currentTimeMillis();

                ClansPlus.plugin.reloadConfig();
                ClanListInventoryFile.reload();
                NoClanInventoryFile.reload();
                ClanMenuInventoryFile.reload();
                MembersMenuInventoryFile.reload();
                AddMemberListInventoryFile.reload();
                MemberListInventoryFile.reload();
                ManageMemberInventoryFile.reload();
                ManageMemberRankInventoryFile.reload();
                AlliesMenuInventoryFile.reload();
                AddAllyListInventoryFile.reload();
                AllyInvitationInventoryFile.reload();
                AllyInivtationConfirmInventoryFile.reload();
                AllyListInventoryFile.reload();
                ManageAllyInventoryFile.reload();
                ViewClanInventoryFile.reload();
                UpgradePluginSkillListInventoryFile.reload();
                UpgradeMenuInventoryFile.reload();
                SkillsMenuInventoryFile.reload();
                EventsMenuInventoryFile.reload();
                ClanSettingsInventoryFile.reload();
                SetIconCustomHeadListInventoryFile.reload();
                SetIconMaterialListInventoryFile.reload();
                SetIconMenuInventoryFile.reload();
                SetPermissionInventoryFile.reload();
                DisbandConfirmationInventoryFile.reload();
                LeaveConfirmationInventoryFile.reload();
                EventsFile.reload();
                SkillsFile.reload();
                UpgradeFile.reload();

                Settings.setupValue();

                Vietnamese.reload();
                Messages.setupValue(Settings.LANGUAGE);

                CriticalHitSkill.registerSkill();
                DodgeSkill.registerSkill();
                LifeStealSkill.registerSkill();
                BoostScoreSkill.registerSkill();

                EventManager.getWarEvent().setupValue();
                ClansPlus.support.discordSupport = new DiscordSupport(Settings.SOFT_DEPEND_DISCORDWEBHOOK_URL);

                sender.sendMessage("Plugin reloaded (" + (System.currentTimeMillis() - time) + "ms)");
                return false;
            }
            if (args[0].equalsIgnoreCase("backup")) {
                sender.sendMessage("Creating backup, please wait...");
                ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {
                    PluginDataManager.backupAll(null);
                    sender.sendMessage("Backup successful! Backup file will be in the 'backup' folder inside the plugin directory.");
                    sender.sendMessage("Database type: " + ClansPlus.databaseType.toString().toUpperCase());
                });
                return false;
            }
            if (args[0].equalsIgnoreCase("chatspy")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!ClanManager.getPlayerUsingChatSpy().contains(player)) {
                        ClanManager.getPlayerUsingChatSpy().add(player);
                        player.sendMessage("Clan chat spy enabled successfully.");
                        return false;
                    } else {
                        ClanManager.getPlayerUsingChatSpy().remove(player);
                        player.sendMessage("Clan chat spy disabled successfully.");
                        return false;
                    }
                } else {
                    if (ClanManager.isConsoleUsingChatSpy()) {
                        ClanManager.consoleUsingChatSpy = false;
                        sender.sendMessage("Clan chat spy disabled successfully.");
                        return false;
                    } else {
                        ClanManager.consoleUsingChatSpy = true;
                        sender.sendMessage("Clan chat spy enabled successfully.");
                        return false;
                    }
                }
            }
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                String clanName = args[1];
                if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                    sender.sendMessage("Clan " + clanName + " does not exist.");
                    return false;
                } else {
                    ClanDataType clanDataType;
                    try {
                        clanDataType = ClanDataType.valueOf(args[2].toLowerCase());
                    } catch (Exception exception) {
                        sender.sendMessage("Type " + (args[2] == null ? "" : args[2] + " ") + "is invalid.");
                        return false;
                    }

                    IClanData clanData = PluginDataManager.getClanDatabase(clanName);

                    if (clanData == null) {
                        sender.sendMessage("Clan " + clanName + " does not exist.");
                        return false;
                    }

                    // Score
                    if (clanDataType == ClanDataType.score) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setScore(0);
                            sender.sendMessage("Successfully reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("give")) {
                                clanData.setScore(clanData.getScore() + value);
                                sender.sendMessage("Gave " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getScore());
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                clanData.setScore(clanData.getScore() - value);
                                sender.sendMessage("Took " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getScore());
                            } else if (args[3].equalsIgnoreCase("set")) {
                                clanData.setScore(value);
                                sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getScore());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value is not available.");
                            return false;
                        }
                    }

                    // Warpoint
                    if (clanDataType == ClanDataType.warpoint) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setWarPoint(0);
                            sender.sendMessage("Reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("give")) {
                                clanData.setWarPoint(clanData.getWarPoint() + value);
                                sender.sendMessage("Gave " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getWarPoint());
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                clanData.setWarPoint(clanData.getWarPoint() - value);
                                sender.sendMessage("Took " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getWarPoint());
                            } else if (args[3].equalsIgnoreCase("set")) {
                                clanData.setWarPoint(value);
                                sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getWarPoint());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value is not available.");
                            return false;
                        }
                    }

                    // Warning
                    if (clanDataType == ClanDataType.warning) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setWarning(0);
                            sender.sendMessage("Reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("give")) {
                                clanData.setWarning(clanData.getWarning() + value);
                                sender.sendMessage("Gave " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getWarning());
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                clanData.setWarning(clanData.getWarning() - value);
                                sender.sendMessage("Took " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getWarning());
                            } else if (args[3].equalsIgnoreCase("set")) {
                                clanData.setWarning(value);
                                sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getWarning());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value is not available.");
                            return false;
                        }
                    }

                    // Icon
                    if (clanDataType == ClanDataType.icon) {
                        ItemType itemType;
                        try {
                            itemType = ItemType.valueOf(args[4].toUpperCase());
                        } catch (Exception exception) {
                            sender.sendMessage("Icon type is not available!");
                            return false;
                        }
                        try {
                            String iconValue = args[5];
                            if (iconValue == null) {
                                sender.sendMessage("Please type icon value!");
                                return false;
                            }

                            if (itemType == ItemType.MATERIAL) {
                                try {
                                    XMaterial xMaterial = XMaterial.valueOf(iconValue);
                                    Material material = xMaterial.get();
                                    if (material == null || material.equals(Material.AIR)) {
                                        sender.sendMessage("Icon value " + iconValue + " is not available!");
                                        sender.sendMessage("Materials list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                                        return false;
                                    }
                                } catch (Exception exception) {
                                    sender.sendMessage("Icon value " + iconValue + " is not available!");
                                    return false;
                                }
                            }
                            PluginDataManager.getClanDatabase(clanName).setIconType(itemType);
                            PluginDataManager.getClanDatabase(clanName).setIconValue(iconValue);
                            sender.sendMessage("Set icon for " + clanName + " to:");
                            sender.sendMessage("Icon type: " + itemType);
                            sender.sendMessage("Icon value: " + iconValue);
                        } catch (Exception exception) {
                            sender.sendMessage("Please type available icon value!");
                            return false;
                        }
                        return false;
                    }

                    // Created date
                    if (clanDataType == ClanDataType.createddate) {
                        try {
                            long value = Long.parseLong(args[4]);
                            if (args[3].equalsIgnoreCase("set")) {
                                clanData.setCreatedDate(value);
                                sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getCreatedDate());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value is not available.");
                            return false;
                        }
                    }

                    // Custom name
                    if (clanDataType == ClanDataType.customname) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setCustomName(null);
                            sender.sendMessage("Reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        StringBuilder builder = new StringBuilder();
                        for (int i = 4; i < args.length; i++)
                            builder.append(args[i]).append(" ");
                        builder.deleteCharAt(builder.length() - 1);
                        String value  = builder.toString();

                        if (args[3].equalsIgnoreCase("set")) {
                            clanData.setCustomName(value);
                            sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                            sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getCustomName());
                        }
                    }

                    // Message
                    if (clanDataType == ClanDataType.message) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setMessage(null);
                            sender.sendMessage("Reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        StringBuilder builder = new StringBuilder();
                        for (int i = 4; i < args.length; i++)
                            builder.append(args[i]).append(" ");
                        builder.deleteCharAt(builder.length() - 1);
                        String value  = builder.toString();

                        if (args[3].equalsIgnoreCase("set")) {
                            clanData.setMessage(value);
                            sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                            sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getMessage());
                        }
                    }

                    // Subject permission
                    if (clanDataType == ClanDataType.subjectpermission) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            HashMap<Subject, Rank> newPermissionDefault = new HashMap<>();
                            for (Subject subject : Subject.values())
                                newPermissionDefault.put(subject, Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(subject));
                            clanData.setSubjectPermission(newPermissionDefault);
                            sender.sendMessage("Reset permission of " + clanName + " to default based on config.yml");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("set")) {

                            Subject subject;
                            try {
                                subject = Subject.valueOf(args[4].toUpperCase());
                            } catch (Exception exception) {
                                sender.sendMessage("Value is not available.");
                                return false;
                            }

                            Rank newRank;
                            try {
                                newRank = Rank.valueOf(args[5].toUpperCase());
                            } catch (Exception exception) {
                                sender.sendMessage("Rank is not available.");
                                return false;
                            }

                            clanData.getSubjectPermission().put(subject, newRank);

                            sender.sendMessage("Set subject " + subject + " of " + clanName + " to " + newRank);
                        }
                    }

                    // Discord channel ID
                    if (clanDataType == ClanDataType.discordchannelid) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setDiscordChannelID(0);
                            sender.sendMessage("Reset " + clanName + " " + clanDataType);
                            return false;
                        }
                        try {
                            int value = Integer.parseInt(args[4]);
                            if (args[3].equalsIgnoreCase("set")) {
                                clanData.setDiscordChannelID(value);
                                sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                                sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getDiscordChannelID());
                            }
                        } catch (Exception exception) {
                            sender.sendMessage("Value is not available.");
                            return false;
                        }
                    }

                    // Discord join link
                    if (clanDataType == ClanDataType.discordjoinlink) {
                        if (args[3].equalsIgnoreCase("reset")) {
                            clanData.setDiscordJoinLink(null);
                            sender.sendMessage("Reset " + clanName + " " + clanDataType);
                            return false;
                        }

                        String value = args[4];
                        if (args[3].equalsIgnoreCase("set")) {
                            clanData.setDiscordJoinLink(value);
                            sender.sendMessage("Set " + clanName + " " + value + " " + clanDataType);
                            sender.sendMessage(clanName + "''s new " + clanDataType + ": " + clanData.getMessage());
                        }
                    }

                    // Members
                    if (clanDataType == ClanDataType.members) {
                        if (args[3].equalsIgnoreCase("add")) {
                            String playerName;
                            try {
                                playerName = args[4];
                                if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) {
                                    sender.sendMessage(playerName + " does not exist in the data!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Please type player name!");
                                return false;
                            }
                            if (clanData.getMembers().contains(playerName)) {
                                sender.sendMessage(playerName + " is already a member of this clan!");
                                return false;
                            }
                            if (ClanManager.isPlayerInClan(playerName)) {
                                sender.sendMessage(playerName + " is already in another clan!");
                                sender.sendMessage("Note: You can remove " + playerName + " from their current clan (" + PluginDataManager.getPlayerDatabase(playerName).getClan() + ") using the setPlayerData command in clanAdmin.");
                                return false;
                            }
                            ClanManager.addPlayerToAClan(playerName, clanName, true);
                            sender.sendMessage("Successfully added " + playerName + " to " + clanName + "!");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("remove")) {
                            String playerName;
                            try {
                                playerName = args[4];
                                if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) {
                                    sender.sendMessage(playerName + " does not exist in the data!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Please type player name!");
                                return false;
                            }
                            if (!clanData.getMembers().contains(playerName)) {
                                sender.sendMessage(playerName + " is not a member of this clan!");
                                return false;
                            }
                            IPlayerData playerData = PluginDataManager.getPlayerDatabase(playerName);
                            if (playerData.getRank() == Rank.LEADER) {
                                sender.sendMessage(playerName + " cannot be removed from the clan because they are the clan leader!");
                                return false;
                            }
                            PluginDataManager.getClanDatabase(playerData.getClan()).getMembers().remove(playerName);
                            PluginDataManager.clearPlayerDatabase(playerName);
                            sender.sendMessage("Removed " + playerName + " from clan " + clanName);
                            return false;
                        }
                    }


                    // Allies
                    if (clanDataType == ClanDataType.allies) {
                        if (args[3].equalsIgnoreCase("add")) {
                            String allyName;
                            try {
                                allyName = args[4];
                                if (!PluginDataManager.getClanDatabase().containsKey(allyName)) {
                                    sender.sendMessage("Clan " + allyName + " does not exist in the data!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Please enter the clan name!");
                                return false;
                            }
                            if (clanData.getAllies().contains(allyName)) {
                                sender.sendMessage(allyName + " is already an ally of this clan!");
                                return false;
                            }
                            if (allyName.equalsIgnoreCase(clanName)) {
                                sender.sendMessage(allyName + " cannot be an ally of itself.");
                                return false;
                            }
                            PluginDataManager.getClanDatabase(clanName).getAllyInvitation().remove(allyName);
                            PluginDataManager.getClanDatabase(allyName).getAllyInvitation().remove(clanName);
                            PluginDataManager.getClanDatabase(clanName).getAllies().add(allyName);
                            PluginDataManager.getClanDatabase(allyName).getAllies().add(clanName);
                            sender.sendMessage("Successfully added " + allyName + " as an ally of " + clanName + "!");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("remove")) {
                            String allyName;
                            try {
                                allyName = args[4];
                                if (!PluginDataManager.getClanDatabase().containsKey(allyName)) {
                                    sender.sendMessage("Clan " + allyName + " does not exist in the data!");
                                    return false;
                                }
                            } catch (Exception exception) {
                                sender.sendMessage("Please enter the clan name!");
                                return false;
                            }
                            if (!clanData.getAllies().contains(allyName)) {
                                sender.sendMessage(allyName + " is not an ally of this clan!");
                                return false;
                            }
                            PluginDataManager.getClanDatabase(clanName).getAllies().remove(allyName);
                            PluginDataManager.getClanDatabase(allyName).getAllies().remove(clanName);
                            sender.sendMessage("Removed clan " + allyName + " from the ally list of clan " + clanName + ".");
                            return false;
                        }
                    }

                    // save clan data eventually
                    PluginDataManager.saveClanDatabaseToStorage(clanName, clanData);
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("setclanskilldata")) {
                String clanName = args[1];
                if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                    sender.sendMessage("Clan " + clanName + " does not exist.");
                    return false;
                } else {
                    int skillID;
                    int skilLevel;

                    try {
                        skillID = Integer.parseInt(args[2]);
                        skilLevel = Integer.parseInt(args[3]);

                        PluginDataManager.getClanDatabase(clanName).getSkillLevel().put(skillID, skilLevel);
                        sender.sendMessage("Set skill ID " + skillID + " of clan " + clanName + " to " + skilLevel + ".");
                        PluginDataManager.saveClanDatabaseToStorage(clanName);
                    } catch (Exception exception) {
                        sender.sendMessage("Skill ID or Skill Level must be a number!");
                        return false;
                    }
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("clanresetall")) {
                List<String> availableTypes = new ArrayList<>();
                availableTypes.add("score");
                availableTypes.add("warpoint");
                availableTypes.add("warning");

                if (!availableTypes.contains(args[1])) {
                    sender.sendMessage("Type is not available.");
                    return false;
                }

                ClansPlus.support.getFoliaLib().getScheduler().runAsync(wrappedTask -> {
                   if (PluginDataManager.getClanDatabase().isEmpty()) {
                       sender.sendMessage("There is no clans to reset all.");
                       return;
                   }

                   int clanSize = PluginDataManager.getClanDatabase().size();
                   sender.sendMessage("Starting to reset " + args[1] + " of " + clanSize + " clans...");
                   for (String clanName : PluginDataManager.getClanDatabase().keySet()) {
                       if (args[1].equalsIgnoreCase("score"))
                           PluginDataManager.getClanDatabase(clanName).setScore(0);
                       if (args[1].equalsIgnoreCase("warpoint"))
                           PluginDataManager.getClanDatabase(clanName).setWarPoint(0);
                       if (args[1].equalsIgnoreCase("warning"))
                           PluginDataManager.getClanDatabase(clanName).setWarning(0);
                   }
                   sender.sendMessage("Reset successfully " + args[1] + " of " + clanSize + " clans.");
                });
                return false;
            }
            if (args[0].equalsIgnoreCase("event")) {
                try {
                    if (args[1].equalsIgnoreCase("war")) {
                        if (args[2].equalsIgnoreCase("start")) {
                            sender.sendMessage("Starting war event...");
                            sender.sendMessage("Note: The event will not start if it is already running.");
                            EventManager.getWarEvent().runEvent(false);
                            return false;
                        }
                        if (args[2].equalsIgnoreCase("end")) {
                            sender.sendMessage("Ending war event...");
                            sender.sendMessage("Note: The event will not end if it is not currently running.");
                            EventManager.getWarEvent().endEvent(true, true, true);
                            return false;
                        }
                        if (args[2].equalsIgnoreCase("settime")) {
                            try {
                                int newTimeLeft = Integer.parseInt(args[3]);
                                sender.sendMessage("Successfully changed the remaining time of the war event to " + newTimeLeft);
                                sender.sendMessage("Note: The time will not change if the event is not currently running.");

                                if (newTimeLeft < 0)
                                    newTimeLeft = 1;

                                EventManager.getWarEvent().setTimeLeft(newTimeLeft);
                                return false;
                            } catch (Exception exception) {
                                sender.sendMessage("Please enter a valid number of seconds!");
                                return false;
                            }
                        }

                    }
                } catch (ArrayIndexOutOfBoundsException exception) {
                    sender.sendMessage("Please enter a valid value!");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("delete")) {
                String clanName = args[1];
                if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                    sender.sendMessage("Clan " + clanName + " does not exist.");
                    return false;
                } else {
                    if (PluginDataManager.deleteClanData(clanName)) {
                        sender.sendMessage("Clan " + clanName + " deleted successfully.");
                    } else {
                        sender.sendMessage("Error occurred while deleting the clan (Please check console)");
                    }
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("transferplugindatabasetype")) {
                try {
                    DatabaseType databaseType = DatabaseType.valueOf(args[1].toUpperCase());
                    if (databaseType != ClansPlus.databaseType) {
                        PluginDataManager.transferDatabase(sender, databaseType);
                    } else {
                        sender.sendMessage("Cannot transfer to the current database type!");
                    }
                    return false;
                } catch (Exception exception) {
                    sender.sendMessage("Database type " + args[1] + " is not available!");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("backup")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                    builder.append(args[i]).append(" ");
                builder.deleteCharAt(builder.length() - 1);

                String fileName = builder.toString();

                List<String> prohibitedCharacters = new ArrayList<>();
                // these characters cannot be used for file name due to windows's limited
                prohibitedCharacters.add("\\");
                prohibitedCharacters.add("/");
                prohibitedCharacters.add(":");
                prohibitedCharacters.add("*");
                prohibitedCharacters.add("?");
                prohibitedCharacters.add("<");
                prohibitedCharacters.add(">");
                prohibitedCharacters.add("|");
                for (String character : prohibitedCharacters) {
                    if (fileName.contains(character)) {
                        sender.sendMessage(fileName + " cannot contain the character: " + character);
                        return false;
                    }
                }

                sender.sendMessage("Creating backup, please wait...");
                ClansPlus.support.getFoliaLib().getScheduler().runAsync(task -> {
                    PluginDataManager.backupAll(fileName);
                    sender.sendMessage("Backup successful! The backup file will be located in the 'backup' folder inside the plugin directory.");
                    sender.sendMessage("Database type: " + ClansPlus.databaseType.toString().toUpperCase());
                    sender.sendMessage("File name: " + fileName);
                });
                return false;
            }
            if (args[0].equalsIgnoreCase("setPlayerData")) {
                String playerName;
                try {
                    playerName = args[1];
                    if (!PluginDataManager.getPlayerDatabase().containsKey(playerName)) {
                        sender.sendMessage("Databsae of " + playerName + " does not exist!");
                        return false;
                    }
                    PlayerDataType playerDataType;
                    try {
                        playerDataType = PlayerDataType.valueOf(args[2].toLowerCase());
                    } catch (Exception exception) {
                        sender.sendMessage("Type " + (args[2] == null ? "" : args[2] + " ") + "is not available.");
                        return false;
                    }

                    if (playerDataType.equals(PlayerDataType.clanname)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            String clanName;
                            try {
                                clanName = args[4];
                            } catch (Exception exception) {
                                sender.sendMessage("Please type available clan name.");
                                return false;
                            }
                            if (!PluginDataManager.getClanDatabase().containsKey(clanName)) {
                                sender.sendMessage("Clan " + clanName + " does not exist.");
                                return false;
                            }
                            if (ClanManager.isPlayerInClan(playerName)) {
                                if (PluginDataManager.getPlayerDatabase(playerName).getRank() == Rank.LEADER) {
                                    sender.sendMessage(playerName + " is a owner of " + PluginDataManager.getPlayerDatabase(playerName).getClan() + ". Please reset instead of setting!");
                                    return false;
                                }
                            }
                            ClanManager.addPlayerToAClan(playerName, clanName, true);
                            sender.sendMessage("Gave " + playerName + " vào " + clanName + ".");
                            sender.sendMessage("Database of " + clanName + " also added " + playerName + " to members list.");
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("reset")) {
                            if (!ClanManager.isPlayerInClan(playerName)) {
                                sender.sendMessage(playerName + " does not have a clan!");
                                return false;
                            }
                            String playerClanName = PluginDataManager.getPlayerDatabase(playerName).getClan();
                            PluginDataManager.clearPlayerDatabase(playerName);
                            sender.sendMessage("Removed " + playerName + " from members list of clan " + playerClanName);
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.rank)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Rank chosenRank;
                            try {
                                chosenRank = Rank.valueOf(args[4].toUpperCase());
                            } catch (Exception exception) {
                                sender.sendMessage("Rank is not available!");
                                sender.sendMessage("Available ranks:");
                                for (Rank rank : Rank.values())
                                    sender.sendMessage(rank.toString().toUpperCase());
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setRank(chosenRank);
                            sender.sendMessage("Set rank of " + playerName + " to " + chosenRank + ".");
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.joindate)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Long newJoinDate;
                            try {
                                newJoinDate = Long.parseLong(args[4]);
                            } catch (Exception exception) {
                                sender.sendMessage("Date is not available! Vui lòng nhập milliseconds");
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setJoinDate(newJoinDate);
                            sender.sendMessage("Set ngày tham gia of " + playerName + " to " + newJoinDate + " (" + com.cortezromeo.clansplus.util.StringUtil.dateTimeToDateFormat(newJoinDate));
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.scorecollected)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Long newScoreCollected;
                            try {
                                newScoreCollected = Long.parseLong(args[4]);
                            } catch (Exception exception) {
                                sender.sendMessage("Date is not available!");
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setScoreCollected(newScoreCollected);
                            sender.sendMessage("Set score collected from " + playerName + " to " + newScoreCollected);
                            return false;
                        }
                        if (args[3].equalsIgnoreCase("reset")) {
                            PluginDataManager.getPlayerDatabase(playerName).setScoreCollected(0);
                            sender.sendMessage("Reset score collected from " + playerName + ".");
                            return false;
                        }
                    }
                    if (playerDataType.equals(PlayerDataType.lastactivated)) {
                        if (args[3].equalsIgnoreCase("set")) {
                            Long newLastActivated;
                            try {
                                newLastActivated = Long.parseLong(args[4]);
                            } catch (Exception exception) {
                                sender.sendMessage("Date is not available! Please type milliseconds");
                                return false;
                            }
                            PluginDataManager.getPlayerDatabase(playerName).setLastActivated(newLastActivated);
                            sender.sendMessage("Set last activated time for " + playerName + " to " + newLastActivated + " (" + com.cortezromeo.clansplus.util.StringUtil.dateTimeToDateFormat(newLastActivated));
                            return false;
                        }
                    }
                } catch (Exception exception) {
                    sender.sendMessage("Please type player name!");
                    return false;
                }
                return false;
            }
        }

        sender.sendMessage("ClanPlus (Version: " + ClansPlus.plugin.getDescription().getVersion() + ") - Admin");
        sender.sendMessage("Plugin developed by Cortez_Romeo");
        sender.sendMessage("");
        sender.sendMessage("/clanadmin reload");
        sender.sendMessage("/clanadmin chatspy");
        sender.sendMessage("/clanadmin transferPluginDatabaseType <type>");
        sender.sendMessage("/clanadmin backup (custom file name)");
        sender.sendMessage("/clanadmin setClanData <clan name> <type> <give/add/set/remove/reset> <value>");
        sender.sendMessage(ChatColor.AQUA + "Types: score, warpoint, warning, createddate, customname, message, icon, spawnpoint, subjectpermission, discordchannelid, discordjoinlink, members, allies");        sender.sendMessage("/clanadmin setClanSkillData <clan name> <skill id> <skill level>");
        sender.sendMessage("/clanadmin setPlayerData <player name> <type> <set/reset> <value>");
        sender.sendMessage(ChatColor.AQUA + "Types: clanname, rank, joindate, scorecollected, lastactivated");
        sender.sendMessage("/clanadmin clanResetAll <type>");
        sender.sendMessage(ChatColor.AQUA + "Types: score, warpoint, warning");
        sender.sendMessage("/clanadmin event <event> <start/end/settime> <value>");
        sender.sendMessage("/clanadmin delete <clan name>");
        sender.sendMessage("");
        sender.sendMessage("<>: Required");
        sender.sendMessage("(): Optional");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            String commandPermission = "clanplus.admin";
            if (!player.hasPermission(commandPermission)) {
                MessageUtil.sendMessage(player, Messages.PERMISSION_REQUIRED.replace("%permission%", commandPermission));
                return null;
            }
        }

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            // general sub command
            commands.add("reload");
            commands.add("transferPluginDatabaseType");
            commands.add("setClanData");
            commands.add("setClanSkillData");
            commands.add("setPlayerData");
            commands.add("event");
            commands.add("backup");
            commands.add("delete");
            commands.add("chatspy");
            commands.add("clanresetall");

            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            // check clan info -> list all clan name
            if (args[0].equalsIgnoreCase("setclandata") || args[0].equalsIgnoreCase("setClanSkillData") || args[0].equalsIgnoreCase("delete")) {
                if (!PluginDataManager.getClanDatabase().isEmpty()) {
                    commands.addAll(PluginDataManager.getClanDatabase().keySet());
                }
            }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (!PluginDataManager.getPlayerDatabase().isEmpty())
                    commands.addAll(PluginDataManager.getPlayerDatabase().keySet());
            }
            if (args[0].equalsIgnoreCase("clanresetall")) {
                commands.add("score");
                commands.add("warpoint");
                commands.add("warning");
            }
            if (args[0].equalsIgnoreCase("event"))
                commands.add("war");
            if (args[0].equalsIgnoreCase("transferPluginDatabaseType")) {
                if (!transferDataCommandNotifying.contains(sender)) {
                    sender.sendMessage("-----------------------");
                    sender.sendMessage("Note: The current clan database type is: " + ClansPlus.databaseType.toString().toUpperCase());
                    sender.sendMessage("Available database types you can use:");
                    for (DatabaseType databaseType : DatabaseType.values()) {
                        if (databaseType != ClansPlus.databaseType) {
                            sender.sendMessage("- " + databaseType.toString().toUpperCase());
                        }
                    }
                    sender.sendMessage("For data safety during the database type transfer, please perform this while the server has no players online.");
                    sender.sendMessage("-----------------------");
                    transferDataCommandNotifying.add(sender);
                    ClansPlus.support.getFoliaLib().getScheduler().runLater(() -> {
                        transferDataCommandNotifying.remove(sender);
                    }, 20 * 30);
                }
                for (DatabaseType databaseType : DatabaseType.values()) {
                    commands.add(databaseType.toString().toUpperCase());
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    for (ClanDataType clanDataType : ClanDataType.values()) {
                        commands.add(clanDataType.toString().toLowerCase());
                    }
                }
            }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (PluginDataManager.getPlayerDatabase().containsKey(args[1])) {
                    for (PlayerDataType playerDataType : PlayerDataType.values()) {
                        commands.add(playerDataType.toString().toLowerCase());
                    }
                }
            }
            if (args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("war")) {
                commands.add("start");
                commands.add("end");
                commands.add("settime");
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    String type = args[2];
                    commands.add("set");
                    if (!type.equalsIgnoreCase("members") && !type.equalsIgnoreCase("allies")) {
                        commands.add("reset");
                        if (type.equalsIgnoreCase("createddate"))
                            commands.remove("reset");
                    } else {
                        commands.add("add");
                        commands.add("remove");
                        commands.remove("set");
                    }
                    if (type.equalsIgnoreCase("score")
                            || type.equalsIgnoreCase("warpoint")
                            || type.equalsIgnoreCase("warning")) {
                        commands.add("give");
                        commands.add("remove");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (PluginDataManager.getPlayerDatabase().containsKey(args[1])) {
                    String type = args[2];
                    commands.add("set");
                    if (type.equalsIgnoreCase("scorecollected") || type.equalsIgnoreCase("clanname"))
                        commands.add("reset");
                }
            }
            StringUtil.copyPartialMatches(args[3], commands, completions);
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    if (args[2].equalsIgnoreCase("subjectpermission")) {
                        for (Subject subject : Subject.values()) {
                            commands.add(subject.toString().toUpperCase());
                        }
                    }
                    if (args[2].equalsIgnoreCase("icon")) {
                        for (ItemType itemType : ItemType.values()) {
                            commands.add(itemType.toString().toUpperCase());
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("setplayerdata")) {
                if (PluginDataManager.getPlayerDatabase().containsKey(args[1])) {
                    if (args[2].equalsIgnoreCase("rank") && args[3].equalsIgnoreCase("set")) {
                        for (Rank rank : Rank.values()) {
                            commands.add(rank.toString().toUpperCase());
                        }
                    }
                    if (args[2].equalsIgnoreCase("clanname") && args[3].equalsIgnoreCase("set")) {
                        if (!PluginDataManager.getClanDatabase().isEmpty()) {
                            commands.addAll(PluginDataManager.getClanDatabase().keySet());
                        }
                    }
                }
            }
            StringUtil.copyPartialMatches(args[4], commands, completions);
        } else if (args.length == 6) {
            if (args[0].equalsIgnoreCase("setclandata")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    if (args[2].equalsIgnoreCase("subjectpermission")) {
                        for (Rank rank : Rank.values()) {
                            commands.add(rank.toString().toUpperCase());
                        }
                    }
                    if (args[2].equalsIgnoreCase("icon")) {
                        try {
                            ItemType itemType = ItemType.valueOf(args[4].toUpperCase());
                            if (itemType.equals(ItemType.MATERIAL)) {
                                for (Material material : Material.values()) {
                                    if (material == Material.AIR)
                                        continue;
                                    commands.add(material.toString().toUpperCase());
                                }
                            }
                        } catch (Exception exception) {
                            //
                        }
                    }
                }
            }
            StringUtil.copyPartialMatches(args[5], commands, completions);
        }

        Collections.sort(completions);
        return completions;
    }

    private enum ClanDataType {
        score, warpoint, warning, createddate, customname, message, icon, spawnpoint, subjectpermission, discordchannelid, discordjoinlink, members, allies
    }

    private enum PlayerDataType {
        clanname, rank, joindate, scorecollected, lastactivated
    }
}