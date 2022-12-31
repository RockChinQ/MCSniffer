package ui.settings;

import core.Main;
import process.SnifferTask;
import ui.components.InputAreaField;
import ui.components.InputField;

import javax.swing.*;

public class SettingsPanel extends JPanel {
    InputAreaField addressRange=new InputAreaField("Addresses",350,120,70);
    InputAreaField portRange=new InputAreaField("Ports",350,80,70);

    InputField proxyAddress=new InputField("Proxy URL",350,30,70);
    SpinnerModel threadAmtSpinnerModel = new SpinnerNumberModel(8, // initial value
            0, // min
            65535, // max
            1);// step

    JLabel threadAmtText=new JLabel("Threads");
    JSpinner threadAmtSpinner=new JSpinner(threadAmtSpinnerModel);

    SpinnerModel timeoutSpinnerModel = new SpinnerNumberModel(5,1,60,1);

    JLabel timeoutText=new JLabel("Timeout");
    JSpinner timeoutAmtSpinner=new JSpinner(timeoutSpinnerModel);

    JLabel randomIntervalText=new JLabel("Random Interval");

    SpinnerModel randomIntervalMaxSpinnerModel = new SpinnerNumberModel(5,0,60,1);

    JSpinner randomIntervalMaxSpinner=new JSpinner(randomIntervalMaxSpinnerModel);

    SpinnerModel randomIntervalMinSpinnerModel = new SpinnerNumberModel(1,0,60,1);

    JSpinner randomIntervalMinSpinner=new JSpinner(randomIntervalMinSpinnerModel);

    JButton saveAndStart=new JButton("Save & Start");
    public SettingsPanel(){
        this.setLayout(null);

        addressRange.setLocation(20,20);
        this.add(addressRange);

        portRange.setLocation(addressRange.getX(),addressRange.getY()+addressRange.getHeight()+15);
        this.add(portRange);

        proxyAddress.setLocation(portRange.getX(),portRange.getY()+portRange.getHeight()+15);
        this.add(proxyAddress);

        threadAmtText.setBounds(addressRange.getX()+addressRange.getWidth()+20,addressRange.getY(),55,30);
        this.add(threadAmtText);

        threadAmtSpinner.setBounds(threadAmtText.getX()+threadAmtText.getWidth(),threadAmtText.getY(),70,30);
        this.add(threadAmtSpinner);

        timeoutText.setBounds(threadAmtText.getX(),threadAmtText.getY()+threadAmtText.getHeight()+10,55,30);
        this.add(timeoutText);

        timeoutAmtSpinner.setBounds(timeoutText.getX()+timeoutText.getWidth(), timeoutText.getY(), 70,30);
        this.add(timeoutAmtSpinner);

        randomIntervalText.setBounds(timeoutText.getX(),timeoutText.getY()+timeoutText.getHeight()+10,100,30);
        this.add(randomIntervalText);

        randomIntervalMinSpinner.setBounds(randomIntervalText.getX(),randomIntervalText.getY()+randomIntervalText.getHeight()+5,70,30);
        this.add(randomIntervalMinSpinner);

        randomIntervalMaxSpinner.setBounds(randomIntervalMinSpinner.getX()+randomIntervalMinSpinner.getWidth()+10,randomIntervalMinSpinner.getY(),70,30);
        this.add(randomIntervalMaxSpinner);

        saveAndStart.setBounds(timeoutText.getX(),portRange.getY()+portRange.getHeight()-30,120,30);
        saveAndStart.setBorder(BorderFactory.createEtchedBorder());

        saveAndStart.addActionListener(e->{
            Main.snifferTask=new SnifferTask(addressRange.getValue(), portRange.getValue(),
                    proxyAddress.getValue().trim(),
                    (int)threadAmtSpinnerModel.getValue(),
                    (int)timeoutSpinnerModel.getValue()*1000,
                    (int)randomIntervalMinSpinnerModel.getValue()*1000,
                    (int)randomIntervalMaxSpinnerModel.getValue()*1000);
            Main.mainFrame.settingsPanel.setEditable(false);
            Main.mainFrame.tabbedPane.setSelectedIndex(1);
            Main.snifferTask.start();
            Main.mainFrame.dashboardPanel.start(200);
        });
        this.add(saveAndStart);
    }

    public void setEditable(boolean b){
        this.addressRange.input.setEditable(b);
        this.portRange.input.setEditable(b);

        this.timeoutAmtSpinner.setEnabled(b);
        this.threadAmtSpinner.setEnabled(b);

        this.randomIntervalMaxSpinner.setEnabled(b);
        this.randomIntervalMinSpinner.setEnabled(b);

        saveAndStart.setEnabled(b);
    }
}
