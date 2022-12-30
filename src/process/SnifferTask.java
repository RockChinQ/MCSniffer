package process;

import java.util.ArrayList;
import java.util.Arrays;

public class SnifferTask implements ISubtaskIterator {

    int thread;
    int timeout;

    int intervalMin,intervalMax;

    ArrayList<String> addresses;
    ArrayList<Integer> ports;

    int currentAddressIndex=0;
    int currentPortIndex=0;

    public ArrayList<WorkerThread> workerThreads=new ArrayList<>();

    ArrayList<Subtask> results=new ArrayList<>();
    public SnifferTask(String address,String port, int thread,int timeout,int intervalMin,int intervalMax) {
        this.thread=thread;
        this.timeout=timeout;
        this.intervalMin= (int) (intervalMin);
        this.intervalMax= (int) (intervalMax);
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
        workerThreads.clear();

        for (int i = 0; i < thread; i++) {
            WorkerThread workerThread=new WorkerThread(this,timeout,intervalMin,intervalMax);
            workerThread.start();
            workerThreads.add(workerThread);
        }
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
        System.out.println("Submit: "+subtask.address+":"+subtask.port+" player:"+subtask.onlinePlayers+"/"+subtask.maxPlayers+" version: "+subtask.versionName+" description: "+subtask.description);
        results.add(subtask);
    }

    @Override
    public synchronized void exception(Subtask subtask, Exception e) {
//        System.out.println("Exception: "+subtask.address+":"+subtask.port);
    }
}
