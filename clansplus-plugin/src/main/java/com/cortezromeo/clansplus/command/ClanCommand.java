package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.IconType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.clan.subject.*;
import com.cortezromeo.clansplus.inventory.*;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
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

public class ClanCommand implements CommandExecutor, TabExecutor {

    public static List<CommandSender> commandConfirmation = new ArrayList<>();

    public ClanCommand() {
        ClansPlus.plugin.getCommand("clan").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, Messages.NON_CONSOLE_COMMAND);
            return false;
        }

        if (args.length == 0) {
            if (PluginDataManager.getPlayerDatabase(player.getName()).getClan() == null) {
                new NoClanInventory(player).open();
                return false;
            } else {
                new ClanMenuInventory(player).open();
                return false;
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                new Accept(player, player.getName()).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("reject") || args[0].equalsIgnoreCase("deny")) {
                new Reject(player, player.getName()).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                new Leave(player, player.getName()).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                new Spawn(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SPAWN), player, player.getName()).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("disband")) {
                if (!commandConfirmation.contains(sender)) {
                    commandConfirmation.add(sender);
                    MessageUtil.sendMessage(player, Messages.COMMAND_CONFIRMATION);

                    ClansPlus.support.getFoliaLib().getScheduler().runLaterAsync(() -> {
                        if (commandConfirmation.contains(sender))
                            commandConfirmation.remove(sender);
                    }, 20 * 10);
                    return false;
                } else {
                    new Disband(Rank.LEADER, player, player.getName()).execute();
                    commandConfirmation.remove(sender);
                }
                return false;
            }
            if (args[0].equalsIgnoreCase("setspawn")) {
                new SetSpawn(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETSPAWN), player, player.getName()).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("list")) {
                new ClanListInventory(player).open();
                return false;
            }
            if (args[0].equalsIgnoreCase("seticon")) {
                new SetIconMenuInventory(player).open();
                return false;
            }
            if (args[0].equalsIgnoreCase("setpermission")) {
                new SetPermissionInventory(player).open();
                return false;
            }
            if (args[0].equalsIgnoreCase("setting")) {
                new ClanSettingsInventory(player).open();
                return false;
            }
            if (args[0].equalsIgnoreCase("upgrade")) {
                new UpgradeMenuInventory(player).open();
                return false;
            }
            if (args[0].equalsIgnoreCase("event")) {
                new EventsMenuInventory(player).open();
                return false;
            }
            if (args[0].equalsIgnoreCase("menu")) {
                if (PluginDataManager.getPlayerDatabase(player.getName()).getClan() == null) {
                    new NoClanInventory(player).open();
                    return false;
                } else {
                    new ClanMenuInventory(player).open();
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("chat")) {
                if (PluginDataManager.getPlayerDatabase(player.getName()).getClan() == null) {
                    MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
                    return false;
                } else {
                    if (!ClanManager.getPlayerUsingClanChat().contains(player)) {
                        MessageUtil.sendMessage(player, Messages.TOGGLE_CLAN_CHAT_ON);
                        ClanManager.getPlayerUsingClanChat().add(player);
                    } else {
                        MessageUtil.sendMessage(player, Messages.TOGGLE_CLAN_CHAT_OFF);
                        ClanManager.getPlayerUsingClanChat().remove(player);
                    }
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("pvp")) {
                if (PluginDataManager.getPlayerDatabase(player.getName()).getClan() == null) {
                    MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
                    return false;
                } else {
                    if (!ClanManager.getPlayerTogglingPvP().contains(player)) {
                        MessageUtil.sendMessage(player, Messages.TOGGLE_CLAN_PVP_ON);
                        ClanManager.getPlayerTogglingPvP().add(player);
                    } else {
                        MessageUtil.sendMessage(player, Messages.TOGGLE_CLAN_PVP_OFF);
                        ClanManager.getPlayerTogglingPvP().remove(player);
                    }
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("info")) {
                if (ClanManager.isPlayerInClan(player)) {
                    new ViewClanInformationInventory(player, PluginDataManager.getPlayerDatabase(player.getName()).getClan()).open();
                    return false;
                } else {
                    MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
                    return false;
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                if (PluginDataManager.getClanDatabase().containsKey(args[1])) {
                    new ViewClanInformationInventory(player, args[1]).open();
                    return false;
                } else {
                    MessageUtil.sendMessage(player, Messages.CLAN_DOES_NOT_EXIST.replace("%clan%", args[1]));
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("create")) {
                new Create(player, player.getName(), args[1]).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("invite")) {
                new Invite(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.INVITE), player, player.getName(), Bukkit.getPlayer(args[1]), args[1], Settings.CLAN_SETTING_TIME_TO_ACCEPT).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("kick")) {
                new Kick(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.KICK), player, player.getName(), Bukkit.getPlayer(args[1]), args[1]).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("setowner")) {
                new SetOwner(Rank.LEADER, player, player.getName(), Bukkit.getPlayer(args[1]), args[1]).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("setmanager")) {
                new SetManager(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETMANAGER), player, player.getName(), Bukkit.getPlayer(args[1]), args[1]).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("removemanager")) {
                new RemoveManager(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.REMOVEMANAGER), player, player.getName(), Bukkit.getPlayer(args[1]), args[1]).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("requestally")) {
                new RequestAlly(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.MANAGEALLY), player, player.getName(), args[1]).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("event") && args[1].equalsIgnoreCase("war")) {
                EventManager.getWarEvent().sendEventStatusMessage(player, false);
                return false;
            }
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("setcustomname")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                    builder.append(args[i]).append(" ");
                builder.deleteCharAt(builder.length() - 1);

                String customName = builder.toString();
                new SetCustomName(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETCUSTOMNAME), player, player.getName(), customName).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("setmessage")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                    builder.append(args[i]).append(" ");
                builder.deleteCharAt(builder.length() - 1);

                String clanMessage = builder.toString();
                new SetMessage(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETMESSAGE), player, player.getName(), clanMessage).execute();
                return false;
            }
            if (args[0].equalsIgnoreCase("chat")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                    builder.append(args[i]).append(" ");
                builder.deleteCharAt(builder.length() - 1);

                String message = builder.toString();
                new Chat(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.CHAT), player, player.getName(), message).execute();
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("seticon")) {
            IconType iconType;
            try {
                iconType = IconType.valueOf(args[1].toUpperCase());
            } catch (Exception exception) {
                MessageUtil.sendMessage(player, Messages.INVALID_ICON_TYPE);
                return false;
            }

            new SetIcon(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.SETICON), player, player.getName(), iconType, args[2]).execute();
            return false;
        }

        IPlayerData playerData = PluginDataManager.getPlayerDatabase(player.getName());
        if (!ClanManager.isPlayerInClan(player)) {
            for (String nonClanMessage : Messages.COMMAND_CLANPLUS_MESSAGES_NON_CLAN) {
                nonClanMessage = nonClanMessage.replace("%version%", ClansPlus.plugin.getDescription().getVersion());
                player.sendMessage(ClansPlus.nms.addColor(nonClanMessage));
            }
            return false;
        }

        String inClanMessage = Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN;
        StringBuilder memberCommands = new StringBuilder();
        StringBuilder managerCommands = new StringBuilder();
        StringBuilder leaderCommands = new StringBuilder();

        IClanData playerClanData = PluginDataManager.getClanDatabase(playerData.getClan());
        for (Subject subject : getPlayerClanSubjectPer(playerClanData).keySet()) {
            Rank subjectRequiredRank = getPlayerClanSubjectPer(playerClanData).get(subject);
            if (subjectRequiredRank == Rank.MEMBER) {
                String commandPlaceholder = Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS_PLACEHOLDER_COMMAND;
                commandPlaceholder = commandPlaceholder.replace("%command%", subject.toString().toLowerCase());
                commandPlaceholder = commandPlaceholder.replace("%description%", subject.getDescription());
                memberCommands.append(commandPlaceholder).append("\n");
            }
            if (subjectRequiredRank == Rank.MANAGER) {
                String commandMessage = Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS_PLACEHOLDER_COMMAND;
                commandMessage = commandMessage.replace("%command%", subject.toString().toLowerCase());
                commandMessage = commandMessage.replace("%description%", subject.getDescription());
                managerCommands.append(commandMessage).append("\na");

                // also add this to leader commands list because the player is leader
                if (playerData.getRank() == Rank.LEADER)
                    subjectRequiredRank = Rank.LEADER;
            }
            if (subjectRequiredRank == Rank.LEADER) {
                String commandMessage = Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS_PLACEHOLDER_COMMAND;
                commandMessage = commandMessage.replace("%command%", subject.toString().toLowerCase());
                commandMessage = commandMessage.replace("%description%", subject.getDescription());
                leaderCommands.append(commandMessage).append("\n");
            }
        }

        inClanMessage = inClanMessage.replace("%memberCommands%", Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS.replace("%command%", memberCommands.toString()));
        inClanMessage = inClanMessage.replace("%managerCommands%", (playerData.getRank() == Rank.MANAGER ? Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS.replace("%command%", managerCommands.toString()) : ""));
        inClanMessage = inClanMessage.replace("%leaderCommands%", (playerData.getRank() == Rank.LEADER ? Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_LEADERCOMMANDS.replace("%command%", leaderCommands.toString()) : ""));
        inClanMessage = inClanMessage.replace("%version%", ClansPlus.plugin.getDescription().getVersion());
        player.sendMessage(ClansPlus.nms.addColor(inClanMessage));

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        Player player = (Player) sender;
        String playerName = player.getName();

        IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(player.getName());
        IPlayerData playerData = PluginDataManager.getPlayerDatabase(player.getName());

        if (args.length == 1) {
            // general sub command
            commands.add("info");
            commands.add("list");
            commands.add("event");

            // player is in a clan -> list all commands available
            if (playerClanData != null) {
                for (Subject subject : getPlayerClanSubjectPer(playerClanData).keySet()) {
                    if (ClanManager.isPlayerRankSatisfied(playerName, getPlayerClanSubjectPer(playerClanData).get(subject)))
                        commands.add(subject.toString().toLowerCase());
                }
                if (playerData.getRank() == Rank.LEADER) {
                    commands.add("disband");
                    commands.add("setowner");
                    commands.add("setpermission");
                } else {
                    commands.add("leave");
                }
                commands.add("setting");
                commands.add("upgrade");
                commands.add("menu");
                // player is not in a clan -> list all commands for non clan player
            } else {
                commands.add("create");
                commands.add("accept");
                commands.add("deny");
            }

            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            // check clan info -> list all clan name
            if (args[0].equalsIgnoreCase("info")) {
                if (!PluginDataManager.getClanDatabase().isEmpty()) {
                    commands.addAll(PluginDataManager.getClanDatabase().keySet());
                }
            }
            if (args[0].equalsIgnoreCase("event")) {
                commands.add("war");
            }

            // all the commands below should be for the player who is in a clan
            if (playerClanData != null) {
                HashMap<Subject, Rank> clanSubjectPer = getPlayerClanSubjectPer(playerClanData);

                if (args[0].equalsIgnoreCase("invite") && ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.INVITE))) {
                    // list all players not in a clan
                    for (Player serverPlayer : Bukkit.getOnlinePlayers()) {
                        String serverPlayerName = serverPlayer.getName();

                        if (serverPlayerName.equalsIgnoreCase(playerName))
                            continue;

                        // server player is already in a clan -> skip
                        if (PluginDataManager.getClanDatabaseByPlayerName(serverPlayerName) != null)
                            continue;

                        commands.add(serverPlayerName);
                    }
                }

                if (args[0].equalsIgnoreCase("kick") && ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.KICK))) {
                    // list all members in the player's clan
                    for (String memberName : playerClanData.getMembers()) {
                        if (memberName.equalsIgnoreCase(playerName))
                            continue;

                        if (memberName.equalsIgnoreCase(playerClanData.getOwner()))
                            continue;

                        commands.add(memberName);
                    }
                }

                if (args[0].equalsIgnoreCase("removeally") && ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.MANAGEALLY))) {
                    commands.addAll(playerClanData.getAllies());
                }

                if (args[0].equalsIgnoreCase("requestally") && ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.MANAGEALLY))) {
                    if (!PluginDataManager.getClanDatabase().isEmpty()) {
                        for (String serverClan : PluginDataManager.getClanDatabase().keySet()) {
                            if (serverClan.equalsIgnoreCase(playerClanData.getName()))
                                continue;

                            if (playerClanData.getAllies().contains(serverClan))
                                continue;

                            commands.add(serverClan);
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("setmanager") && ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.SETMANAGER))) {
                    // list all members in the player's clan
                    for (String memberName : playerClanData.getMembers()) {
                        if (memberName.equalsIgnoreCase(playerName))
                            continue;

                        if (memberName.equalsIgnoreCase(playerClanData.getOwner()))
                            continue;

                        if (PluginDataManager.getPlayerDatabase(memberName).getRank().equals(Rank.MANAGER))
                            continue;

                        commands.add(memberName);
                    }
                }

                if (args[0].equalsIgnoreCase("removemanager") && ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.REMOVEMANAGER))) {
                    // list all members in the player's clan
                    for (String memberName : playerClanData.getMembers()) {
                        if (PluginDataManager.getPlayerDatabase(memberName).getRank().equals(Rank.MANAGER))
                            commands.add(memberName);
                    }
                }

                if (args[0].equalsIgnoreCase("seticon") && ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.SETICON))) {
                    for (IconType iconType : IconType.values())
                        commands.add(iconType.toString().toUpperCase());
                }

                if (args[0].equalsIgnoreCase("setowner") && ClanManager.isPlayerRankSatisfied(playerName, Rank.LEADER)) {
                    // list all members in the player's clan
                    for (String memberName : playerClanData.getMembers()) {
                        if (memberName.equalsIgnoreCase(playerClanData.getOwner()))
                            continue;
                        commands.add(memberName);
                    }
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            if (playerClanData != null) {
                HashMap<Subject, Rank> clanSubjectPer = getPlayerClanSubjectPer(playerClanData);

                if (ClanManager.isPlayerRankSatisfied(playerName, clanSubjectPer.get(Subject.SETICON))) {
                    if (args[0].equalsIgnoreCase("seticon") && args[1].equalsIgnoreCase("MATERIAL")) {
                        for (Material material : Material.values()) {
                            if (material == Material.AIR)
                                continue;
                            commands.add(material.toString().toUpperCase());
                        }
                    }
                }
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }

        Collections.sort(completions);
        return completions;
    }

    private HashMap<Subject, Rank> getPlayerClanSubjectPer(IClanData clanData) {
        if (Settings.CLAN_SETTING_PERMISSION_DEFAULT_FORCED)
            return Settings.CLAN_SETTING_PERMISSION_DEFAULT;
        else
            return clanData.getSubjectPermission();
    }
}
