package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.FileIO;

import java.io.File;
import java.io.IOException;

public class Settings {
    public String addresses;
    public String ports;
    public String proxyURL;
    public int thread=16;
    public int timeout=3;
    public int intervalMin=1;
    public int intervalMax=5;

    public void dump() throws Exception {
        FileIO.write("settings.json", toPrettyFormat(new Gson().toJson(this)));
    }
    public static String toPrettyFormat(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    public void load() throws FileIO.FileTooBigException, IOException {
        if (new File("settings.json").exists()) {
            String json = FileIO.read("settings.json");
            Settings settings = new Gson().fromJson(json, Settings.class);
            this.addresses = settings.addresses;
            this.ports = settings.ports;
            this.proxyURL = settings.proxyURL;
            this.thread = settings.thread;
            this.timeout = settings.timeout;
            this.intervalMin = settings.intervalMin;
            this.intervalMax = settings.intervalMax;
        }
    }
}
