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
package org.kitteh.sqlbans.api;

/**
 * Scheduler for task scheduling
 * Task scheduling does not need to run on any particular thread
 * It is preferable that scheduled tasks run on a thread that will not tie up operation of the server
 * All time values are measured in seconds
 */
public interface Scheduler {

    /**
     * Schedule a task to run after a specified delay
     *
     * @param runnable Runnable to execute
     * @param delay Seconds to delay execution
     */
    void delayedTask(Runnable runnable, int delay);

    /**
     * Schedule a task to run after a specified delay and then repeat on a specified period
     *
     * @param runnable Runnable to execute
     * @param delay Seconds to delay first execution
     * @param period Seconds to delay between subsequent executions
     */
    void repeatingTask(Runnable runnable, int delay, int period);

    /**
     * Schedule a task to run immediately
     *
     * @param runnable Runnable to execute
     */
    void run(Runnable runnable);
}