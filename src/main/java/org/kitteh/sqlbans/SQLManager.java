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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class SQLManager {

    final class SQLConnection implements AutoCloseable {

        private Connection connection;
        private final String url;
        private boolean inUse = false;

        SQLConnection(String url) throws SQLException {
            this.connection = DriverManager.getConnection(url);
            this.url = url;
        }

        Connection getConnection() {
            return this.connection;
        }

        private boolean inUse() {
            return this.inUse;
        }

        private void myTurn() throws SQLException {
            if (this.connection.isValid(1)) {
                this.connection.close();
                this.connection = DriverManager.getConnection(this.url);
            }
            this.inUse = true;
        }

        /**
         * Note: THIS DOES NOT CLOSE THE ACTUAL CONNECTION.
         * <p/>
         * Purely used for marking it as no longer in use.
         */
        @Override
        public void close() {
            this.inUse = false;
        }

        private void reset() throws SQLException {
            try {
                this.connection.close();
            } catch (final SQLException e) {
            }
            this.connection = DriverManager.getConnection(this.url);
            this.inUse = false;
        }
    }

    private final SQLConnection updateConnection;
    private final int conCount = 4;

    private final SQLConnection[] queryConnections = new SQLConnection[this.conCount];

    SQLManager(String url) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.updateConnection = new SQLConnection(url);
        for (int x = 0; x < this.conCount; x++) {
            this.queryConnections[x] = new SQLConnection(url);
        }
    }

    synchronized SQLConnection getQueryConnection() throws SQLException {
        int i = 0;
        final long start = System.currentTimeMillis();
        SQLConnection con = null;
        while (con == null) {
            final SQLConnection test = this.queryConnections[i];
            if (!test.inUse()) {
                con = test;
            }
            if (!test.getConnection().isValid(1) || ((System.currentTimeMillis() - 5000) > start)) {
                test.reset();
                con = test;
            }
            if (i++ == this.conCount) {
                i = 0;
            }
        }
        con.myTurn();
        return con;
    }

    synchronized SQLConnection getUpdateConnection() throws SQLException {
        final long start = System.currentTimeMillis();
        while (this.updateConnection.inUse()) {
            if ((System.currentTimeMillis() - 5000) > start) {
                System.out.println("[SQLBans] Something went funky with SQL. Resetting a connection");
                this.updateConnection.reset();
            }
        }
        this.updateConnection.myTurn();
        return this.updateConnection;
    }
}