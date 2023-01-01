package ui.result;

import core.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.Proxy;
import java.util.ArrayList;

public class ResultPanel extends JPanel {

    public JPanel entryPanel=new JPanel();
    public JScrollPane resultListScrollPane=new JScrollPane(entryPanel);
    ArrayList<ServerEntry> serverEntries=new ArrayList<>();

    JButton refresh=new JButton("Refresh");
    public ResultPanel(){
        this.setLayout(null);

        resultListScrollPane.setBounds(10,10,570,330);
        resultListScrollPane.setBorder(BorderFactory.createEtchedBorder());

        this.add(resultListScrollPane);
        entryPanel.setLayout(null);

        refresh.setBounds(resultListScrollPane.getX(),resultListScrollPane.getY()+resultListScrollPane.getHeight()+5,100,30);
        refresh.setBorder(BorderFactory.createEtchedBorder());
        refresh.addActionListener(e -> {
            refreshAll();
        });
        this.add(refresh);
    }

    public void updateServerList(){
        serverEntries.forEach(entryPanel::remove);
        serverEntries.clear();

        for(int i=0;Main.snifferTask!=null&&Main.snifferTask.results!=null&&i<Main.snifferTask.results.size();i++){
            ServerEntry serverEntry=new ServerEntry(Main.snifferTask.results.get(i));
            serverEntries.add(serverEntry);
            System.out.println("Adding server entry "+i);

            serverEntry.setSize(550,70);

            serverEntry.setLocation(1,i*serverEntry.getHeight()+3);
            serverEntry.setBorder(BorderFactory.createEtchedBorder());
            entryPanel.add(serverEntry);
        }

        entryPanel.setSize(new Dimension(535,serverEntries.size()*70+3));
        entryPanel.setPreferredSize(new Dimension(535,serverEntries.size()*70+3));
        resultListScrollPane.repaint();
    }

    public void refreshAll(){
        new Thread(()->{
            ResultPanel.this.refresh.setEnabled(false);
            //从SettingsPanel获取最新代理地址
            String proxyURL= Main.mainFrame.settingsPanel.proxyAddress.getValue();
            Proxy proxy;
            if (proxyURL!=null&&!proxyURL.trim().equals("")) {
                proxy = new Proxy(Proxy.Type.HTTP, java.net.InetSocketAddress.createUnresolved(proxyURL.split(":")[0], Integer.parseInt(proxyURL.split(":")[1])));
            } else {
                proxy = null;
            }
            serverEntries.forEach(serverEntry -> {
                serverEntry.statusLabel.setText("Unknown");
                serverEntry.statusLabel.setForeground(Color.GRAY);
                serverEntry.status=ServerEntry.STATUS_UNKNOWN;
            });
            for (ServerEntry serverEntry : serverEntries) {
                try{
                    Thread.sleep((long) (Math.random()*200));
                }catch (Exception ignored){
                }
                serverEntry.refresh(proxy);
                ResultPanel.this.refresh.setText("Refresh ("+serverEntries.indexOf(serverEntry)+"/"+serverEntries.size()+")");
            }
            ResultPanel.this.refresh.setText("Refresh");
            ResultPanel.this.refresh.setEnabled(true);
        }).start();
    }
}
