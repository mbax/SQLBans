package org.kitteh.sqlbans.fish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.bukkit.event.Listener;

import com.google.gson.Gson;

public class FishBans implements Listener {
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
            url = new URL("http://fishbans.com/api/bans/"+user+"/");
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
