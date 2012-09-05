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

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.kitteh.sqlbans.exceptions.SQLBansException;

public class SQLHandler {

    private static SQLHandler instance = null;
    private static Object sync = new Object();
    private static String tableName;

    public static void ban(String user, String reason, String admin) throws SQLBansException {
        synchronized (SQLHandler.sync) {
            try {
                final PreparedStatement statement = SQLHandler.instance().connection.prepareStatement("INSERT INTO `" + SQLHandler.tableName + "` (`username`, `reason`, `admin`) VALUES (?, ?, ?)");
                statement.setString(1, user);
                statement.setString(2, reason);
                statement.setString(3, admin);
                statement.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean canJoin(String name) throws SQLBansException, SQLException {
        synchronized (SQLHandler.sync) {
            final PreparedStatement banQuery = SQLHandler.instance().connection.prepareStatement("SELECT `id` FROM `" + SQLHandler.tableName + "` WHERE `username`=? AND `banned`=1");
            banQuery.setString(1, name);
            if (banQuery.executeQuery().first()) {
                return false;
            }
        }
        return true;
    }

    public static void nullifyInstance() {
        synchronized (SQLHandler.sync) {
            SQLHandler.instance = null;
        }
    }

    public static void start(String host, int port, String user, String pass, String db, String table) throws SQLBansException {
        synchronized (SQLHandler.sync) {
            if (SQLHandler.instance != null) {
                throw new SQLBansException("Thread already running! Something has gone terribly wrong!");
            }
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (final ClassNotFoundException e1) {
                throw new SQLBansException("What on earth are you doing. This isn't CraftBukkit");
            }
            SQLHandler.instance = new SQLHandler(host, port, user, pass, db, table);
        }
    }

    public static void unban(String user) throws SQLBansException {
        synchronized (SQLHandler.sync) {
            try {
                final PreparedStatement statement = SQLHandler.instance().connection.prepareStatement("UPDATE `" + SQLHandler.tableName + "` SET `banned` = 0 WHERE `username` = ?");
                statement.setString(1, user);
                statement.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static SQLHandler instance() throws SQLBansException {
        if (SQLHandler.instance == null) {
            throw new SQLBansException("Not loaded!");
        }
        return SQLHandler.instance;
    }

    private java.sql.Connection connection;

    public SQLHandler() throws SQLBansException {
        throw new SQLBansException("Stop right there, criminal scum");
    }

    private SQLHandler(String host, int port, String user, String pass, String db, String table) throws SQLBansException {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&user=" + user + "&password=" + pass);
        } catch (final Exception e) {
            throw new SQLBansException("SQL connection failure!", e);
        }
        try {
            SQLHandler.tableName = table;
            final ResultSet bansExists = this.connection.getMetaData().getTables(null, null, SQLHandler.tableName, null);
            if (!bansExists.first()) {
                if (SQLBans.TABLE_CREATE != null) {
                    this.connection.createStatement().executeUpdate(SQLBans.TABLE_CREATE);
                } else {
                    new SQLBansException("You need to create the bans table.");
                }
            }
        } catch (final Exception e) {
            throw new SQLBansException("SQL failure while checking for table!", e);
        }
    }

}
