package org.kitteh.sqlbans.bungeecord;

import java.io.File;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import org.kitteh.sqlbans.JoinAttempt;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansCommand;
import org.kitteh.sqlbans.api.SQLBansImplementation;
import org.kitteh.sqlbans.api.Scheduler;

import com.google.common.eventbus.Subscribe;

public class SQLBansPlugin extends Plugin implements Listener, SQLBansImplementation {

    private SQLBans sqlbans;
    private final Scheduler scheduler = new BungeeScheduler();
    private final File pluginDir = new File("plugins/SQLBans");

    @Override
    public File getDataFolder() {
        return this.pluginDir;
    }

    @Override
    public Logger getLogger() {
        return ProxyServer.getInstance().getLogger();
    }

    @Override
    public Player[] getOnlinePlayers() {
        final ProxiedPlayer[] bplayers = ProxyServer.getInstance().getPlayers().toArray(new ProxiedPlayer[0]);
        final Player[] players = new Player[bplayers.length];
        for (int x = 0; x < players.length; x++) {
            players[x] = new BungeePlayer(bplayers[x]);
        }
        return players;
    }

    @Override
    public Player getPlayer(String name) {
        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if (player == null) {
            return null;
        }
        return new BungeePlayer(player);
    }

    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public void onEnable() {
        this.sqlbans = new SQLBans(this);
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        final JoinAttempt attempt = new JoinAttempt(event.getConnection().getName(), event.getConnection().getAddress().getAddress().getHostAddress());
        this.sqlbans.onJoinAttempt(attempt);
        if (attempt.getResult() != JoinAttempt.Result.UNCHANGED) {
            event.setCancelled(true);
            event.setCancelReason(attempt.getReason());
        }
    }

    @Override
    public void registerCommand(String string, SQLBansCommand command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(new BungeeCommand(command));
    }

    @Override
    public void registerLoginAttemptListening() {
        ProxyServer.getInstance().getPluginManager().registerListener(this);
    }

    @Override
    public void sendMessage(Perm permission, String message) {
        for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission(permission.toString())) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void shutdown() {
        // Boggle!
    }
}