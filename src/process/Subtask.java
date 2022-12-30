package process;

import api.main.api.IServerInfo;
import api.main.conn.MinecraftServer;

import java.awt.image.BufferedImage;

public class Subtask {

    public MinecraftServer minecraftServer;

    String[] playerList;
    String address;
    int port;

    public static final int STATUS_WAITING = 0,STATUS_TESTING = 1,STATUS_FINISHED = 2;
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
}
