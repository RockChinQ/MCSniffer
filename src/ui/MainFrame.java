package ui;

import ui.dashborad.DashboardPanel;
import ui.settings.SettingsPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    public JTabbedPane tabbedPane;

    public SettingsPanel settingsPanel=new SettingsPanel();
    public DashboardPanel dashboardPanel=new DashboardPanel();
    public MainFrame(){
        this.setTitle("MCSniffer");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if(javax.swing.JOptionPane.showConfirmDialog(null,"Confirm exiting?")==JOptionPane.OK_OPTION){
                    System.exit(0);
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if(javax.swing.JOptionPane.showConfirmDialog(null,"Confirm exiting?")==JOptionPane.OK_OPTION){
                    System.exit(0);
                }
            }
        });

        this.setSize(600,450);

        tabbedPane = new JTabbedPane();
        this.add(tabbedPane);
        tabbedPane.add("Settings", settingsPanel);
        tabbedPane.add("Dashboard", dashboardPanel);

        this.setResizable(false);
        this.setVisible(true);

        DialogManager.register(this);
        DialogManager.setCenter(this);
    }
}
