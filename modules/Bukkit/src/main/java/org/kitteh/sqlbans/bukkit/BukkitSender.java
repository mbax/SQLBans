package org.kitteh.sqlbans.bukkit;

import org.bukkit.command.CommandSender;
import org.kitteh.sqlbans.Perm;

public class BukkitSender implements org.kitteh.sqlbans.api.CommandSender {

    private final CommandSender sender;

    public BukkitSender(CommandSender sender) {
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