package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.CurrencyType;
import com.cortezromeo.clansplus.clan.ClanManager;
import com.cortezromeo.clansplus.language.Messages;
import com.cortezromeo.clansplus.storage.PluginDataManager;
import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

    public static String dateTimeToDateFormat(long time) {
        if (time == 0)
            return ClansPlus.nms.addColor(Messages.UNKNOWN);
        return new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date(time));
    }

    public static String getCurrencySymbolFormat(CurrencyType currencyType) {
        if (currencyType == CurrencyType.VAULT)
            return Messages.CURRENCY_DISPLAY_VAULT_SYMBOL;
        if (currencyType == CurrencyType.PLAYERPOINTS)
            return Messages.CURRENCY_DISPLAY_PLAYERPOINTS_SYMBOL;
        if (currencyType == CurrencyType.WARPOINT)
            return Messages.CURRENCY_DISPLAY_WARPOINT_SYMBOL;
        return null;
    }

    public static String getCurrencyNameFormat(CurrencyType currencyType) {
        if (currencyType == CurrencyType.VAULT)
            return Messages.CURRENCY_DISPLAY_VAULT_NAME;
        if (currencyType == CurrencyType.PLAYERPOINTS)
            return Messages.CURRENCY_DISPLAY_PLAYERPOINTS_NAME;
        if (currencyType == CurrencyType.WARPOINT)
            return Messages.CURRENCY_DISPLAY_WARPOINT_NAME;
        return null;
    }

    public static String getProgressBar(int current, int max) {
        float percent = (float) current / max;
        int progressBars = (int) (Settings.PROGRESS_BAR_TOTAL_BARS * percent);

        return ClansPlus.nms.addColor(Strings.repeat(Settings.PROGRESS_BAR_SYMBOL_COMPLETED, progressBars) + Strings.repeat(Settings.PROGRESS_BAR_SYMBOL_NOTCOMPLETED, Settings.PROGRESS_BAR_TOTAL_BARS - progressBars) + "&r");
    }

    public static String getStatus(boolean status) {
        if (status)
            return Messages.STATUS_ENABLE;
        else
            return Messages.STATUS_DISABLE;
    }

    public static String setClanNamePlaceholder(String string, String clanName) {
        if (!ClanManager.isClanExisted(clanName))
            return string;

        return string.replace("%formatClanName%", ClanManager.getFormatClanName(PluginDataManager.getClanDatabase(clanName))).replace("%clanName%", clanName);
    }

    public static String getTimeFormat(long seconds, String hhmmss, String mmss, String ss) {
        if (seconds > 3600) {
            hhmmss = hhmmss.replace("%hours%", String.valueOf(seconds / 3600));
            hhmmss = hhmmss.replace("%minutes%", String.valueOf((seconds % 3600) / 60));
            hhmmss = hhmmss.replace("%seconds%", String.valueOf(seconds % 60));
            return ClansPlus.nms.addColor(hhmmss);
        } else if (seconds > 60) {
            mmss = mmss.replace("%minutes%", String.valueOf((seconds / 60)));
            mmss = mmss.replace("%seconds%", String.valueOf(seconds % 60));
            return ClansPlus.nms.addColor(mmss);
        } else
            return ClansPlus.nms.addColor(ss.replace("%seconds%", String.valueOf(seconds)));
    }

}
