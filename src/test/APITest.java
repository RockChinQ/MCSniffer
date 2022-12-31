package test;

import api.main.conn.MinecraftServer;
import com.google.gson.Gson;

import java.util.Arrays;

public class APITest {
    public static void main(String[] args) throws Exception {
        MinecraftServer server = new MinecraftServer("cn-zz-bgp-6.natfrp.cloud",28338,null,true,10000);
        System.out.println(server.getRawJSONString());
        System.out.println(new Gson().toJson(server.getModInfo()));
        System.out.println(new Gson().toJson(server.getModPackData()));
    }
}
