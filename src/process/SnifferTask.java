package process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.FileIO;
import core.Logger;
import core.Main;
import data.Progress;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

public class SnifferTask implements ISubtaskIterator {

    int thread;
    int timeout;

    int intervalMin,intervalMax;

    public ArrayList<String> addresses;
    public ArrayList<Integer> ports;

    public Proxy proxy;

    public int currentAddressIndex=0;
    public int currentPortIndex=0;

    public ArrayList<WorkerThread> workerThreads=new ArrayList<>();

    public long startTime;
    public int status=0;
    public static final int STATUS_STOPPED=0,STATUS_RUNNING=1,STATUS_PAUSED=2;
    public ArrayList<Subtask> results=new ArrayList<>();

    public Progress progress;
    public SnifferTask(String address,String port,String proxyURL, int thread,int timeout,int intervalMin,int intervalMax) {
        init(address,port,proxyURL,thread,timeout,intervalMin,intervalMax);
    }

    public SnifferTask(Progress progress) {
        this.progress=progress;
        init(progress.settings.addresses,progress.settings.ports,progress.settings.proxyURL,progress.settings.thread,progress.settings.timeout,progress.settings.intervalMin,progress.settings.intervalMax);
        currentAddressIndex=progress.addressIndex;
        currentPortIndex=progress.portIndex;
        startTime=progress.startTime;
        for (int i = 0; i < thread; i++) {
            Logger.log("Sniffer","Create thread: "+i);
            WorkerThread workerThread=new WorkerThread(this,proxy,timeout,intervalMin,intervalMax);
            workerThreads.add(workerThread);
        }
        Main.mainFrame.dashboardPanel.progressPanel.startTime=progress.startTime;
        Main.mainFrame.dashboardPanel.waterfallPanel.startTime=progress.startTime;
        results.addAll(Arrays.asList(progress.results));
        Main.mainFrame.resultPanel.updateServerList();
        Main.mainFrame.tabbedPane.setTitleAt(2,"Results"+" ("+results.size()+")");
        if (progress.status==Progress.STATUS_FINISHED){
            Main.mainFrame.settingsPanel.pauseAndResume.setEnabled(false);
        }
    }

