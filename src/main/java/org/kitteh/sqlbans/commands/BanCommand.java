/*
 * SQLBans
 * Copyright 2012 Matt Baxter
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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.SQLHandler;
import org.kitteh.sqlbans.Util;

public class BanCommand implements CommandExecutor {

    private final SQLBans plugin;

    public BanCommand(SQLBans plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String targetName = args[0];
        String reasonbit;
        final String reason;
        final StringBuilder disconnect = new StringBuilder();
        disconnect.append(ChatColor.RED).append("Banned");

        if (args.length > 1) {
            reason = Util.separatistsUnite(args, " ", 1);
            reasonbit = ChatColor.WHITE + ": " + reason;
        } else {
            reasonbit = ".";
            reason = "Banned";
        }
        disconnect.append(reasonbit);

        final Player target = this.plugin.getServer().getPlayer(targetName);
        boolean online = false;
        if ((target != null) && target.isOnline()) {
            targetName = target.getName();
            online = true;
            target.kickPlayer(disconnect.toString());
        }

        final String banMessage = ChatColor.RED + "Banned " + targetName + reasonbit;
        final String banAdminMessage = ChatColor.RED + sender.getName() + " banned " + targetName + reasonbit;
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && player.isOnline()) {
                if (online || Perm.MESSAGE_BAN_OFFLINE.has(player)) {
                    if (Perm.MESSAGE_BAN_ADMIN.has(player)) {
                        player.sendMessage(banAdminMessage);
                    } else if (Perm.MESSAGE_BAN_NORMAL.has(player)) {
                        player.sendMessage(banMessage);
                    }
                }
            }
        }

        this.plugin.getServer().getConsoleSender().sendMessage(banAdminMessage);

        final String admin = sender.getName();
        final String username = targetName;
        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
                try {
                    SQLHandler.ban(username, reason, admin);
                } catch (final Exception e) {
                    BanCommand.this.plugin.getLogger().log(Level.SEVERE, "Could not ban " + username, e);
                    Util.queueMessage(BanCommand.this.plugin, sender, ChatColor.RED + "[SQLBans] Failed to ban " + username);
                }
            }
        });
        return true;
    }

}
