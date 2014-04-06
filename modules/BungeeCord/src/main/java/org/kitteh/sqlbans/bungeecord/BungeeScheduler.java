/*
 * SQLBans
 * Copyright 2012-2014 Matt Baxter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kitteh.sqlbans.bungeecord;

import org.kitteh.sqlbans.api.Scheduler;

import java.util.Timer;
import java.util.TimerTask;

final class BungeeScheduler implements Scheduler {

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