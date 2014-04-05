package org.kitteh.sqlbans.bungeecord;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.api.Player;

public final class BungeePlayer implements Player {

    private final ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player) {
        this.player = player;
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
    public void kick(String reason) {
        this.player.disconnect(reason);
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }
}