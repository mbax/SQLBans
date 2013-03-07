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

import java.util.logging.Level;

import org.kitteh.sqlbans.ChatColor;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.SQLHandler;
import org.kitteh.sqlbans.Util;
import org.kitteh.sqlbans.api.CommandSender;
import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansCommand;

public class BanCommand extends SQLBansCommand {

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

        final int type = Util.isIP(targetName) ? 1 : 0;
        boolean online = false;
        if (type == 0) {
            final Player target = this.plugin.getPlayer(targetName);
            if ((target != null)) {
                targetName = target.getName();
                online = true;
                target.kick(SQLBans.Messages.getDisconnectBanned(reason, sender.getName()));
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

        final String admin = sender.getName();
        final String info = targetName;
        this.plugin.getScheduler().run(new Runnable() {
            @Override
            public void run() {
                try {
                    SQLHandler.ban(info, reason, admin, type);
                } catch (final Exception e) {
                    BanCommand.this.plugin.getLogger().log(Level.SEVERE, "Could not ban " + info, e);
                    BanCommand.this.plugin.sendMessage(Perm.MESSAGE_BAN_ADMIN, ChatColor.RED + "[SQLBans] Failed to ban " + info);
                }
            }
        });
        return true;
    }

}
