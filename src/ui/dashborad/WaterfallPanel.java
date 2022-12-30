package ui.dashborad;

import core.Main;
import process.WorkerThread;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

public class WaterfallPanel extends JPanel {
    long startTime;

    long scanInterval=250;
    long panelDuration=60*1000;

    int WORK_COLOR=0xA8BBF2;
    Timer timer;

    LinkedHashMap<Long,ArrayList<Integer>> history=new LinkedHashMap<>();

    int currentPage=0;
    int labelWidth=30;
    int legendWidth=80;
    int scaleHeight=30;
    public WaterfallPanel() {
        this.setLayout(null);
    }

    public void start(long scanInterval){
        history.clear();
        currentPage=0;
        this.startTime=System.currentTimeMillis();
        this.scanInterval=scanInterval;
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTime=System.currentTimeMillis();
                if((currentTime-startTime)/panelDuration>currentPage){
                    currentPage++;
                    history.clear();
                }

                ArrayList<WorkerThread> workerThreads= Main.snifferTask.workerThreads;
                //绘制当前的
                ArrayList<Integer> current=new ArrayList<>();
//        System.out.println(x);
                for (int i=0;i<workerThreads.size();i++){
                    WorkerThread workerThread=workerThreads.get(i);

                    switch (workerThread.status){
                        case WorkerThread.STATUS_SLEEPING:
                            current.add(0xdddddd);
                            break;
                        case WorkerThread.STATUS_TESTING:
                            current.add(WORK_COLOR);
                            break;
                        case WorkerThread.STATUS_FINISHED:
                            current.add(0xff0000);
                            break;
                    }
                }
                history.put(currentTime,current);
                repaint();
            }
        }, 0, scanInterval);
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int panelWidth=this.getWidth()-labelWidth-legendWidth;
        int panelHeight=this.getHeight()-scaleHeight;

        ArrayList<WorkerThread> workerThreads= Main.snifferTask.workerThreads;
        //绘制标签
        g.setColor(Color.BLACK);
        for (int i = 0; i < workerThreads.size(); i++) {
            g.drawString("#"+i,5,(i+1)*panelHeight/workerThreads.size());
        }


        //绘制本页历史的
        for (Long aLong : history.keySet()) {
            ArrayList<Integer> integers = history.get(aLong);
            int x=(int) ((aLong-startTime)%panelDuration/(double)panelDuration*panelWidth)+labelWidth;
            for (int i = 0; i < integers.size(); i++) {
                int y=i*panelHeight/workerThreads.size()+4;
                int height=(int)((double)panelHeight/workerThreads.size());
                g.setColor(new Color(integers.get(i)));
                g.fillRect(x,y,1,height);
            }
        }

        //行分割线
        if (workerThreads.size()<panelHeight/2) {
            g.setColor(Color.lightGray);
            for (int i = 0; i < workerThreads.size() + 1; i++) {
                g.drawLine(labelWidth, i * panelHeight / workerThreads.size() + 4, panelWidth + labelWidth, i * panelHeight / workerThreads.size() + 4);
            }
        }

        //标尺
        g.setColor(Color.BLACK);
        //计算本页起点时间戳
        long currentPageStartTime=(startTime+currentPage*panelDuration);
        for(long i=currentPageStartTime;i<currentPageStartTime+panelDuration;i++){
            if ((i-startTime)%1000==0){
                int x=(int) ((i-currentPageStartTime)%panelDuration/(double)panelDuration*panelWidth)+labelWidth;
                g.drawLine(x,panelHeight+5,x,panelHeight+8);
            }

            if ((i-startTime)%10000==0){
                int x=(int) ((i-currentPageStartTime)%panelDuration/(double)panelDuration*panelWidth)+labelWidth;
                g.drawLine(x,panelHeight+5,x,panelHeight+12);
                String timeString=String.format("%02d:%02d",(i-startTime)/1000/60,(i-startTime)/1000%60);
                g.drawString(timeString,x,panelHeight+25);
            }
        }

        //飞棱柱
        g.setColor(Color.GREEN);
        g.drawLine((int) ((System.currentTimeMillis()-startTime)%panelDuration/(double)panelDuration*panelWidth)+labelWidth,
                2,
                (int) ((System.currentTimeMillis()-startTime)%panelDuration/(double)panelDuration*panelWidth)+labelWidth,panelHeight+5);


        //图例
        g.setColor(new Color(WORK_COLOR));
        g.fillRect(getWidth()-legendWidth+8, 5, 15, 15);
        g.setColor(Color.BLACK);
        g.drawString("Testing",getWidth()-legendWidth+25, 20);

        g.setColor(new Color(0xdddddd));
        g.fillRect(getWidth()-legendWidth+8, 25, 15, 15);
        g.setColor(Color.BLACK);
        g.drawString("Sleeping",getWidth()-legendWidth+25, 40);

        g.setColor(new Color(0xff0000));
        g.fillRect(getWidth()-legendWidth+8, 45, 15, 15);
        g.setColor(Color.BLACK);
        g.drawString("Finished",getWidth()-legendWidth+25, 60);

        //border
        g.setColor(Color.GRAY);
        g.drawRect(0,0,getWidth()-1,getHeight()-1);
    }
}
