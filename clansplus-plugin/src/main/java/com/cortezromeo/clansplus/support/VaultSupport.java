package com.cortezromeo.clansplus.support;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {

    public static boolean setup() {
        RegisteredServiceProvider<Economy> rsp = ClansPlus.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        ClansPlus.vaultEconomy = rsp.getProvider();
        return true;
    }

    public static Economy getEcon() {
        if (ClansPlus.vaultEconomy == null)
            if (!setup()) {
                MessageUtil.throwErrorMessage("KHÔNG THỂ TÌM THẤY PLUGIN VAULT");
                return null;
            }
        return ClansPlus.vaultEconomy;
    }
}
