package com.cortezromeo.clansplus.task;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.EventManager;
import com.tcoded.folialib.wrapper.task.WrappedTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventTask implements Runnable {

    private WrappedTask eventTask;

    public EventTask() {
        this.eventTask = ClansPlus.support.getFoliaLib().getScheduler().runTimer(this, 1, 20L);
    }

    @Override
    public void run() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String strDate = dateFormat.format(date);

        // war event
        List<String> warEventTimeFrame = EventManager.getWarEvent().EVENT_TIME_FRAME;
        for (String timeFrame : warEventTimeFrame) {
            if (strDate.equals(timeFrame) && !EventManager.getWarEvent().isStarting()) {
                EventManager.getWarEvent().runEvent(true);
            }
        }
    }

    public WrappedTask getEventTask() {
        return eventTask;
    }

}
