package org.kitteh.sqlbans.bukkit;

import org.bukkit.entity.Player;

public class BukkitPlayer extends BukkitSender implements org.kitteh.sqlbans.api.Player {

    private final Player player;

    public BukkitPlayer(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public void kick(String reason) {
        this.player.kickPlayer(reason);
    }
}