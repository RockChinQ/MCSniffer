package data;

import com.google.gson.Gson;
import core.FileIO;
import core.Main;

import java.io.File;
import java.io.IOException;

public class Mapping {
    public int localPort=25565;
    public String remoteHost="localhost";
    public int remotePort=25565;
    public String proxyURL="";

    public void dump()throws Exception{
        FileIO.write("mapping.json", Main.toPrettyFormat(new Gson().toJson(this)));
    }

    public void load() throws FileIO.FileTooBigException, IOException {
        if (new File("mapping.json").exists()) {
            String json = FileIO.read("mapping.json");
            Mapping mapping = new Gson().fromJson(json, Mapping.class);
            this.localPort = mapping.localPort;
            this.remoteHost = mapping.remoteHost;
            this.remotePort = mapping.remotePort;
            this.proxyURL = mapping.proxyURL;
        }
    }
}
