package com.cortezromeo.clansplus.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

    public static String dateTimeToDateFormat(long time) {
        return new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date(time));
    }

}
