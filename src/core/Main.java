package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Mapping;
import data.Settings;
import mapping.MappingRule;
import process.SnifferTask;
import ui.MainFrame;

import java.io.IOException;

public class Main {
    public static MainFrame mainFrame;
    public static SnifferTask snifferTask;
    public static Settings settings;

    public static final String VERSION = "0.2.0";
    public static MappingRule mappingRule;
    public static Mapping mapping;
    public static void main(String[] args) {
        settings=new Settings();
        try{
            settings.load();
        }catch (Exception ignored){}

        mapping=new Mapping();
        try{
            mapping.load();
        }catch (Exception ignored){}

        mainFrame=new MainFrame();

        new Thread(()->{
            try {
                HttpRequest.doGet("http://rockchin.top:18989/usage?service_name=mcsniffer&version="+VERSION+"&count=1");
            } catch (IOException ignored) {
                ;
            }
        }).start();
    }
    public static String toPrettyFormat(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
}
