package com.cortezromeo.clansplus.util;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.api.enums.CurrencyType;
import com.cortezromeo.clansplus.language.Messages;
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

}
