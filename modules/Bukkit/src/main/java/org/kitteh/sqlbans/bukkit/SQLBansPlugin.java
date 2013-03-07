package org.kitteh.sqlbans.bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.sqlbans.JoinAttempt;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansCommand;
import org.kitteh.sqlbans.api.SQLBansImplementation;
import org.kitteh.sqlbans.api.Scheduler;

public class SQLBansPlugin extends JavaPlugin implements SQLBansImplementation, Listener {

    private final Scheduler scheduler = new BukkitScheduler(this);
    private SQLBans sqlbans;

    @Override
    public Player[] getOnlinePlayers() {
        final org.bukkit.entity.Player[] bplayers = this.getServer().getOnlinePlayers();
        final Player[] players = new Player[bplayers.length];
        for (int x = 0; x < players.length; x++) {
            players[x] = new BukkitPlayer(bplayers[x]);
        }
        return players;
    }

    @Override
    public Player getPlayer(String name) {
        final org.bukkit.entity.Player player = this.getServer().getPlayer(name);
        return player == null ? null : new BukkitPlayer(player);
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
        final Set<JoinAttempt> attempts = new HashSet<JoinAttempt>();
        for (final org.bukkit.entity.Player player : this.getServer().getOnlinePlayers()) {
            final JoinAttempt attempt = new JoinAttempt(player.getName(), player.getAddress().getAddress().getHostAddress());
            attempts.add(attempt);
        }
        if (!attempts.isEmpty()) {
            this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    final Map<String, String> kick = new HashMap<String, String>();
                    for (final JoinAttempt attempt : attempts) {
                        SQLBansPlugin.this.sqlbans.onJoinAttempt(attempt);
                        if (attempt.getResult() != JoinAttempt.Result.UNCHANGED) {
                            kick.put(attempt.getName(), attempt.getReason());
                        }
                    }
                    if (!kick.isEmpty()) {
                        SQLBansPlugin.this.getServer().getScheduler().runTask(SQLBansPlugin.this, new Runnable() {
                            @Override
                            public void run() {
                                for (final Map.Entry<String, String> entry : kick.entrySet()) {
                                    final org.bukkit.entity.Player player = SQLBansPlugin.this.getServer().getPlayerExact(entry.getKey());
                                    if (player != null) {
                                        player.kickPlayer(entry.getValue());
                                        SQLBansPlugin.this.getLogger().info("Kicked player " + player.getName());
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        final JoinAttempt attempt = new JoinAttempt(event.getName(), event.getAddress().getHostAddress());
        this.sqlbans.onJoinAttempt(attempt);
        if (attempt.getResult() != JoinAttempt.Result.UNCHANGED) {
            AsyncPlayerPreLoginEvent.Result result;
            switch (attempt.getResult()) {
                case KICK_BANNED:
                    result = AsyncPlayerPreLoginEvent.Result.KICK_BANNED;
                    break;
                default:
                    result = AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
            }
            event.disallow(result, attempt.getReason());
        }
    }

    @Override
    public void registerCommand(String string, SQLBansCommand command) {
        this.getCommand(string).setExecutor(new BukkitCommand(command));
    }

    @Override
    public void registerLoginAttemptListening() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void sendMessage(final Perm permission, final String message) {
        this.getServer().getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                SQLBansPlugin.this.getServer().broadcast(message, permission.toString());
            }
        });
    }

    @Override
    public void shutdown() {
        this.getServer().getPluginManager().disablePlugin(this);
    }
}