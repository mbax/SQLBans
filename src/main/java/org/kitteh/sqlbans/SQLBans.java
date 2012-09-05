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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.sqlbans.commands.BanCommand;
import org.kitteh.sqlbans.commands.KickCommand;
import org.kitteh.sqlbans.commands.ReloadCommand;
import org.kitteh.sqlbans.commands.UnbanCommand;
import org.kitteh.sqlbans.exceptions.SQLBansException;

public class SQLBans extends JavaPlugin implements Listener {
    public static String TABLE_CREATE = null;

    private String banDisconnectMessage;

    private HashSet<String> bannedCache;

    private Object bannedCacheSync;

    @Override
    public void onEnable() {
        this.bannedCache = new HashSet<String>() {
            private static final long serialVersionUID = 1337L;

            @Override
            public boolean add(String string) {
                return super.add(string.toLowerCase());
            }

            @Override
            public boolean remove(Object object) {
                if (object instanceof String) {
                    return remove(((String) object).toLowerCase());
                } else {
                    return remove(object);
                }
            }

            @Override
            public boolean contains(Object object) {
                if (object instanceof String) {
                    return contains(((String) object).toLowerCase());
                } else {
                    return contains(object);
                }
            }
        };
        this.bannedCacheSync = new Object();
        final File confFile = new File(this.getDataFolder(), "config.yml");
        if (!confFile.exists()) {
            this.saveDefaultConfig();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getResource("create.sql")));
        StringBuilder builder = new StringBuilder();
        String next;
        try {
            while ((next = reader.readLine()) != null) {
                builder.append(next);
            }
        } catch (IOException e) {
            new SQLBansException("Could not load default table creation text", e).printStackTrace();
        }
        TABLE_CREATE = builder.toString();

        // Command registration
        this.getCommand("ban").setExecutor(new BanCommand(this));
        this.getCommand("kick").setExecutor(new KickCommand(this));
        this.getCommand("sqlbansreload").setExecutor(new ReloadCommand(this));
        this.getCommand("unban").setExecutor(new UnbanCommand(this));

        this.getServer().getPluginManager().registerEvents(this, this);

        this.initializeHandler();
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

    public void initializeHandler() {
        this.reloadConfig();
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

}