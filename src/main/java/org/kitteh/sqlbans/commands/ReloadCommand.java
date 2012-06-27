package org.kitteh.sqlbans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.SQLHandler;

public class ReloadCommand implements CommandExecutor {

    private final SQLBans plugin;

    public ReloadCommand(SQLBans plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player != null && Perm.MESSAGE_RELOAD.has(player)) {
                player.sendMessage(ChatColor.AQUA + sender.getName() + " reloaded SQLBans");
            }
        }
        SQLHandler.nullifyInstance();
        this.plugin.initializeHandler();
        return true;
    }

}
