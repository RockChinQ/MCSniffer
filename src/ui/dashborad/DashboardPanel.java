package ui.dashborad;

import javax.swing.*;

public class DashboardPanel extends JPanel {

    public JTabbedPane progressTabbedPane=new JTabbedPane();
    public ProgressPanel progressPanel;
    public WaterfallPanel waterfallPanel;

    public LogPanel logPanel=new LogPanel();
    public DashboardPanel() {
        this.setLayout(null);

        progressTabbedPane.setBounds(0,0,585,160);
        progressTabbedPane.setTabPlacement(JTabbedPane.LEFT);
        this.add(progressTabbedPane);

        progressPanel=new ProgressPanel();
        progressTabbedPane.add("Progress",progressPanel);

        waterfallPanel=new WaterfallPanel();
        progressTabbedPane.add("Waterfall",waterfallPanel);

        logPanel.setBounds(5,progressTabbedPane.getY()+progressTabbedPane.getHeight()+10,575,220);
        this.add(logPanel);

        logPanel.setBorder(BorderFactory.createTitledBorder("Log"));
    }

    public void start(long scanInterval){
        progressPanel.start(scanInterval);
        waterfallPanel.start(scanInterval);
    }

    public void stop(){
        progressPanel.stop();
        waterfallPanel.stop();
    }
}
