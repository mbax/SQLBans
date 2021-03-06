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
package org.kitteh.sqlbans;

import org.kitteh.sqlbans.api.Player;
import org.kitteh.sqlbans.api.SQLBansImplementation;
import org.kitteh.sqlbans.api.Scheduler;
import org.kitteh.sqlbans.api.UserData;
import org.kitteh.sqlbans.commands.BanCommand;
import org.kitteh.sqlbans.commands.KickCommand;
import org.kitteh.sqlbans.commands.ReloadCommand;
import org.kitteh.sqlbans.commands.UnbanCommand;
import org.kitteh.sqlbans.exceptions.SQLBansException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SQLBans {

    public final static class Messages {

        private static String COMMAND_NO_PERMISSION;
        private static String DISCONNECT_REJECTED;
        private static String DISCONNECT_KICKED_NOREASON;
        private static String DISCONNECT_KICKED_REASON;
        private static String DISCONNECT_BANNED_NOREASON;
        private static String DISCONNECT_BANNED_REASON;
        private static String INGAME_KICKED_NORMAL_NOREASON;
        private static String INGAME_KICKED_NORMAL_REASON;
        private static String INGAME_KICKED_ADMIN_NOREASON;
        private static String INGAME_KICKED_ADMIN_REASON;
        private static String INGAME_BANNED_NORMAL_NOREASON;
        private static String INGAME_BANNED_NORMAL_REASON;
        private static String INGAME_BANNED_ADMIN_NOREASON;
        private static String INGAME_BANNED_ADMIN_REASON;
        private static String INGAME_UNBANNED_NORMAL;
        private static String INGAME_UNBANNED_ADMIN;

        public static String getCommandNoPermission() {
            return Messages.COMMAND_NO_PERMISSION;
        }

        public static String getDisconnectBanned(String reason, String admin) {
            final String ret = reason == null ? Messages.DISCONNECT_BANNED_NOREASON : Messages.DISCONNECT_BANNED_REASON.replace("%reason%", reason);
            return ret.replace("%admin%", admin == null ? "Admin" : admin);
        }

        public static String getDisconnectKicked(String reason, String admin) {
            final String ret = reason == null ? Messages.DISCONNECT_KICKED_NOREASON : Messages.DISCONNECT_KICKED_REASON.replace("%reason%", reason);
            return ret.replace("%admin%", admin == null ? "Admin" : admin);
        }

        public static String getDisconnectRejected() {
            return Messages.DISCONNECT_REJECTED;
        }

        public static String getIngameBanned(String target, String reason, String admin, boolean adminmsg) {
            String ret;
            if (adminmsg) {
                ret = reason == null ? Messages.INGAME_BANNED_ADMIN_NOREASON : Messages.INGAME_BANNED_ADMIN_REASON.replace("%reason%", reason);
            } else {
                ret = reason == null ? Messages.INGAME_BANNED_NORMAL_NOREASON : Messages.INGAME_BANNED_NORMAL_REASON.replace("%reason%", reason);
            }
            return ret.replace("%admin%", admin == null ? "Admin" : admin).replace("%target%", target == null ? "Target" : target);
        }

        public static String getIngameKicked(String target, String reason, String admin, boolean adminmsg) {
            String ret;
            if (adminmsg) {
                ret = reason == null ? Messages.INGAME_KICKED_ADMIN_NOREASON : Messages.INGAME_KICKED_ADMIN_REASON.replace("%reason%", reason);
            } else {
                ret = reason == null ? Messages.INGAME_KICKED_NORMAL_NOREASON : Messages.INGAME_KICKED_NORMAL_REASON.replace("%reason%", reason);
            }
            return ret.replace("%admin%", admin == null ? "Admin" : admin).replace("%target%", target == null ? "Target" : target);
        }

        public static String getIngameUnbanned(String target, String admin, boolean adminmsg) {
            final String ret = adminmsg ? Messages.INGAME_UNBANNED_ADMIN : Messages.INGAME_UNBANNED_NORMAL;
            return ret.replace("%admin%", admin == null ? "Admin" : admin).replace("%target%", target == null ? "Target" : target);
        }

        public static void load(Config config) {
            Messages.COMMAND_NO_PERMISSION = Messages.color(config.getString("messages.command.nopermission"));
            Messages.DISCONNECT_REJECTED = Messages.color(config.getString("messages.disconnect.rejected"));
            Messages.DISCONNECT_KICKED_NOREASON = Messages.color(config.getString("messages.disconnect.kicked.noreason"));
            Messages.DISCONNECT_KICKED_REASON = Messages.color(config.getString("messages.disconnect.kicked.reason"));
            Messages.DISCONNECT_BANNED_NOREASON = Messages.color(config.getString("messages.disconnect.banned.noreason"));
            Messages.DISCONNECT_BANNED_REASON = Messages.color(config.getString("messages.disconnect.banned.reason"));
            Messages.INGAME_KICKED_NORMAL_NOREASON = Messages.color(config.getString("messages.ingame.kicked.normal.noreason"));
            Messages.INGAME_KICKED_NORMAL_REASON = Messages.color(config.getString("messages.ingame.kicked.normal.reason"));
            Messages.INGAME_KICKED_ADMIN_NOREASON = Messages.color(config.getString("messages.ingame.kicked.admin.noreason"));
            Messages.INGAME_KICKED_ADMIN_REASON = Messages.color(config.getString("messages.ingame.kicked.admin.reason"));
            Messages.INGAME_BANNED_NORMAL_NOREASON = Messages.color(config.getString("messages.ingame.banned.normal.noreason"));
            Messages.INGAME_BANNED_NORMAL_REASON = Messages.color(config.getString("messages.ingame.banned.normal.reason"));
            Messages.INGAME_BANNED_ADMIN_NOREASON = Messages.color(config.getString("messages.ingame.banned.admin.noreason"));
            Messages.INGAME_BANNED_ADMIN_REASON = Messages.color(config.getString("messages.ingame.banned.admin.reason"));
            Messages.INGAME_UNBANNED_NORMAL = Messages.color(config.getString("messages.ingame.unbanned.normal"));
            Messages.INGAME_UNBANNED_ADMIN = Messages.color(config.getString("messages.ingame.unbanned.admin"));
        }

        private static String color(String string) {
            if (string.endsWith("&&")) {
                string = string.substring(0, string.length() - 2);
            }
            return string.replace("&&", String.valueOf(ChatColor.COLOR_CHAR));
        }
    }

    private final BanCache banCache = new BanCache(this);
    private final SQLBansImplementation implementation;
    private String serverName;
    private SQLHandler sql;

    /**
     * If you start me up, I'll never stop
     * But really, please only initialize me once.
     *
     * @param implementation the implementation in use
     */
    public SQLBans(SQLBansImplementation implementation) {
        this.implementation = implementation;

        try {
            this.reload();
        } catch (final SQLBansException e) {
            return;
        }

        // Command registration
        this.implementation.registerCommand(new BanCommand(this));
        this.implementation.registerCommand(new KickCommand(this));
        this.implementation.registerCommand(new ReloadCommand(this));
        this.implementation.registerCommand(new UnbanCommand(this));

        // Backup disabled pending 1.7.6 changes
        // this.getScheduler().repeatingTask(new BackupTask(this), 5, 300);

        this.implementation.registerLoginAttemptListening();
    }

    /**
     * Ban an IP address.
     *
     * @param address InetAddress to ban
     * @param reason Reason for the ban
     * @param admin Who banned the user. 16 character limit
     */
    public void banIP(final InetAddress address, final String reason, final String admin) {
        Util.nullCheck(address, "IP Address");
        Util.nullCheck(reason, "Ban reason");
        Util.nullCheck(admin, "Admin");
        this.getScheduler().run(new Runnable() {
            @Override
            public void run() {
                try {
                    SQLBans.this.sql.ban(address, reason, admin);
                    SQLBans.this.banCache.addIP(address);
                } catch (final Exception e) {
                    SQLBans.this.getLogger().log(Level.SEVERE, "Could not ban " + address.getHostAddress(), e);
                    SQLBans.this.sendMessage(Perm.MESSAGE_BAN_ADMIN, ChatColor.RED + "[SQLBans] Failed to ban " + address.getHostAddress());
                }
            }
        });
    }

    /**
     * Ban a username.
     *
     * @param name Username to ban
     * @param reason Reason for the ban
     * @param admin Who banned the user. 16 character limit
     */
    public void banName(final String name, final String reason, final String admin) {
        Util.nullCheck(name, "Username");
        Util.nullCheck(reason, "Ban reason");
        Util.nullCheck(admin, "Admin");
        this.getScheduler().run(new Runnable() {
            @Override
            public void run() {
                try {
                    SQLBans.this.sql.ban(name, reason, admin);
                    SQLBans.this.banCache.addName(name);
                } catch (final Exception e) {
                    SQLBans.this.getLogger().log(Level.SEVERE, "Could not ban " + name, e);
                    SQLBans.this.sendMessage(Perm.MESSAGE_BAN_ADMIN, ChatColor.RED + "[SQLBans] Failed to ban " + name);
                }
            }
        });
    }

    /**
     * Get the Logger
     *
     * @return the current implementation's logger
     */
    public Logger getLogger() {
        return this.implementation.getLogger();
    }

    /**
     * Get an array of online players
     *
     * @return online Players from the implementation
     */
    public Player[] getOnlinePlayers() {
        return this.implementation.getOnlinePlayers();
    }

    /**
     * Get a Player by name
     *
     * @param name Username queried
     * @return Player from the implementation
     */
    public Player getPlayer(String name) {
        Util.nullCheck(name, "Player name");
        return this.implementation.getPlayer(name);
    }

    /**
     * Get an InputStream of a file by path
     *
     * @param path Path to the file
     * @return InputStream for the file or null if not found
     * @throws IOException If something goes terribly wrong
     */
    InputStream getResource(String path) throws IOException {
        Util.nullCheck(path, "Resource path");
        final URL url = this.getClass().getClassLoader().getResource(path);
        if (url == null) {
            return null;
        }
        final URLConnection urlConnection = url.openConnection();
        urlConnection.setUseCaches(false);
        return urlConnection.getInputStream();
    }

    /**
     * Get the Scheduler
     *
     * @return the implementation's scheduler
     */
    public Scheduler getScheduler() {
        return this.implementation.getScheduler();
    }

    /**
     * Get the current server name as defined in config
     *
     * @return the current server name
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     * Get the current SQLBans version
     *
     * @return the implementation's stored version
     */
    public String getVersion() {
        return this.implementation.getVersion();
    }

    /**
     * Process if a user may join the server, by username and IP
     * Sets the Result of the UserData and a disconnect reason
     *
     * @param data UserData which will be processed
     * @param isJoin Set to true to indicate this is an actual join attempt, to log the username/IP
     */
    public void processUserData(UserData data, boolean isJoin) {
        Util.nullCheck(data, "UserData");
        if (this.banCache.containsName(data.getName()) || this.banCache.containsIP(data.getIP())) {
            data.disallow(UserData.Result.KICK_BANNED, SQLBans.Messages.getDisconnectRejected());
            return;
        }
        try {
            if (!this.sql.canJoin(data.getName())) {
                data.disallow(UserData.Result.KICK_BANNED, SQLBans.Messages.getDisconnectRejected());
                this.banCache.addName(data.getName());
            }
            if (!this.sql.canJoin(data.getIP())) {
                data.disallow(UserData.Result.KICK_BANNED, SQLBans.Messages.getDisconnectRejected());
                this.banCache.addIP(data.getIP());
            }
            if (isJoin) {
                this.sql.logJoin(data.getName(), data.getIP());
            }
        } catch (final Exception e) {
            data.disallow(UserData.Result.KICK_OTHER, "Connection error: Please retry.");
            this.getLogger().log(Level.SEVERE, "Severe error on user connect", e);
        }
    }

    /**
     * Reload configuration and restart SQL connection
     * Called on startup and in reload command
     *
     * @throws SQLBansException
     */
    public void reload() throws SQLBansException {
        Config config = new Config(this);
        SQLBans.Messages.load(config);
        this.serverName = config.getString("server-name");
        final String host = config.getString("database.host");
        final int port = config.getInt("database.port");
        final String db = config.getString("database.database");
        final String user = config.getString("database.auth.username");
        final String pass = config.getString("database.auth.password");
        final String bansTableName = config.getString("database.tablenames.bans", "SQLBans_bans");
        final String logTableName = config.getString("database.tablenames.log", "SQLBans_log");

        String tableCreate = null;
        final StringBuilder builder = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getResource("create.sql")))) {
            String next;
            while ((next = reader.readLine()) != null) {
                builder.append(next);
            }
            tableCreate = String.format(builder.toString(), bansTableName, logTableName);
        } catch (final IOException e) {
            new SQLBansException("Could not load default table creation text", e).printStackTrace();
        }
        try {
            this.sql = new SQLHandler(this, host, port, user, pass, db, bansTableName, logTableName, tableCreate);
        } catch (final SQLBansException e) {
            this.getLogger().log(Level.SEVERE, "Failure to load, shutting down", e);
            this.implementation.shutdown();
            throw new SQLBansException("Shutdown");
        }
    }

    /**
     * Save a resource to the plugin folder, by path
     *
     * @param path Path to the local resource
     * @param replace If true, overwrite on save
     * @throws IOException If something goes horribly wrong
     * @throws SQLBansException If the resource doesn't exist to begin with
     */
    public void saveResource(String path, boolean replace) throws IOException, SQLBansException {
        Util.nullCheck(path, "Resource path");
        try (InputStream input = this.getResource(path)) {
            if (input == null) {
                throw new SQLBansException("Resource not found: " + path);
            }
            final File outputFile = new File(this.getDataFolder(), path);
            final int slashLocation = path.lastIndexOf("/");
            final File outputFolder = slashLocation >= 0 ? new File(this.getDataFolder(), path.substring(0, slashLocation)) : this.getDataFolder();
            outputFolder.mkdirs();
            if (replace || !outputFile.exists()) {
                try (OutputStream output = new FileOutputStream(outputFile)) {
                    final byte[] buf = new byte[1024];
                    int len;
                    while ((len = input.read(buf)) > 0) {
                        output.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            // TODO complain
        }
    }

    /**
     * Send a message to all Players with a permission
     *
     * @param permission Permission to check against
     * @param message Message to send
     */
    public void sendMessage(Perm permission, String message) {
        Util.nullCheck(permission, "Permission");
        Util.nullCheck(message, "Message");
        this.implementation.sendMessage(permission, message);
    }

    /**
     * Unban an IP address
     *
     * @param address InetAddress to unban
     */
    public void unbanIP(final InetAddress address) {
        Util.nullCheck(address, "IP Address");
        this.getScheduler().run(new Runnable() {
            @Override
            public void run() {
                try {
                    SQLBans.this.sql.unban(address);
                    SQLBans.this.banCache.removeIP(address);
                } catch (final Exception e) {
                    SQLBans.this.getLogger().log(Level.SEVERE, "Could not unban " + address.getHostAddress(), e);
                    SQLBans.this.sendMessage(Perm.MESSAGE_UNBAN_ADMIN, ChatColor.RED + "[SQLBans] Failed to unban " + address.getHostAddress());
                }
            }
        });
    }

    /**
     * Unban a username
     *
     * @param name Username to unban
     */
    public void unbanName(final String name) {
        Util.nullCheck(name, "Username");
        this.getScheduler().run(new Runnable() {
            @Override
            public void run() {
                try {
                    SQLBans.this.sql.unban(name);
                    SQLBans.this.banCache.removeName(name);
                } catch (final Exception e) {
                    SQLBans.this.getLogger().log(Level.SEVERE, "Could not unban " + name, e);
                    SQLBans.this.sendMessage(Perm.MESSAGE_UNBAN_ADMIN, ChatColor.RED + "[SQLBans] Failed to unban " + name);
                }
            }
        });
    }

    BanCache getBanCache() {
        return this.banCache;
    }

    File getDataFolder() {
        return this.implementation.getDataFolder();
    }

    SQLHandler getSQL() {
        return this.sql;
    }
}