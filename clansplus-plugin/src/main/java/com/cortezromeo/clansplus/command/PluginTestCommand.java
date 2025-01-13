package com.cortezromeo.clansplus.command;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.DatabaseType;
import com.cortezromeo.clansplus.api.enums.Rank;
import com.cortezromeo.clansplus.api.enums.Subject;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.api.storage.IPlayerData;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.inventory.UpgradePluginSkillInventory;
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
                    IPlayerData playerData = PluginDataManager.getPlayerDatabase(player.getName());
                    MessageUtil.devMessage(player, "name: &e" + playerData.getPlayerName());
                    MessageUtil.devMessage(player, "uuid: &e" + playerData.getUUID());
                    MessageUtil.devMessage(player, "clan: &e" + playerData.getClan());
                    MessageUtil.devMessage(player, "rank: &e" + playerData.getRank());
                    MessageUtil.devMessage(player, "join date: &e" + playerData.getJoinDate());
                    MessageUtil.devMessage(player, "score collected: &e" + playerData.getScoreCollected());
                    MessageUtil.devMessage(player, "clan: &e" + playerData.getClan());
                    MessageUtil.devMessage(player, "--------");
                    IClanData clanData = PluginDataManager.getClanDatabase(playerData.getClan());
                    MessageUtil.devMessage(player, "clan name: &e" + clanData.getName());
                    MessageUtil.devMessage(player, "clan custom name: &e" + clanData.getCustomName());
                    MessageUtil.devMessage(player, "clan owner: &e" + clanData.getOwner());
                    MessageUtil.devMessage(player, "clan score: &e" + clanData.getScore());
                    MessageUtil.devMessage(player, "clan warpoint: &e" + clanData.getWarPoint());
                    MessageUtil.devMessage(player, "clan warning: &e" + clanData.getWarning());
                    MessageUtil.devMessage(player, "clan max member: &e" + clanData.getMaxMembers());
                    MessageUtil.devMessage(player, "clan created date: &e" + clanData.getCreatedDate());
                    MessageUtil.devMessage(player, "clan icon type: &e" + clanData.getIconType());
                    MessageUtil.devMessage(player, "clan icon value: &e" + clanData.getIconValue());
                    MessageUtil.devMessage(player, "clan members: &e" + clanData.getMembers());
                    if (clanData.getSpawnPoint() != null) {
                        MessageUtil.devMessage(player, "clan spawn point world: &e" + clanData.getSpawnPoint().getWorld().getName());
                        MessageUtil.devMessage(player, "clan spawn point x: &e" + clanData.getSpawnPoint().getX());
                        MessageUtil.devMessage(player, "clan spawn point y: &e" + clanData.getSpawnPoint().getY());
                        MessageUtil.devMessage(player, "clan spawn point z: &e" + clanData.getSpawnPoint().getZ());
                    } else
                        MessageUtil.devMessage(player, "clan spawn point is null!");
                    if (clanData.getSubjectPermission() != null) {
                        for (Subject subject : clanData.getSubjectPermission().keySet()) {
                            MessageUtil.devMessage(player, subject + ": &e" + clanData.getSubjectPermission().get(subject));
                        }
                    } else {
                        MessageUtil.devMessage(player, "clan subject permission is null!");
                    }
                    MessageUtil.devMessage(player, "clan allies: &e" + clanData.getAllies());
                    MessageUtil.devMessage(player, "clan skill level: &e" + clanData.getSkillLevel());
                }
            }
            if (args[0].equalsIgnoreCase("bemanager")) {
                PluginDataManager.getPlayerDatabase(player.getName()).setRank(Rank.MANAGER);
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("transfer")) {
                try {
                    DatabaseType databaseType = DatabaseType.valueOf(args[1].toUpperCase());
                    if (databaseType != ClansPlus.databaseType) {
                        PluginDataManager.transferDatabase(sender, databaseType);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("upgrade"))
                new UpgradePluginSkillInventory(player, PluginDataManager.getClanDatabaseByPlayerName(player.getName()), PluginSkill.valueOf(args[1].toUpperCase())).open();
        }

        return false;
    }
}
