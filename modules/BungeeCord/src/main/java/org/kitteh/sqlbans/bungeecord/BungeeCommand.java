package org.kitteh.sqlbans.bungeecord;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import org.kitteh.sqlbans.api.SQLBansCommand;

public final class BungeeCommand extends Command {

    private final SQLBansCommand command;

    public BungeeCommand(SQLBansCommand command) {
        super(command.getName(), command.getPermission().toString(), command.getAliases());
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.command.processCommand(new BungeeSender(sender), args);
    }
}