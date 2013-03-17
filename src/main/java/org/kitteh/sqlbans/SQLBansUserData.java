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

import org.kitteh.sqlbans.api.UserData;

/**
 * Default UserData implementation. Methods have no immediate effect.
 */
public final class SQLBansUserData implements UserData {

    private Result result = Result.UNCHANGED;
    private final String name;
    private final InetAddress ip;
    private String reason = null;

    public SQLBansUserData(String name, InetAddress ip) {
        this.name = name;
        this.ip = ip;
    }

    @Override
    public void disallow(Result result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    @Override
    public InetAddress getIP() {
        return this.ip;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public Result getResult() {
        return this.result;
    }
}