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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;

final class BackupTask implements Runnable {

    final static class BanItem {

        private final String reason;
        private final String admin;
        private final Date created;
        private final int length;
        private final String info;

        BanItem(String info, String admin, Date created, int length, String reason) {
            this.info = info;
            this.admin = admin;
            this.created = created;
            this.length = length;
            this.reason = reason;
        }

        String getAdmin() {
            return this.admin;
        }

        Date getCreated() {
            return this.created;
        }

        String getInfo() {
            return this.info;
        }

        int getLength() {
            return this.length;
        }

        String getReason() {
            return this.reason;
        }
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final SQLBans plugin;

    BackupTask(SQLBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (final BanType type : BanType.values()) {
            Set<BanItem> set = null;
            try {
                set = this.plugin.getSQL().getAllBans(type);
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not acquire " + type.getName() + " bans for backup", e);
            }
            if ((set == null) || set.isEmpty()) {
                continue;
            }
            try {
                final PrintWriter writer = new PrintWriter(new FileWriter(new File(this.plugin.getDataFolder(), "backup-" + type.getName() + "s.txt"), false));

                writer.println("# Updated " + (new SimpleDateFormat()).format(new Date()) + " by SQLBans " + this.plugin.getVersion());
                writer.println("# victim name | ban date | banned by | banned until | reason");
                writer.println();

                for (final BanItem item : set) {
                    final Date created = item.getCreated();
                    final int length = item.getLength();
                    final Date expires = length == 0 ? null : new Date(created.getTime() + (item.getLength() * 60000));

                    final StringBuilder builder = new StringBuilder();

                    builder.append(item.getInfo());
                    builder.append("|");
                    builder.append(BackupTask.format.format(created));
                    builder.append("|");
                    builder.append(item.getAdmin());
                    builder.append("|");
                    builder.append(expires == null ? "Forever" : BackupTask.format.format(expires));
                    builder.append("|");
                    builder.append(item.getReason());
                    writer.println(builder.toString());
                }
                writer.close();
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save " + type.getName() + " ban list", e);
            }
        }
    }
}