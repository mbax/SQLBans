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

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BanCache {

    private final SQLBans plugin;

    private final Set<String> names = Collections.synchronizedSet(new HashSet<String>());

    private final Set<InetAddress> ips = Collections.synchronizedSet(new HashSet<InetAddress>());

    BanCache(SQLBans plugin) {
        this.plugin = plugin;
    }

    public boolean containsIP(InetAddress ip) {
        return this.ips.contains(ip);
    }

    public boolean containsName(String name) {
        return this.names.contains(name.toLowerCase());
    }

    public void removeIP(InetAddress ip) {
        this.ips.remove(ip);
    }

    public void removeName(String name) {
        this.names.remove(name.toLowerCase());
    }

    void addIP(final InetAddress ip) {
        this.ips.add(ip);
        this.plugin.getScheduler().delayedTask(new Runnable() {
            @Override
            public void run() {
                BanCache.this.removeIP(ip);
            }
        }, 60);
    }

    void addName(final String name) {
        this.names.add(name.toLowerCase());
        this.plugin.getScheduler().delayedTask(new Runnable() {
            @Override
            public void run() {
                BanCache.this.removeName(name);
            }
        }, 60);
    }
}