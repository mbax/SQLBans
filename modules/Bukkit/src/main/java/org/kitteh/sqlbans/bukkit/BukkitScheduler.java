package org.kitteh.sqlbans.bukkit;

import org.kitteh.sqlbans.api.Scheduler;

public class BukkitScheduler implements Scheduler {

    private final SQLBansPlugin plugin;

    public BukkitScheduler(SQLBansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void delayedTask(Runnable runnable, int delay) {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, runnable, delay);
    }

    @Override
    public void repeatingTask(Runnable runnable, int delay, int period) {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, delay, period);
    }

    @Override
    public void run(Runnable runnable) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }
}