    public void init(String address,String port,String proxyURL, int thread,int timeout,int intervalMin,int intervalMax){
        this.thread=thread;
        this.timeout=timeout;
        this.intervalMin= intervalMin;
        this.intervalMax= intervalMax;
        if (proxyURL!=null&&!proxyURL.equals("")){
            String[] proxyURLSplit=proxyURL.split(":");
            this.proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyURLSplit[0],Integer.parseInt(proxyURLSplit[1])));
        }
        this.list(address,port);
    }

    public void list(String address,String port) {
        addresses=new ArrayList<>();
        ports=new ArrayList<>();
        String[] addressList=address.split(",");
        String[] portList=port.split(",");
        addresses.addAll(Arrays.asList(addressList));

        for (String s : portList) {
            if (s.contains("-")) {
                String[] range=s.split("-");
                int start=Integer.parseInt(range[0]);
                int end=Integer.parseInt(range[1]);
                for (int i = start; i <= end; i++) {
                    ports.add(i);
                }
            } else {
                ports.add(Integer.parseInt(s));
            }
        }
    }

    public void start(){
        results.clear();
        progress=null;
        startTime=System.currentTimeMillis();
        Main.mainFrame.resultPanel.updateServerList();
        Main.mainFrame.tabbedPane.setTitleAt(2,"Results (0)");
        workerThreads.clear();

        for (int i = 0; i < thread; i++) {
            Logger.log("Sniffer","Create thread: "+i);
            WorkerThread workerThread=new WorkerThread(this,proxy,timeout,intervalMin,intervalMax);
            workerThread.start();
            workerThreads.add(workerThread);
        }
        Main.mainFrame.settingsPanel.setEditable(false);
        Main.mainFrame.tabbedPane.setSelectedIndex(1);
        Main.mainFrame.dashboardPanel.start(200);
        status=STATUS_RUNNING;
        Main.mainFrame.updateTitle();
    }

    public void pause(){
        //回溯指针
        for (int i=0;i<workerThreads.size();i++){
            WorkerThread workerThread=workerThreads.get(i);
            if (workerThread.status==WorkerThread.STATUS_TESTING){
                currentPortIndex--;
                if (currentPortIndex<0){
                    currentPortIndex=ports.size()-1;
                    currentAddressIndex--;
                }
            }
        }

        progress=new Progress();
        progress.settings=Main.settings;
        progress.addressIndex=currentAddressIndex;
        progress.portIndex=currentPortIndex;
        progress.status=Progress.STATUS_INPROGRESS;
        progress.results=results.toArray(new Subtask[0]);
        progress.startTime=startTime;
        this.stop();
        status=STATUS_PAUSED;
        Main.mainFrame.updateTitle();
    }

    public void resume(){
        results.clear();
        results.addAll(Arrays.asList(progress.results));
        Main.mainFrame.resultPanel.updateServerList();
        Main.mainFrame.tabbedPane.setTitleAt(2,"Results"+" ("+results.size()+")");
        workerThreads.clear();
        currentAddressIndex=progress.addressIndex;
        currentPortIndex=progress.portIndex;
        for (int i = 0; i < thread; i++) {
            Logger.log("Sniffer","Create thread: "+i);
            WorkerThread workerThread=new WorkerThread(this,proxy,timeout,intervalMin,intervalMax);
            workerThread.start();
            workerThreads.add(workerThread);
        }
        Main.mainFrame.settingsPanel.setEditable(false);
        Main.mainFrame.tabbedPane.setSelectedIndex(1);
        Main.mainFrame.dashboardPanel.start(200);
        Main.mainFrame.dashboardPanel.progressPanel.startTime=progress.startTime;
        Main.mainFrame.dashboardPanel.waterfallPanel.startTime=progress.startTime;
        status=STATUS_RUNNING;
        Main.mainFrame.updateTitle();
    }

    public void stop(){
        this.status=STATUS_STOPPED;
        Main.mainFrame.dashboardPanel.stop();
        Main.mainFrame.settingsPanel.setEditable(true);

        new Thread(() -> {
            for (WorkerThread workerThread : workerThreads) {
                workerThread.stop();
            }
        }).start();
        Main.mainFrame.updateTitle();
        progress.status=Progress.STATUS_FINISHED;
        Main.mainFrame.settingsPanel.pauseAndResume.setEnabled(false);
    }
    @Override
    public synchronized boolean hasNext() {
        if (currentAddressIndex>=addresses.size()){
            return false;
        }
        return true;
    }

    @Override
    public synchronized Subtask next() {
        Subtask subtask=new Subtask(addresses.get(currentAddressIndex),ports.get(currentPortIndex));
        currentPortIndex++;
        if (currentPortIndex>=ports.size()){
            currentPortIndex=0;
            currentAddressIndex++;
        }
        return subtask;
    }

    @Override
    public synchronized void submit(Subtask subtask) {
        Logger.log("Sniffer","Submit: "+subtask.address+":"+subtask.port+" player:"
                +subtask.getMinecraftServer().getOnlinePlayer()+"/"+subtask.getMinecraftServer().getMaxPlayer()
                +" version: "+subtask.getMinecraftServer().getVersionName()+" description: "
                +subtask.getMinecraftServer().getDefaultDescriptionText());
        results.add(subtask);

        Main.mainFrame.resultPanel.updateServerList();

        Main.mainFrame.tabbedPane.setTitleAt(2,"Results("+results.size()+")");
    }

    @Override
    public synchronized void exception(Subtask subtask, Exception e) {
//        System.out.println("Exception: "+subtask.address+":"+subtask.port);
        Logger.log("Sniffer","Exception: "+subtask.address+":"+subtask.port+" "+e.getMessage());
    }

    public void exportProgress(String fileName) throws Exception {
        String json=new Gson().toJson(progress);
        FileIO.write(fileName,toPrettyFormat(json));
    }
    public static String toPrettyFormat(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
}
