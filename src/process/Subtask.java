package process;

import api.main.api.IServerInfo;

import java.awt.image.BufferedImage;

public class Subtask {

    String address;
    int port;

    String description;

    int maxPlayers;
    int onlinePlayers;

    String[] playerList;
    String versionName;
    int protocolVersion;

    BufferedImage favicon;

    IServerInfo.ModInfo modInfo;
    IServerInfo.ModPackData modPackData;

    public static final int STATUS_WAITING = 0,STATUS_TESTING = 1,STATUS_FINISHED = 2;
    public Subtask(String address,int port) {
        this.address=address;
        this.port=port;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public IServerInfo.ModInfo getModInfo() {
        return modInfo;
    }

    public void setModInfo(IServerInfo.ModInfo modInfo) {
        this.modInfo = modInfo;
    }

    public IServerInfo.ModPackData getModPackData() {
        return modPackData;
    }

    public void setModPackData(IServerInfo.ModPackData modPackData) {
        this.modPackData = modPackData;
    }

    public BufferedImage getFavicon() {
        return favicon;
    }

    public void setFavicon(BufferedImage favicon) {
        this.favicon = favicon;
    }

    public String[] getPlayerList() {
        return playerList;
    }

    public void setPlayerList(String[] playerList) {
        this.playerList = playerList;
    }
}
