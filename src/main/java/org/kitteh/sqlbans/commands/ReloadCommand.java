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
package org.kitteh.sqlbans.commands;

import org.kitteh.sqlbans.ChatColor;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.SQLHandler;
import org.kitteh.sqlbans.api.CommandSender;
import org.kitteh.sqlbans.api.SQLBansCommand;
import org.kitteh.sqlbans.exceptions.SQLBansException;

public class ReloadCommand extends SQLBansCommand {

    private final SQLBans plugin;

    public ReloadCommand(SQLBans plugin) {
        super("sqlbansreload", Perm.COMMAND_RELOAD);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        SQLHandler.nullifyInstance();
        try {
            this.plugin.load();
            this.plugin.sendMessage(Perm.MESSAGE_RELOAD, ChatColor.AQUA.toString() + sender.getName() + " reloaded SQLBans");
            this.plugin.getLogger().info(sender.getName() + " reloaded SQLBans");
        } catch (final SQLBansException e) {
            this.plugin.sendMessage(Perm.MESSAGE_RELOAD, ChatColor.AQUA.toString() + sender.getName() + " reloaded SQLBans, " + ChatColor.RED + "which failed to load.");
            this.plugin.getLogger().info(sender.getName() + " reloaded SQLBans, which failed to load.");
        }
        return true;
    }
}