package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.subject.*;
import com.cortezromeo.clansplus.inventory.ClanListInventory;
import com.cortezromeo.clansplus.inventory.ClanMenuInventory;
import com.cortezromeo.clansplus.inventory.NoClanInventory;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClanCommand implements CommandExecutor {

    public static List<CommandSender> commandConfirmation = new ArrayList<>();

    public ClanCommand() {
        ClansPlus.plugin.getCommand("clan").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player))
            return false;

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
            if (args[0].equalsIgnoreCase("reject")) {
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

                    Bukkit.getScheduler().runTaskLaterAsynchronously(ClansPlus.plugin, () -> {
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
            if (args[0].equalsIgnoreCase("list")) {
                new ClanListInventory(player).open();
                return false;
            }
        }

        if (args.length == 2) {
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
        for (Subject subject : playerClanData.getSubjectPermission().keySet()) {
            Rank subjectRequiredRank = playerClanData.getSubjectPermission().get(subject);
            if (subjectRequiredRank == Rank.MEMBER) {
                String commandPlaceholder = Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MEMBERCOMMANDS_PLACEHOLDER_COMMAND;
                commandPlaceholder = commandPlaceholder.replace("%command%", subject.toString().toLowerCase());
                commandPlaceholder = commandPlaceholder.replace("%description%", subject.getDescription());
                memberCommands.append(commandPlaceholder).append("\n");
            }
            if (subjectRequiredRank == Rank.MANAGER || playerData.getRank() == Rank.LEADER) {
                String commandMessage = Messages.COMMAND_CLANPLUS_MESSAGES_IN_CLAN_PLACEHOLDER_MANAGERCOMMANDS_PLACEHOLDER_COMMAND;
                commandMessage = commandMessage.replace("%command%", subject.toString().toLowerCase());
                commandMessage = commandMessage.replace("%description%", subject.getDescription());
                managerCommands.append(commandMessage).append("\n");
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
}
