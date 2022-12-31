package process;

import core.Logger;
import core.Main;

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

    public int status=0;
    public static final int STATUS_STOPPED=0,STATUS_RUNNING=1;
    public ArrayList<Subtask> results=new ArrayList<>();
    public SnifferTask(String address,String port,String proxyURL, int thread,int timeout,int intervalMin,int intervalMax) {
        this.thread=thread;
        this.timeout=timeout;
        this.intervalMin= (int) (intervalMin);
        this.intervalMax= (int) (intervalMax);
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
        Main.mainFrame.tabbedPane.setTitleAt(2,"Results");
        workerThreads.clear();

        for (int i = 0; i < thread; i++) {
            WorkerThread workerThread=new WorkerThread(this,proxy,timeout,intervalMin,intervalMax);
            workerThread.start();
            workerThreads.add(workerThread);
        }
        status=STATUS_RUNNING;
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
}
