package org.kitteh.sqlbans.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kitteh.sqlbans.api.SQLBansCommand;

public final class BukkitCommand implements CommandExecutor {

    private final SQLBansCommand command;

    public BukkitCommand(SQLBansCommand command) {
        this.command = command;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return this.command.processCommand(new BukkitSender(sender), args);
    }
}