package com.cortezromeo.clansplus.clan;

import com.cortezromeo.clansplus.clan.event.WarEvent;

public class EventManager {

    private static WarEvent warEvent;

    public static WarEvent getWarEvent() {
        if (warEvent == null)
            warEvent = new WarEvent();
        return warEvent;
    }
}
