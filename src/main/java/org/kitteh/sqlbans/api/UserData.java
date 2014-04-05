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
package org.kitteh.sqlbans.api;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Class for tracking a user's ability to join
 */
public interface UserData {

    public enum Result {
        /**
         * Default state
         */
        UNCHANGED,
        /**
         * Kicked for any reason not specified by another Result
         */
        KICK_OTHER,
        /**
         * Kicked because banned
         */
        KICK_BANNED
    }

    /**
     * Disallow the player from joining
     *
     * @param result Result for the player
     * @param reason Reason to display to the user
     */
    public void disallow(Result result, String reason);

    /**
     * Get the player's IP
     *
     * @return InetAddress of the player
     */
    public InetAddress getIP();

    /**
     * Get the player's name
     *
     * @return Username of the player
     */
    public String getName();

    /**
     * Get the reason for the player being denied
     *
     * @return kick reason or null if none.
     */
    public String getReason();

    /**
     * Get the result of the join attempt
     *
     * @return Result of the attempt
     */
    public Result getResult();

    /**
     * Gets the player's UUID
     *
     * @return player's unique ID
     */
    UUID getUniqueId();
}