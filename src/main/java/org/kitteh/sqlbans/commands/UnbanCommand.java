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
package org.kitteh.sqlbans.commands;

import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.SQLBans.Messages;
import org.kitteh.sqlbans.Util;
import org.kitteh.sqlbans.api.CommandSender;
import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansCommand;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class UnbanCommand extends SQLBansCommand {

    private final SQLBans plugin;

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
            this.plugin.unbanIP(ipaddress);
        } else {
            this.plugin.unbanName(targetName);
        }
        return true;
    }
}