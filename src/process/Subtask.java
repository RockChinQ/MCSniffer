package process;

public class Subtask {

    String address;
    int port;

    String description;

    int maxPlayers;
    int onlinePlayers;

    String versionName;
    int protocolVersion;

    public static final int STATUS_WAITING = 0,STATUS_TESTING = 1,STATUS_FINISHED = 2;
    public Subtask(String address,int port) {
        this.address=address;
        this.port=port;
    }
}
