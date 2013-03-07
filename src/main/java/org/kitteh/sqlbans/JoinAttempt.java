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

public class JoinAttempt {

    public enum Result {
        UNCHANGED,
        KICK_OTHER,
        KICK_BANNED;
    }

    private Result result = Result.UNCHANGED;
    private final String name;
    private final String ip;
    private String reason = null;

    public JoinAttempt(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public void disallow(Result result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    public String getIP() {
        return this.ip;
    }

    public String getName() {
        return this.name;
    }

    public String getReason() {
        return this.reason;
    }

    public Result getResult() {
        return this.result;
    }
}