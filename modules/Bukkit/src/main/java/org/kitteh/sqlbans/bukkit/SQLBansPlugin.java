/*
 * SQLBans
 * Copyright 2012-2014 Matt Baxter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kitteh.sqlbans.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.SQLBans;
import org.kitteh.sqlbans.SQLBansUserData;
import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansCommand;
import org.kitteh.sqlbans.api.SQLBansImplementation;
import org.kitteh.sqlbans.api.Scheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SQLBansPlugin extends JavaPlugin implements SQLBansImplementation, Listener {

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
        final Set<SQLBansUserData> attempts = new HashSet<>();
        for (final org.bukkit.entity.Player player : this.getServer().getOnlinePlayers()) {
            final SQLBansUserData attempt = new SQLBansUserData(player.getName(), player.getUniqueId(), player.getAddress().getAddress());
            attempts.add(attempt);
        }
        if (!attempts.isEmpty()) {
            this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    final Map<String, String> kick = new HashMap<>();
                    for (final SQLBansUserData attempt : attempts) {
                        SQLBansPlugin.this.sqlbans.processUserData(attempt, false);
                        if (attempt.getResult() != SQLBansUserData.Result.UNCHANGED) {
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
        final SQLBansUserData data = new SQLBansUserData(event.getName(), event.getUniqueId(), event.getAddress());
        this.sqlbans.processUserData(data, true);
        if (data.getResult() != SQLBansUserData.Result.UNCHANGED) {
            AsyncPlayerPreLoginEvent.Result result;
            switch (data.getResult()) {
                case KICK_BANNED:
                    result = AsyncPlayerPreLoginEvent.Result.KICK_BANNED;
                    break;
                default:
                    result = AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
            }
            event.disallow(result, data.getReason());
        }
    }

    @Override
    public void registerCommand(SQLBansCommand command) {
        this.getCommand(command.getName()).setExecutor(new BukkitCommand(command));
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