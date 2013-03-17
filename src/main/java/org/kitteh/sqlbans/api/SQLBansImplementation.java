/*
 * SQLBans
 * Copyright 2012 Matt Baxter
 *
 * Google Gson
 * Copyright 2008-2011 Google Inc.
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

import java.io.File;
import java.util.logging.Logger;

import org.kitteh.sqlbans.Perm;

public interface SQLBansImplementation {

    /**
     * Get this implementation's Scheduler
     * 
     * @return
     */
    public Scheduler getScheduler();

    /**
     * Called on plugin disable (due to initial SQL failure, or on reload)
     * It is advisable for implementations to implement this method to un-hook any events or commands
     */
    public void shutdown();

    /**
     * Send all players with a specified permission node a message
     * 
     * @param permission
     *            Permission to check
     * @param message
     *            Message to send
     */
    public void sendMessage(final Perm permission, final String message);

    /**
     * Handle implementation-side registration of commands
     * 
     * @param command
     *            Command to register
     */
    public void registerCommand(SQLBansCommand command);

    /**
     * Called when the implementation should start listening for login attempts
     */
    public void registerLoginAttemptListening();

    /**
     * Get the implementation's Logger
     * 
     * @return a Logger to use
     */
    public Logger getLogger();

    /**
     * Get the data folder for saving files
     * 
     * @return the implementation's data folder
     */
    public File getDataFolder();

    /**
     * Get the current SQLBans version, as stored in the implementation's files
     * 
     * @return the current SQLBans version
     */
    public String getVersion();

    /**
     * Get a Player by name
     * 
     * @param name
     *            Name of the player
     * @return a Player or null if not found
     */
    public Player getPlayer(String name);

    /**
     * Get the currently online Players
     * 
     * @return array of Player currently online
     */
    public Player[] getOnlinePlayers();
}