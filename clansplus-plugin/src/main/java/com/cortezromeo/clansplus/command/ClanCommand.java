package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.clan.subject.*;
import com.cortezromeo.clansplus.inventory.ClanListInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanCommand implements CommandExecutor {

    public ClanCommand() {
        ClansPlus.plugin.getCommand("clan").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player))
            return false;

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
            if (args[0].equalsIgnoreCase("disband")) {
                new Disband(Settings.CLAN_SETTING_PERMISSION_DEFAULT.get(Subject.DISBAND), player, player.getName()).execute();
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

        return false;
    }
}
