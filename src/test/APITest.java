package test;

import api.main.conn.MinecraftServer;
import com.google.gson.Gson;

import java.util.Arrays;

public class APITest {
    public static void main(String[] args) throws Exception {
        MinecraftServer server = new MinecraftServer("frp.freefrp.net",3168,true,10000);
        System.out.println(server.getRawJSONString());
    }
}
