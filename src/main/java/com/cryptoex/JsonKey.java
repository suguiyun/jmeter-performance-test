package com.cryptoex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonKey {
    public static List<Group> list = new ArrayList<>();

    static {
        list = getJson();
    }

    class Group {
        String apiKey;
        String apiSecret;
    }

    static List<Group> getJson() {
        List<Group> listJson = new ArrayList<>();
        try {
            InputStream stream = JsonKey.class.getResourceAsStream("/init-keys.json");
            JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            Gson gson = new GsonBuilder().create();
            reader.beginArray();
            while (reader.hasNext()) {
                Group group = gson.fromJson(reader, Group.class);
                listJson.add(group);
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return listJson;
    }
}
