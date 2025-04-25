package com.cortezromeo.clansplus.listener;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.clan.EventManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class EntityDamageListener implements Listener {

    public EntityDamageListener() {
        Bukkit.getPluginManager().registerEvents(this, ClansPlus.plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER || event.getEntity().getType() != EntityType.PLAYER)
            return;

        // Check clan PVP --
        Player damager = (Player) event.getDamager();
        Player entity = (Player) event.getEntity();

        if (!ClanManager.isPlayerInClan(damager) || !ClanManager.isPlayerInClan(entity))
            return;

        EventManager.getWarEvent().onDamage(event);

        int checkEvent = 0;

        String victimClanName = PluginDataManager.getPlayerDatabase(entity.getName()).getClan();
        if (PluginDataManager.getPlayerDatabase(damager.getName()).getClan().equals(victimClanName)) {
            if (!ClanManager.getPlayerTogglingPvP().contains(damager))
                checkEvent = 1;
            if (!ClanManager.getPlayerTogglingPvP().contains(entity))
                checkEvent = 2;
        } else {
            // check if entity's clan is an ally of damager's clan
            List<String> damagerClanAllies = PluginDataManager.getClanDatabaseByPlayerName(damager.getName()).getAllies();
            if (damagerClanAllies.isEmpty())
                return;
            if (damagerClanAllies.contains(victimClanName)) {
                if (!ClanManager.getPlayerTogglingPvP().contains(damager))
                    checkEvent = 1;
                if (!ClanManager.getPlayerTogglingPvP().contains(entity))
                    checkEvent = 2;
            }
        }

        if (checkEvent > 0) {
            event.setCancelled(true);
            if (checkEvent == 1) {
                MessageUtil.sendMessage(damager, Messages.CLAN_MEMBER_PVP_DENY);
                return;
            }
            MessageUtil.sendMessage(damager, Messages.CLAN_MEMBER_PVP_DENY_VICTIM.replace("%player%", entity.getName()));
        }
        // --
    }

    @EventHandler
    public void onArrow(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (event.getEntity() == null || damager == null)
            return;

        if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                Damageable damageableVictim = (Damageable) event.getEntity();
                if (damageableVictim instanceof Player victim) {

                    if (!ClanManager.isPlayerInClan(shooter) || !ClanManager.isPlayerInClan(victim))
                        return;

                    int checkEvent = 0;

                    String victimClanName = PluginDataManager.getPlayerDatabase(victim.getName()).getClan();
                    if (PluginDataManager.getPlayerDatabase(shooter.getName()).getClan().equals(victimClanName)) {
                        if (!ClanManager.getPlayerTogglingPvP().contains(shooter))
                            checkEvent = 1;
                        if (!ClanManager.getPlayerTogglingPvP().contains(victim))
                            checkEvent = 2;
                    } else {
                        // check if entity's clan is an ally of damager's clan
                        List<String> damagerClanAllies = PluginDataManager.getClanDatabaseByPlayerName(shooter.getName()).getAllies();
                        if (damagerClanAllies.isEmpty())
                            return;
                        if (damagerClanAllies.contains(victimClanName)) {
                            if (!ClanManager.getPlayerTogglingPvP().contains(shooter))
                                checkEvent = 1;
                            if (!ClanManager.getPlayerTogglingPvP().contains(victim))
                                checkEvent = 2;
                        }
                    }

                    if (checkEvent > 0) {
                        event.setCancelled(true);
                        if (checkEvent == 1) {
                            MessageUtil.sendMessage(shooter, Messages.CLAN_MEMBER_PVP_DENY);
                            return;
                        }
                        MessageUtil.sendMessage(shooter, Messages.CLAN_MEMBER_PVP_DENY_VICTIM.replace("%player%", victim.getName()));
                    }
                }
            }
        }
    }

}
