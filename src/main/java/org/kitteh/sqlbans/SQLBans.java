/*
 * SQLBans
 * Copyright 2012 Matt Baxter
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
package org.kitteh.sqlbans;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.sqlbans.exceptions.SQLBansException;

public class SQLBans extends JavaPlugin {
    private String banDisconnectMessage;

    private HashSet<String> bannedCache;

    private Object bannedCacheSync;

    @Override
    public void onEnable() {
        this.bannedCache = new HashSet<String>();
        this.bannedCacheSync = new Object();
        final File confFile = new File(this.getDataFolder(), "config.yml");
        if (!confFile.exists()) {
            this.saveDefaultConfig();
        }
        final FileConfiguration config = this.getConfig();
        this.banDisconnectMessage = config.getString("disconnect.banned");
        final String host = config.getString("database.host");
        final int port = config.getInt("database.port");
        final String db = config.getString("database.database");
        final String user = config.getString("database.auth.username");
        final String pass = config.getString("database.auth.password");
        final String tableName = config.getString("database.tablename");
        try {
            SQLHandler.start(host, port, user, pass, db, tableName);
        } catch (final SQLBansException e) {
            this.getLogger().log(Level.SEVERE, "Failure to load, shutting down", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        synchronized (this.bannedCacheSync) {
            if (this.bannedCache.contains(event.getName())) {
                event.disallow(Result.KICK_BANNED, this.banDisconnectMessage);
                return;
            }
        }
        try {
            if (!SQLHandler.canJoin(event.getName())) {
                event.disallow(Result.KICK_BANNED, this.banDisconnectMessage);
                synchronized (this.bannedCacheSync) {
                    final String name = event.getName();
                    this.bannedCache.add(name);
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        public void run() {
                            synchronized (SQLBans.this.bannedCacheSync) {
                                SQLBans.this.bannedCache.remove(name);
                            }
                        }
                    }, 1200);
                }
            }
        } catch (final Exception e) {
            event.disallow(Result.KICK_OTHER, "Connection error: Please retry.");
            this.getLogger().log(Level.SEVERE, "Severe error on user connect", e);
        }
    }

}