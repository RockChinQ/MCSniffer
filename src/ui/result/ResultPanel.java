package ui.result;

import core.Main;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class ResultPanel extends JPanel {
    public JScrollPane resultListScrollPane=new JScrollPane();

    ArrayList<ServerEntry> serverEntries=new ArrayList<>();

    public ResultPanel(){
        this.setLayout(null);

        resultListScrollPane.setBounds(10,10,250,350);
        resultListScrollPane.setBorder(BorderFactory.createEtchedBorder());
        resultListScrollPane.setLayout(null);

        this.add(resultListScrollPane);

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

        int scrollY=resultListScrollPane.getVerticalScrollBar().getValue();

        for(int i=0;i<Main.snifferTask.results.size();i++){
            ServerEntry serverEntry=new ServerEntry(Main.snifferTask.results.get(i));
            serverEntries.add(serverEntry);

            serverEntry.setSize(230,70);

            serverEntry.setLocation(3,i*serverEntry.getHeight()+3);
            serverEntry.setBorder(BorderFactory.createEtchedBorder());
            resultListScrollPane.add(serverEntry);
        }

        resultListScrollPane.getVerticalScrollBar().setValue(scrollY);
        resultListScrollPane.repaint();
    }
}
