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

/**
 * Types of bans available
 */
public enum BanType {

    /**
     * Username bans
     */
    NAME("player", 0),
    /**
     * IP address bans
     */
    IP("ip", 1);

    private final int id;
    private final String name;

    private BanType(String name, int id) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get the internal int ID of the ban type, used in SQL
     * 
     * @return the internal ban type ID
     */
    public int getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}