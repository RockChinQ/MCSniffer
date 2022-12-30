package api.main.api;

import java.awt.image.BufferedImage;

/**
 * Defines interfaces to get the basic info of a Minecraft server.
 * @author Rock Chin
 */
public interface IServerInfo {
    class Player{
        public String name;
        public String id;
    }
    class ExtraDescr{
        public String text;
        public String color;
    }
    class ModInfo {
        String type;
        static class Mod {
            String modid;
            String version;
        }
        Mod[] modList;
    }
    class ModPackData{
        int projectID;
        String name;
        String version;
        int versionID;
        String releaseType;
        boolean isMetadata;
    }

    class ForgeData{
        int fmlNetworkVersion;
        String d;
        boolean truncated;
    }
    boolean isAvailable();
    String getVersionName();
    int getVersionProtocol();
    int getMaxPlayer();
    int getOnlinePlayer();
    Player[] getPlayerList();
    String getDefaultDescriptionText();
    String getDefaultDescriptionColor();
    ExtraDescr[] getExtraDescription();
    String getFaviconBase64();
    BufferedImage getFaviconImage();
    String getRawJSONString();

    ModInfo getModInfo();
    ModPackData getModPackData();
    ForgeData getForgeData();
}
