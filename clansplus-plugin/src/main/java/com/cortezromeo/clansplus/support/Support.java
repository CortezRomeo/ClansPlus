package com.cortezromeo.clansplus.support;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.tcoded.folialib.FoliaLib;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Support {

    public PlayerPointsAPI playerPointsAPI;
    public Economy vaultEconomyAPI;
    public DiscordSupport discordSupport;
    public FoliaLib foliaLib;
    public boolean placeholderAPISupported = false;
    public boolean playerPointsSupported = false;
    public boolean mythicMobsSupported = false;
    public boolean vaultSupported = false;

    public boolean isPlaceholderAPISupported() {
        return placeholderAPISupported;
    }

    public boolean isPlayerPointsSupported() {
        return playerPointsSupported;
    }

    public boolean isMythicMobsSupported() {
        return mythicMobsSupported;
    }

    public boolean isVaultSupported() {
        return vaultSupported;
    }

    public PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    public DiscordSupport getDiscordSupport() {
        return discordSupport;
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public boolean isFoliaLibSupported() {
        return foliaLib.isFolia();
    }

    public void setupSupports() {
        // Vault
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupVault();
        }

        // PlayerPoints
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            playerPointsAPI = PlayerPoints.getInstance().getAPI();
            playerPointsSupported = true;
        }

        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPISupport().register();
            placeholderAPISupported = true;
        }

        // MythicMobs
        if (Bukkit.getServer().getPluginManager().getPlugin("MythicMobs") != null) mythicMobsSupported = true;

        // discordWebhook
        discordSupport = new DiscordSupport(Settings.SOFT_DEPEND_DISCORDWEBHOOK_URL);

        // FoliaLib
        foliaLib = new FoliaLib(ClansPlus.plugin);
        foliaLib.enableInvalidTickValueDebug();
    }

    public boolean setupVault() {
        if (ClansPlus.plugin.getServer().getPluginManager().getPlugin("Vault") == null) return false;

        RegisteredServiceProvider<Economy> rsp = ClansPlus.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEconomyAPI = rsp.getProvider();
        vaultSupported = true;
        return true;
    }

    public Economy getVault() {
        if (vaultEconomyAPI == null) if (!setupVault()) {
            MessageUtil.throwErrorMessage("KHÔNG THỂ TÌM THẤY PLUGIN VAULT");
            return null;
        }
        return vaultEconomyAPI;
    }

}
