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

import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans.Messages;

/**
 * A command
 */
public abstract class SQLBansCommand {

    private final String[] aliases;
    private final String name;
    private final Perm permission;

    public SQLBansCommand(String commandName, Perm permission, String... aliases) {
        this.name = commandName;
        this.permission = permission;
        this.aliases = aliases;
    }

    /**
     * Get potential aliases of the command
     * Can be null/empty for no aliases
     * 
     * @return an array of potential aliases
     */
    public String[] getAliases() {
        return this.aliases;
    }

    /**
     * Get the primary name of the command
     * 
     * @return the primary name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the permission required to call the command
     * Can be null if no permission required
     * 
     * @return the permission required
     */
    public Perm getPermission() {
        return this.permission;
    }

    /**
     * Process the command
     * 
     * @param sender
     *            Command sender
     * @param args
     *            Arguments sent in the command
     * @return true if processed
     */
    public boolean processCommand(CommandSender sender, String[] args) {
        if ((this.permission != null) && !sender.hasPermission(this.permission)) {
            sender.sendMessage(Messages.getCommandNoPermission());
            return true;
        }
        return this.onCommand(sender, args);
    }

    protected abstract boolean onCommand(CommandSender sender, String[] args);
}