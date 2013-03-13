package org.kitteh.sqlbans.bungeecord;

import java.util.Timer;
import java.util.TimerTask;

import org.kitteh.sqlbans.api.Scheduler;

public final class BungeeScheduler implements Scheduler {

    private final Timer timer = new Timer();

    @Override
    public void delayedTask(final Runnable runnable, int delay) {
        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }

        }, delay * 1000);
    }

    @Override
    public void repeatingTask(final Runnable runnable, int delay, int period) {
        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }
        }, delay * 1000, period * 1000);
    }

    @Override
    public void run(final Runnable runnable) {
        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }

        }, 1);
    }
}