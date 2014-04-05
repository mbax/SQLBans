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

import org.kitteh.sqlbans.BanType;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.Util;
import org.kitteh.sqlbans.api.CommandSender;
import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansCommand;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class BanCommand extends SQLBansCommand {

    private final SQLBans plugin;

    public BanCommand(SQLBans plugin) {
        super("ban", Perm.COMMAND_BAN, "b");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String targetName = args[0];
        final String reason = args.length > 1 ? Util.separatistsUnite(args, " ", 1) : "Banned";

        final BanType type = Util.isIP(targetName) ? BanType.IP : BanType.NAME;
        boolean online = false;
        InetAddress ipaddress = null;
        if (type == BanType.NAME) {
            final Player target = this.plugin.getPlayer(targetName);
            if ((target != null)) {
                targetName = target.getName();
                online = true;
                target.kick(SQLBans.Messages.getDisconnectBanned(reason, sender.getName()));
            }
        } else {
            try {
                ipaddress = InetAddress.getByName(targetName);
            } catch (final UnknownHostException e) {
            }
        }

        final String banMessage = SQLBans.Messages.getIngameBanned(targetName, reason, sender.getName(), false);
        final String banAdminMessage = SQLBans.Messages.getIngameBanned(targetName, reason, sender.getName(), true);
        for (final Player player : this.plugin.getOnlinePlayers()) {
            if ((player != null)) {
                if (online || player.hasPermission(Perm.MESSAGE_BAN_OFFLINE)) {
                    if (player.hasPermission(Perm.MESSAGE_BAN_ADMIN)) {
                        player.sendMessage(banAdminMessage);
                    } else if (player.hasPermission(Perm.MESSAGE_BAN_NORMAL)) {
                        player.sendMessage(banMessage);
                    }
                }
            }
        }
        this.plugin.getLogger().info(banAdminMessage);

        if (type == BanType.NAME) {
            this.plugin.banName(targetName, reason, sender.getName());
        } else {
            this.plugin.banIP(ipaddress, reason, sender.getName());
        }
        return true;
    }
}