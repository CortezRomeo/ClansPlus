package com.cortezromeo.clansplus.clan;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.api.enums.CurrencyType;
import com.cortezromeo.clansplus.api.storage.IClanData;
import com.cortezromeo.clansplus.clan.skill.PluginSkill;
import com.cortezromeo.clansplus.file.UpgradeFile;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.cortezromeo.clansplus.util.MessageUtil;
import com.cortezromeo.clansplus.util.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UpgradeManager {

    public static CurrencyType getSkillCurrencyType(PluginSkill pluginSkill) {
        FileConfiguration upgradeFilConfig = UpgradeFile.get();
        return CurrencyType.valueOf(upgradeFilConfig.getString("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".currency-type").toUpperCase());
    }

    public static long getSkillCost(PluginSkill pluginSkill, int level) {
        FileConfiguration upgradeFilConfig = UpgradeFile.get();
        return upgradeFilConfig.getLong("upgrade.plugin-skills." + pluginSkill.toString().toLowerCase() + ".price." + level);
    }

    public static boolean checkPlayerCurrency(Player player, CurrencyType currencyType, long value, boolean take) {
        if (currencyType == CurrencyType.WARPOINT) {
            if (PluginDataManager.getClanDatabaseByPlayerName(player.getName()) == null) {
                MessageUtil.sendMessage(player, Messages.MUST_BE_IN_CLAN);
                return false;
            }
            IClanData playerClanData = PluginDataManager.getClanDatabaseByPlayerName(player.getName());
            if (playerClanData.getWarPoint() >= value) {
                if (take) {
                    playerClanData.setWarPoint(playerClanData.getWarPoint() - value);
                    PluginDataManager.saveClanDatabaseToStorage(playerClanData.getName(), playerClanData);
                }
                return true;
            }
        }
        if (currencyType == CurrencyType.VAULT) {
            if (ClansPlus.support.getVault() == null) {
                MessageUtil.throwErrorMessage("THE SERVER DOES NOT HAVE THE VAULT PLUGIN TO PERFORM THE ACTION, PLEASE CHECK AGAIN");
                player.sendMessage("Error: Vault plugin is missing, please contact the server admin immediately");
                return false;
            }
            if (ClansPlus.support.getVault().getBalance(player) >= value) {
                if (take)
                    ClansPlus.support.getVault().withdrawPlayer(player, value);
                return true;
            }
        }
        if (currencyType == CurrencyType.PLAYERPOINTS) {
            if (ClansPlus.support.getPlayerPointsAPI() == null) {
                MessageUtil.throwErrorMessage("THE SERVER DOES NOT HAVE THE PLAYERPOINTS PLUGIN TO PERFORM THE ACTION, PLEASE CHECK AGAIN");
                player.sendMessage("Error: PlayerPoints plugin is missing, please contact the server admin immediately");
                return false;
            }
            if (ClansPlus.support.getPlayerPointsAPI().look(player.getUniqueId()) >= value) {
                if (take)
                    ClansPlus.support.getPlayerPointsAPI().take(player.getUniqueId(), (int) value);
                return true;
            }
        }
        MessageUtil.sendMessage(player, Messages.NOT_ENOUGH_CURRENCY.replace("%currencySymbol%", StringUtil.getCurrencySymbolFormat(currencyType)).replace("%price%", String.valueOf(value)).replace("%currencyName%", StringUtil.getCurrencyNameFormat(currencyType)));
        return false;
    }

}
