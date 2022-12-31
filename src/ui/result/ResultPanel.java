package ui.result;

import core.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

    }

    public void updateServerList(){
        serverEntries.forEach(resultListScrollPane::remove);
        serverEntries.clear();

        for(int i=0;Main.snifferTask!=null&&Main.snifferTask.results!=null&&i<Main.snifferTask.results.size();i++){
            ServerEntry serverEntry=new ServerEntry(Main.snifferTask.results.get(i));
            serverEntries.add(serverEntry);

            serverEntry.setSize(550,70);

            serverEntry.setLocation(1,i*serverEntry.getHeight()+3);
            serverEntry.setBorder(BorderFactory.createEtchedBorder());
            entryPanel.add(serverEntry);
        }

        entryPanel.setSize(new Dimension(535,serverEntries.size()*70+3));
        entryPanel.setPreferredSize(new Dimension(535,serverEntries.size()*70+3));
        resultListScrollPane.repaint();
    }
}
