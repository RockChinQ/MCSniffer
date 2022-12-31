package test;

import api.main.conn.MinecraftServer;
import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;

public class APITest {
    public static void main(String[] args) throws Exception {
        String proxyURL = "localhost:7890";
        String[] proxyURLSplit=proxyURL.split(":");
        Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyURLSplit[0],Integer.parseInt(proxyURLSplit[1])));
        MinecraftServer server = new MinecraftServer("cn-zz-bgp-1.natfrp.cloud",65040,proxy,true,10000);
        System.out.println(server.getRawJSONString());
        System.out.println(new Gson().toJson(server.getModInfo()));
        System.out.println(new Gson().toJson(server.getModPackData()));
    }
}
