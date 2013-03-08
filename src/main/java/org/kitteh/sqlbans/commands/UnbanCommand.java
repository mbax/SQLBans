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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.kitteh.sqlbans.ChatColor;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.SQLBans.Messages;
import org.kitteh.sqlbans.SQLHandler;
import org.kitteh.sqlbans.Util;
import org.kitteh.sqlbans.api.CommandSender;
import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansCommand;

public class UnbanCommand extends SQLBansCommand {

    public SQLBans plugin;

    public UnbanCommand(SQLBans plugin) {
        super("unban", Perm.COMMAND_UNBAN);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }
        final String targetName = args[0];

        final String unbanMessage = Messages.getIngameUnbanned(targetName, sender.getName(), false);
        final String unbanAdminMessage = Messages.getIngameUnbanned(targetName, sender.getName(), true);
        for (final Player player : this.plugin.getOnlinePlayers()) {
            if ((player != null)) {
                if (player.hasPermission(Perm.MESSAGE_UNBAN_ADMIN)) {
                    player.sendMessage(unbanAdminMessage);
                } else if (player.hasPermission(Perm.MESSAGE_UNBAN_NORMAL)) {
                    player.sendMessage(unbanMessage);
                }
            }
        }
        this.plugin.getLogger().info(unbanAdminMessage);

        InetAddress ipaddress = null;
        if (Util.isIP(targetName)) {
            try {
                ipaddress = InetAddress.getByName(targetName);
            } catch (final UnknownHostException e) {
            }
        }
        if (ipaddress != null) {
            this.plugin.getBanCache().removeIP(ipaddress);
        } else {
            this.plugin.getBanCache().removeName(targetName);
        }
        final InetAddress address = ipaddress;
        final String name = address != null ? address.getHostAddress() : targetName;
        this.plugin.getScheduler().run(new Runnable() {
            @Override
            public void run() {
                try {
                    if (address != null) {
                        SQLHandler.unban(address);
                    } else {
                        SQLHandler.unban(name);
                    }
                } catch (final Exception e) {
                    UnbanCommand.this.plugin.getLogger().log(Level.SEVERE, "Could not unban " + name, e);
                    UnbanCommand.this.plugin.sendMessage(Perm.MESSAGE_UNBAN_ADMIN, ChatColor.RED + "[SQLBans] Failed to unban " + name);
                }
            }
        });
        return true;
    }
}