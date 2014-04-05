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
package org.kitteh.sqlbans.fish;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public final class FishBans {
    public class UserData {
        public class BansData {
            public class ServicesData {
                public int bans;
                public Map<String, String> ban_info;
            }

            public String username;

            public Map<String, ServicesData> service;
        }

        public String success;

        public BansData bans;
    }

    public static void test(String user) {
        URL url;
        try {
            url = new URL("http://fishbans.com/api/bans/" + user + "/");
            final URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-agent", "Meow meow mbax testing");
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            final StringBuilder builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            final String json = builder.toString();
            final Gson gson = new Gson();
            final UserData meow = gson.fromJson(json, UserData.class);
            System.out.println(gson.toJson(meow));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
