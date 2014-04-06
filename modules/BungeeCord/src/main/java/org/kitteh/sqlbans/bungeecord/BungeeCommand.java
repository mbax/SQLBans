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

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.kitteh.sqlbans.api.SQLBansCommand;

final class BungeeCommand extends Command {

    private final SQLBansCommand command;

    public BungeeCommand(SQLBansCommand command) {
        super(command.getName(), command.getPermission().toString(), command.getAliases());
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.command.processCommand(new BungeeSender(sender), args);
    }
}