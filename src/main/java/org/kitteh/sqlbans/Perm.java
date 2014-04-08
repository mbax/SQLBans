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

/**
 * Permissions for the plugin.
 * The toString method provides the String form (sqlbans.perm.node)
 */
public enum Perm {

    /* Commands */
    COMMAND_BAN,
    COMMAND_KICK,
    COMMAND_LOOKUP_BANS,
    COMMAND_LOOKUP_IPS,
    COMMAND_RELOAD,
    COMMAND_UNBAN,

    /* Messaging */
    MESSAGE_BAN_NORMAL,
    MESSAGE_BAN_ADMIN,
    MESSAGE_BAN_OFFLINE,

    MESSAGE_KICK_NORMAL,
    MESSAGE_KICK_ADMIN,

    MESSAGE_RELOAD,

    MESSAGE_UNBAN_NORMAL,
    MESSAGE_UNBAN_ADMIN;

    private final String perm;

    private Perm() {
        this.perm = "sqlbans." + this.name().toLowerCase().replace("_", ".");
    }

    @Override
    public String toString() {
        return this.perm;
    }
}