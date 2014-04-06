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
package org.kitteh.sqlbans.bungeecord;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.kitteh.sqlbans.Perm;
import org.kitteh.sqlbans.api.Player;

import java.util.UUID;

final class BungeePlayer implements Player {

    private final ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public boolean hasPermission(Perm permission) {
        return this.player.hasPermission(permission.toString());
    }

    @Override
    public void kick(String reason) {
        this.player.disconnect(TextComponent.fromLegacyText(reason));
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(TextComponent.fromLegacyText(message));
    }
}