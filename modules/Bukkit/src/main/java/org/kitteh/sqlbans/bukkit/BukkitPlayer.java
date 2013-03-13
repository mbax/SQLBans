package org.kitteh.sqlbans.bukkit;

import org.bukkit.entity.Player;
import org.kitteh.sqlbans.Perm;

public final class BukkitPlayer implements org.kitteh.sqlbans.api.Player {

    private final Player player;

    public BukkitPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void kick(String reason) {
        this.player.kickPlayer(reason);
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public boolean hasPermission(Perm permission) {
        return this.player.hasPermission(permission.toString());
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }
}