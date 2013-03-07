package org.kitteh.sqlbans.bungeecord;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.kitteh.sqlbans.api.Player;

public class BungeePlayer extends BungeeSender implements Player {

    ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public void kick(String reason) {
        this.player.disconnect(reason);
    }
}