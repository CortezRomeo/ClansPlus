package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.storage.ClanData;
import com.cortezromeo.clansplus.storage.PlayerData;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PluginTestCommand implements CommandExecutor {

    public PluginTestCommand() {
        ClansPlus.plugin.getCommand("clantest").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {
                if (PluginDataManager.getPlayerDatabase(player.getName()).getClan() != null) {
                    PlayerData playerData = PluginDataManager.getPlayerDatabase(player.getName());
                    MessageUtil.devMessage(player, "name: &e" + playerData.getPlayerName());
                    MessageUtil.devMessage(player, "uuid: &e" + playerData.getUUID());
                    MessageUtil.devMessage(player, "clan: &e" + playerData.getClan());
                    MessageUtil.devMessage(player, "rank: &e" + playerData.getRank());
                    MessageUtil.devMessage(player, "join date: &e" + playerData.getJoinDate());
                    MessageUtil.devMessage(player, "score collected: &e" + playerData.getScoreCollected());
                    MessageUtil.devMessage(player, "clan: &e" + playerData.getClan());
                    MessageUtil.devMessage(player, "--------");
                    ClanData clanData = PluginDataManager.getClanDatabase(playerData.getClan());
                    MessageUtil.devMessage(player, "clan name: &e" + clanData.getName());
                    MessageUtil.devMessage(player, "clan custom name: &e" + clanData.getCustomName());
                    MessageUtil.devMessage(player, "clan owner: &e" + clanData.getOwner());
                    MessageUtil.devMessage(player, "clan score: &e" + clanData.getScore());
                    MessageUtil.devMessage(player, "clan warpoint: &e" + clanData.getWarPoint());
                    MessageUtil.devMessage(player, "clan warning: &e" + clanData.getWarning());
                    MessageUtil.devMessage(player, "clan max member: &e" + clanData.getMaxMember());
                    MessageUtil.devMessage(player, "clan created date: &e" + clanData.getCreatedDate());
                    MessageUtil.devMessage(player, "clan icon type: &e" + clanData.getIconType());
                    MessageUtil.devMessage(player, "clan icon value: &e" + clanData.getIconValue());
                    MessageUtil.devMessage(player, "clan members: &e" + clanData.getMembers());
                    if (clanData.getSpawnPoint() != null) {
                        MessageUtil.devMessage(player, "clan spawn point world: &e" + clanData.getSpawnPoint().getWorld().getName());
                        MessageUtil.devMessage(player, "clan spawn point x: &e" + clanData.getSpawnPoint().getX());
                        MessageUtil.devMessage(player, "clan spawn point y: &e" + clanData.getSpawnPoint().getY());
                        MessageUtil.devMessage(player, "clan spawn point z: &e" + clanData.getSpawnPoint().getZ());
                    }
                    MessageUtil.devMessage(player, "clan allies: &e" + clanData.getAllies());
                    MessageUtil.devMessage(player, "clan skill level: &e" + clanData.getSkillLevel());
                }
            }
        }

        return false;
    }
}
