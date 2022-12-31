package process;

import api.main.conn.MinecraftServer;
import core.Main;

import java.net.Proxy;
import java.util.ArrayList;

public class WorkerThread extends Thread{
    ISubtaskIterator subtaskIterator;
    int timeout;

    int intervalMin;
    int intervalMax;

    Proxy proxy;

    public int status=0;

    public static final int STATUS_SLEEPING = 0,STATUS_TESTING = 1,STATUS_FINISHED = 2;
    public WorkerThread(ISubtaskIterator subtaskIterator, Proxy proxy, int timeout, int intervalMin, int intervalMax){
        this.subtaskIterator=subtaskIterator;
        this.timeout=timeout;
        this.proxy=proxy;
        this.intervalMin=intervalMin;
        this.intervalMax=intervalMax;
    }
    @Override
    public void run() {
        while (subtaskIterator.hasNext()){
            Subtask subtask=subtaskIterator.next();
            status= STATUS_SLEEPING;
            try {
                Thread.sleep((long) (Math.random()*(intervalMax-intervalMin)+intervalMin));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status=STATUS_TESTING;
            try {
                MinecraftServer server = new MinecraftServer(subtask.address,subtask.port,this.proxy,false,this.timeout);
                if (server.isAvailable()){
                    subtask.minecraftServer=server;
                    ArrayList<String> playerList=new ArrayList<>();

                    for (int i = 0; i < server.getOnlinePlayer(); i++) {
                        playerList.add(server.getPlayerList()[i].name);
                    }

                    subtask.playerList=playerList.toArray(new String[0]);

                    subtaskIterator.submit(subtask);
                }else {
                    throw new Exception("Server is not available");
                }
            } catch (Exception e) {
                subtaskIterator.exception(subtask,e);
            }
        }
        status=STATUS_FINISHED;
        for (WorkerThread workerThread : Main.snifferTask.workerThreads) {
            if (workerThread.status!=STATUS_FINISHED){
                return;
            }
        }
        Main.mainFrame.settingsPanel.pauseAndResume.setEnabled(false);
        Main.snifferTask.stop();
    }
}
