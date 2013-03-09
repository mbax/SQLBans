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
package org.kitteh.sqlbans.api;

import java.io.File;
import java.util.logging.Logger;

import org.kitteh.sqlbans.Perm;

public interface SQLBansImplementation {

    public Scheduler getScheduler();

    public void shutdown();

    public void sendMessage(final Perm permission, final String message);

    public void registerCommand(SQLBansCommand command);

    public void registerLoginAttemptListening();

    public Logger getLogger();

    public File getDataFolder();

    public String getVersion();

    public Player getPlayer(String name);

    public Player[] getOnlinePlayers();
}