package com.cortezromeo.clansplus.task;

import com.cortezromeo.clansplus.ClansPlus;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class EventTask implements Runnable {

    private BukkitTask eventTask;

    public EventTask() {
        this.eventTask = Bukkit.getScheduler().runTaskTimerAsynchronously(ClansPlus.plugin, this, 0, 20L);
    }

    @Override
    public void run() {

    }

    public BukkitTask getEventTask() {
        return eventTask;
    }

    public int getEventTaskID() {
        return eventTask.getTaskId();
    }
}
