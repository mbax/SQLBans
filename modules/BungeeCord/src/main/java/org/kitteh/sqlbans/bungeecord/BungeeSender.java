package org.kitteh.sqlbans.bungeecord;

import net.md_5.bungee.api.CommandSender;

import org.kitteh.sqlbans.Perm;

public final class BungeeSender implements org.kitteh.sqlbans.api.CommandSender {

    private final CommandSender sender;

    public BungeeSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return this.sender.getName();
    }

    @Override
    public boolean hasPermission(Perm permission) {
        return this.sender.hasPermission(permission.toString());
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }
}