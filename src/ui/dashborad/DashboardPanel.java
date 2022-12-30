package ui.dashborad;

import javax.swing.*;

public class DashboardPanel extends JPanel {

    public JTabbedPane progressTabbedPane=new JTabbedPane();
    public WaterfallPanel waterfallPanel;
    public DashboardPanel() {
        this.setLayout(null);

        progressTabbedPane.setBounds(0,0,585,160);
        progressTabbedPane.setTabPlacement(JTabbedPane.LEFT);
        this.add(progressTabbedPane);

        waterfallPanel=new WaterfallPanel();
        progressTabbedPane.add("Waterfall",waterfallPanel);
    }
}
