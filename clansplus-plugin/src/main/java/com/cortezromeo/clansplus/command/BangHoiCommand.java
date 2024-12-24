package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.subject.Create;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BangHoiCommand implements CommandExecutor {

    public BangHoiCommand() {
        ClansPlus.plugin.getCommand("banghoi").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                new Create(player, player.getName(), args[1]).execute();
            }
        }

        return false;
    }
}
