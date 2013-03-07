/*
 * SQLBans
 * Copyright 2012 Matt Baxter
 *
 * Google Gson
 * Copyright 2008-2011 Google Inc.
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.kitteh.sqlbans.SQLManager.SQLConnection;
import org.kitteh.sqlbans.exceptions.SQLBansException;

public class SQLHandler {

    private static SQLHandler instance = null;
    private static String tableName;

    public static void ban(String user, String reason, String admin, int type) throws SQLBansException, SQLException {
        final SQLConnection con = SQLHandler.instance().manager.getUpdateConnection();
        final PreparedStatement statement = con.getConnection().prepareStatement("INSERT INTO `" + SQLHandler.tableName + "` (`info`, `type`, `reason`, `admin`, `timestamp`, `server`) VALUES (?, ?, ?, ?, ?, ?)");
        statement.setString(1, user);
        statement.setInt(2, type);
        statement.setString(3, reason);
        statement.setString(4, admin);
        statement.setTimestamp(5, new Timestamp(new Date().getTime()));
        statement.setString(6, SQLHandler.instance().plugin.getServerName());
        statement.executeUpdate();
        con.myWorkHereIsDone();
    }

    public static boolean canJoin(String name) throws SQLBansException, SQLException {
        final SQLConnection con = SQLHandler.instance().manager.getQueryConnection();
        final PreparedStatement banQuery = con.getConnection().prepareStatement("SELECT `id` FROM `" + SQLHandler.tableName + "` WHERE `info`=? AND `isbanned`=1");
        banQuery.setString(1, name);
        final boolean ret = !banQuery.executeQuery().first();
        con.myWorkHereIsDone();
        return ret;
    }

    public static Set<BanItem> getAllBans(int type) throws SQLException, SQLBansException {
        final Set<BanItem> list = new HashSet<BanItem>();
        final SQLConnection con = SQLHandler.instance().manager.getQueryConnection();
        final PreparedStatement statement = con.getConnection().prepareStatement("SELECT `info`,`reason`,`admin`,`timestamp`,`banlength` FROM `" + SQLHandler.tableName + "` WHERE `type`=? AND `isbanned`=1");
        statement.setInt(1, type);
        final ResultSet result = statement.executeQuery();
        while (result.next()) {
            list.add(new BanItem(result.getString("info"), result.getString("admin"), result.getTimestamp("timestamp"), result.getInt("banlength"), result.getString("reason")));
        }
        con.myWorkHereIsDone();
        return list;
    }

    public static void nullifyInstance() {
        SQLHandler.instance = null;
    }

    public static void start(SQLBans plugin, String host, int port, String user, String pass, String db, String table) throws SQLBansException {
        if (SQLHandler.instance != null) {
            throw new SQLBansException("Thread already running! Something has gone terribly wrong!");
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (final ClassNotFoundException e1) {
            throw new SQLBansException("What on earth are you doing. This isn't CraftBukkit.");
        }
        SQLHandler.instance = new SQLHandler(plugin, host, port, user, pass, db, table);
    }

    public static void unban(String user) throws SQLBansException, SQLException {
        final SQLConnection con = SQLHandler.instance().manager.getUpdateConnection();
        final PreparedStatement statement = con.getConnection().prepareStatement("UPDATE `" + SQLHandler.tableName + "` SET `isbanned` = 0 WHERE `info` = ?");
        statement.setString(1, user);
        statement.executeUpdate();
        con.myWorkHereIsDone();
    }

    private static SQLHandler instance() throws SQLBansException {
        if (SQLHandler.instance == null) {
            throw new SQLBansException("Not loaded!");
        }
        return SQLHandler.instance;
    }

    private SQLManager manager;
    private SQLBans plugin;

    public SQLHandler() throws SQLBansException {
        throw new SQLBansException("Stop right there, criminal scum");
    }

    private SQLHandler(SQLBans plugin, String host, int port, String user, String pass, String db, String table) throws SQLBansException {
        this.plugin = plugin;
        try {
            this.manager = new SQLManager("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&user=" + user + "&password=" + pass);
        } catch (final Exception e) {
            throw new SQLBansException("SQL connection failure!", e);
        }
        try {
            SQLHandler.tableName = table;
            final SQLConnection con = this.manager.getQueryConnection();
            final ResultSet bansExists = con.getConnection().getMetaData().getTables(null, null, SQLHandler.tableName, null);
            if (!bansExists.first()) {
                if (SQLBans.TABLE_CREATE != null) {
                    con.getConnection().createStatement().executeUpdate(SQLBans.TABLE_CREATE);
                } else {
                    new SQLBansException("You need to create the bans table.");
                }
            }
            con.myWorkHereIsDone();
        } catch (final Exception e) {
            throw new SQLBansException("SQL failure while checking for table!", e);
        }
    }
}