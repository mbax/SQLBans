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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.Util;

public class KickCommand implements CommandExecutor {

    private final SQLBans plugin;

    public KickCommand(SQLBans plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        final Player target = this.plugin.getServer().getPlayer(args[0]);

        if ((target == null) || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Could not find a player named " + args[0]);
            return true;
        }

        final String reason = args.length > 1 ? Util.separatistsUnite(args, " ", 1) : "Kicked";

        final String kick = SQLBans.Messages.getIngameKicked(target.getName(), reason, sender.getName(), false);
        final String kickAdmin = SQLBans.Messages.getIngameKicked(target.getName(), reason, sender.getName(), true);
        target.kickPlayer(SQLBans.Messages.getDisconnectKicked(reason, sender.getName()));
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && player.isOnline()) {
                if (Perm.MESSAGE_KICK_ADMIN.has(player)) {
                    player.sendMessage(kickAdmin);
                } else if (Perm.MESSAGE_KICK_NORMAL.has(player)) {
                    player.sendMessage(kick);
                }
            }
        }
        this.plugin.getServer().getConsoleSender().sendMessage(kickAdmin);
        return true;
    }

}
