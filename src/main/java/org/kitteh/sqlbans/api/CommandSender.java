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

import org.kitteh.sqlbans.Perm;

/**
 * A command sender.
 */
public interface CommandSender {

    /**
     * Get the name of the CommandSender
     * If a Player, this is their username
     *
     * @return the sender's name
     */
    public String getName();

    /**
     * Get if a CommandSender has a permission
     *
     * @param permission Permission to check
     * @return true if the CommandSender has the permission
     */
    public boolean hasPermission(Perm permission);

    /**
     * Send the CommandSender a message
     *
     * @param message Message to send
     */
    public void sendMessage(String message);
}