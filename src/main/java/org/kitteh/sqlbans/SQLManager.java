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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLManager {

    public class SQLConnection {
        private Connection connection;
        private final String url;
        private boolean inUse = false;

        public SQLConnection(String url) throws SQLException {
            this.connection = DriverManager.getConnection(url);
            this.url = url;
        }

        public Connection getConnection() {
            return this.connection;
        }

        public boolean inUse() {
            return this.inUse;
        }

        public void myTurn() throws SQLException {
            if (this.connection.isValid(1)) {
                this.connection.close();
                this.connection = DriverManager.getConnection(this.url);
            }
            this.inUse = true;
        }

        public void myWorkHereIsDone() {
            this.inUse = false;
        }

        public void reset() throws SQLException {
            this.connection.close();
            this.connection = DriverManager.getConnection(this.url);
            this.inUse = false;
        }
    }

    private final SQLConnection updateConnection;
    private final int conCount = 4;

    private final SQLConnection[] queryConnections = new SQLConnection[this.conCount];

    public SQLManager(String url) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.updateConnection = new SQLConnection(url);
        for (int x = 0; x < this.conCount; x++) {
            this.queryConnections[x] = new SQLConnection(url);
        }
    }

    public synchronized SQLConnection getQueryConnection() throws SQLException {
        int i = 0;
        final long start = System.currentTimeMillis();
        SQLConnection con = null;
        while (con == null) {
            final SQLConnection test = this.queryConnections[i];
            if (!test.inUse()) {
                con = test;
            }
            if ((System.currentTimeMillis() - 5000) > start) {
                test.reset();
                System.out.println("[SQLBans] Something went funky with SQL. Resetting a connection");
                con = test;
            }
            if (i++ == this.conCount) {
                i = 0;
            }
        }
        con.myTurn();
        return con;
    }

    public synchronized SQLConnection getUpdateConnection() throws SQLException {
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
