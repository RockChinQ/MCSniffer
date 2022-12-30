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

        resultListScrollPane.setBounds(10,10,250,330);
        resultListScrollPane.setBorder(BorderFactory.createEtchedBorder());

        this.add(resultListScrollPane);
        entryPanel.setLayout(null);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                updateServerList();
            }
        });
    }

    public void updateServerList(){
        serverEntries.forEach(resultListScrollPane::remove);
        serverEntries.clear();

        for(int i=0;i<Main.snifferTask.results.size();i++){
            ServerEntry serverEntry=new ServerEntry(Main.snifferTask.results.get(i));
            serverEntries.add(serverEntry);

            serverEntry.setSize(230,70);

            serverEntry.setLocation(1,i*serverEntry.getHeight()+3);
            serverEntry.setBorder(BorderFactory.createEtchedBorder());
            entryPanel.add(serverEntry);
        }

        entryPanel.setSize(new Dimension(225,serverEntries.size()*70+3));
        entryPanel.setPreferredSize(new Dimension(225,serverEntries.size()*70+3));
        resultListScrollPane.repaint();
    }
}
