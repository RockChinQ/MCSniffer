package process;

import api.main.api.IServerInfo;
import api.main.conn.MinecraftServer;

import java.awt.image.BufferedImage;
import java.net.Proxy;

public class Subtask {

    public MinecraftServer minecraftServer;

    String[] playerList;
    String address;
    int port;

    public Subtask(String address,int port) {
        this.address=address;
        this.port=port;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public void setMinecraftServer(MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getPlayerList() {
        return playerList;
    }

    public void setPlayerList(String[] playerList) {
        this.playerList = playerList;
    }

    public void refresh(Proxy proxy, int timeout) throws Exception {
        MinecraftServer server = new MinecraftServer(this.address,this.port,proxy,false,timeout);
        if(server.isAvailable()){
            this.minecraftServer=server;
        }else {
            throw new Exception("Server is not available");
        }
    }
}
