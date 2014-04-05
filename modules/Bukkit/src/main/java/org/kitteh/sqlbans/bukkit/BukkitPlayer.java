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
package org.kitteh.sqlbans.bukkit;

import org.bukkit.entity.Player;
import org.kitteh.sqlbans.Perm;

public final class BukkitPlayer implements org.kitteh.sqlbans.api.Player {

    private final Player player;

    public BukkitPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public boolean hasPermission(Perm permission) {
        return this.player.hasPermission(permission.toString());
    }

    @Override
    public void kick(String reason) {
        this.player.kickPlayer(reason);
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }
}