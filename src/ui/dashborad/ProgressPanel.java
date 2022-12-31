package ui.dashborad;

import core.Main;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class ProgressPanel extends JPanel {
    JProgressBar totalProgressBar =new JProgressBar();

    JLabel currentAddress=new JLabel("Addr.");

    JProgressBar currentProgressBar=new JProgressBar();

    JLabel currentPort=new JLabel("Port");

    JLabel spentTime=new JLabel("Time");

    JLabel estimatedTime=new JLabel("Estimate");

    JLabel foundCount=new JLabel("Found");

    long scanInterval=250;

    public long startTime;
    Timer timer;
    public ProgressPanel(){
        this.setLayout(null);

        totalProgressBar.setMinimum(0);
        totalProgressBar.setMaximum(100);
        totalProgressBar.setBounds(5,5,280,20);
        totalProgressBar.setStringPainted(true);
        this.add(totalProgressBar);

        currentAddress.setBounds(totalProgressBar.getX()+totalProgressBar.getWidth()+10,5,250,20);
        this.add(currentAddress);

        currentProgressBar.setMinimum(0);
        currentProgressBar.setMaximum(100);
        currentProgressBar.setStringPainted(true);
        currentProgressBar.setBounds(totalProgressBar.getX(),totalProgressBar.getY()+totalProgressBar.getHeight()+10,totalProgressBar.getWidth(),20);
        this.add(currentProgressBar);

        currentPort.setBounds(currentProgressBar.getX()+currentProgressBar.getWidth()+10,currentProgressBar.getY(),250,20);
        this.add(currentPort);

        spentTime.setBounds(currentProgressBar.getX(),currentProgressBar.getY()+currentProgressBar.getHeight()+10,250,20);
        this.add(spentTime);

        estimatedTime.setBounds(spentTime.getX()+spentTime.getWidth()+10,spentTime.getY(),250,20);
        this.add(estimatedTime);

        foundCount.setBounds(spentTime.getX(),spentTime.getY()+spentTime.getHeight()+10,250,20);
        this.add(foundCount);
    }

    public void start(long scanInterval){
        this.scanInterval=scanInterval;
        this.startTime=System.currentTimeMillis();
        totalProgressBar.setValue(0);
        currentProgressBar.setValue(0);

        timer=new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        },0,scanInterval);
    }

    public void update(){

        try {
            int currentProgress=(int)(Main.snifferTask.currentPortIndex/(double)Main.snifferTask.ports.size()*100);
            currentProgressBar.setValue(currentProgress);

            int totalProgress=(int)(Main.snifferTask.currentAddressIndex/(double)Main.snifferTask.addresses.size()*100+currentProgress/(double)Main.snifferTask.addresses.size());
            totalProgressBar.setValue(totalProgress);

            if(Main.snifferTask.currentAddressIndex<Main.snifferTask.addresses.size()){
                currentAddress.setText((Main.snifferTask.currentAddressIndex) + "/" + Main.snifferTask.addresses.size() + " Addr:" + Main.snifferTask.addresses.get(Main.snifferTask.currentAddressIndex));
            }else {
                currentAddress.setText((Main.snifferTask.currentAddressIndex) + "/" + Main.snifferTask.addresses.size() + " Addr:Done");
            }
            currentPort.setText((Main.snifferTask.currentPortIndex) + "/" + Main.snifferTask.ports.size() + " Port:" + Main.snifferTask.ports.get(Main.snifferTask.currentPortIndex));

            long currentTime=System.currentTimeMillis();
            String spentTimeHHMMSS=String.format("%02d:%02d:%02d", (currentTime-startTime)/1000/60/60,(currentTime-startTime)/1000/60%60,(currentTime-startTime)/1000%60);
            spentTime.setText("Time:"+spentTimeHHMMSS);

            long est= (long) (((double)(Main.snifferTask.addresses.size()*Main.snifferTask.ports.size())/(Main.snifferTask.currentAddressIndex*Main.snifferTask.ports.size()+Main.snifferTask.currentPortIndex))*(currentTime-startTime));
            String estHHMMSS=String.format("%02d:%02d:%02d", est/1000/60/60,est/1000/60%60,est/1000%60);
            estimatedTime.setText("Estimate:"+estHHMMSS);

            foundCount.setText("Found:"+Main.snifferTask.results.size());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        timer.cancel();
        update();
    }
}
