package com.cortezromeo.clansplus.task;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.clan.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventTask implements Runnable {

    private BukkitTask eventTask;

    public EventTask() {
        this.eventTask = Bukkit.getScheduler().runTaskTimer(ClansPlus.plugin, this, 0, 20L);
    }

    @Override
    public void run() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String strDate = dateFormat.format(date);

        List<String> warEventTimeFrame = EventManager.getWarEvent().EVENT_TIME_FRAME;
        for (String timeFrame : warEventTimeFrame) {
            if (strDate.equals(timeFrame) && !EventManager.getWarEvent().isStarting()) {
                EventManager.getWarEvent().runEvent(true);
            }
        }
    }

    public BukkitTask getEventTask() {
        return eventTask;
    }

    public int getEventTaskID() {
        return eventTask.getTaskId();
    }
}
