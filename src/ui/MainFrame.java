package ui;

import core.Main;
import process.SnifferTask;
import ui.dashborad.DashboardPanel;
import ui.mapping.MappingPanel;
import ui.result.ResultPanel;
import ui.settings.SettingsPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    public JTabbedPane tabbedPane;

    public SettingsPanel settingsPanel=new SettingsPanel();
    public DashboardPanel dashboardPanel=new DashboardPanel();
    public ResultPanel resultPanel=new ResultPanel();
    public MappingPanel mappingPanel=new MappingPanel();
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
        tabbedPane.addChangeListener(e -> {
//            if (tabbedPane.getSelectedIndex()==2){
//                resultPanel.updateServerList();
//            }
        });
        tabbedPane.add("Settings", settingsPanel);
        tabbedPane.add("Dashboard", dashboardPanel);
        tabbedPane.add("Results", resultPanel);
        tabbedPane.add("Mapping",mappingPanel);

        this.setResizable(false);
        this.setVisible(true);

        DialogManager.register(this);
        DialogManager.setCenter(this);
    }

    public void updateTitle(){
        this.setTitle("MCSniffer "+ (Main.snifferTask.status==SnifferTask.STATUS_RUNNING?"- Running":""));
    }
}
