package ui.settings;

import com.google.gson.Gson;
import core.FileIO;
import core.Main;
import data.Progress;
import process.SnifferTask;
import ui.components.InputAreaField;
import ui.components.InputField;

import javax.swing.*;

public class SettingsPanel extends JPanel {
    InputAreaField addressRange=new InputAreaField("Addresses",350,120,70);
    InputAreaField portRange=new InputAreaField("Ports",350,80,70);

    InputField proxyAddress=new InputField("Proxy URL",350,35,70);
    SpinnerModel threadAmtSpinnerModel = new SpinnerNumberModel(8, // initial value
            0, // min
            65535, // max
            1);// step

    JLabel threadAmtText=new JLabel("Threads");
    JSpinner threadAmtSpinner=new JSpinner(threadAmtSpinnerModel);

    SpinnerModel timeoutSpinnerModel = new SpinnerNumberModel(5000,100,60000,100);

    JLabel timeoutText=new JLabel("Timeout");
    JSpinner timeoutAmtSpinner=new JSpinner(timeoutSpinnerModel);

    JLabel randomIntervalText=new JLabel("Random Interval");

    SpinnerModel randomIntervalMaxSpinnerModel = new SpinnerNumberModel(5000,0,60000,100);

    JSpinner randomIntervalMaxSpinner=new JSpinner(randomIntervalMaxSpinnerModel);

    SpinnerModel randomIntervalMinSpinnerModel = new SpinnerNumberModel(1000,0,60000,100);

    JSpinner randomIntervalMinSpinner=new JSpinner(randomIntervalMinSpinnerModel);

    JButton saveAndStart=new JButton("Save & Start");

    public JButton pauseAndResume=new JButton("Pause");
    JButton export=new JButton("Export Progress");
    JButton load=new JButton("Load Progress");
    public SettingsPanel(){
        this.setLayout(null);

        addressRange.setLocation(20,20);
        addressRange.setValue(Main.settings.addresses);
        this.add(addressRange);

        portRange.setLocation(addressRange.getX(),addressRange.getY()+addressRange.getHeight()+15);
        portRange.setValue(Main.settings.ports);
        this.add(portRange);

        proxyAddress.setLocation(portRange.getX(),portRange.getY()+portRange.getHeight()+15);
        proxyAddress.setValue(Main.settings.proxyURL);
        this.add(proxyAddress);

        threadAmtText.setBounds(addressRange.getX()+addressRange.getWidth()+20,addressRange.getY(),55,25);
        this.add(threadAmtText);

        threadAmtSpinner.setBounds(threadAmtText.getX()+threadAmtText.getWidth(),threadAmtText.getY(),70,25);
        threadAmtSpinner.setValue(Main.settings.thread);
        this.add(threadAmtSpinner);

        timeoutText.setBounds(threadAmtText.getX(),threadAmtText.getY()+threadAmtText.getHeight()+10,55,25);
        this.add(timeoutText);

        timeoutAmtSpinner.setBounds(timeoutText.getX()+timeoutText.getWidth(), timeoutText.getY(), 70,25);
        timeoutAmtSpinner.setValue(Main.settings.timeout);
        this.add(timeoutAmtSpinner);

        randomIntervalText.setBounds(timeoutText.getX(),timeoutText.getY()+timeoutText.getHeight()+7,100,25);
        this.add(randomIntervalText);

        randomIntervalMinSpinner.setBounds(randomIntervalText.getX(),randomIntervalText.getY()+randomIntervalText.getHeight()+5,70,25);
        randomIntervalMinSpinner.setValue(Main.settings.intervalMin);
        this.add(randomIntervalMinSpinner);

        randomIntervalMaxSpinner.setBounds(randomIntervalMinSpinner.getX()+randomIntervalMinSpinner.getWidth()+10,randomIntervalMinSpinner.getY(),70,25);
        randomIntervalMaxSpinner.setValue(Main.settings.intervalMax);
        this.add(randomIntervalMaxSpinner);

        saveAndStart.setBounds(timeoutText.getX(),randomIntervalMinSpinner.getY()+randomIntervalMinSpinner.getHeight()+25,90,30);
        saveAndStart.setBorder(BorderFactory.createEtchedBorder());

        saveAndStart.addActionListener(e->{
            if (Main.snifferTask!=null) {
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new scan? Current progress will be erased.", "New Scan", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            Main.snifferTask=new SnifferTask(addressRange.getValue(), portRange.getValue(),
                    proxyAddress.getValue().trim(),
                    (int)threadAmtSpinnerModel.getValue(),
                    (int)timeoutSpinnerModel.getValue(),
                    (int)randomIntervalMinSpinnerModel.getValue(),
                    (int)randomIntervalMaxSpinnerModel.getValue());
            Main.snifferTask.start();

            exportSettings();

            pauseAndResume.setEnabled(true);
            pauseAndResume.setText("Pause");
        });
        this.add(saveAndStart);

        pauseAndResume.setBounds(saveAndStart.getX()+saveAndStart.getWidth()+8,saveAndStart.getY(),80,30);
        pauseAndResume.setBorder(BorderFactory.createEtchedBorder());
        pauseAndResume.setEnabled(false);
        pauseAndResume.addActionListener(e->{
            if (pauseAndResume.getText().equals("Pause")) {
                Main.snifferTask.pause();
                pauseAndResume.setText("Resume");
            }else {
                exportSettings();
                if (Main.snifferTask!=null&&Main.snifferTask.status==SnifferTask.STATUS_STOPPED){
                    JOptionPane.showMessageDialog(null,"Task already finished");
                    return;
                }
                Main.snifferTask.progress.settings=Main.settings.clone();
                Main.snifferTask.init(Main.settings.addresses,Main.settings.ports,Main.settings.proxyURL,Main.settings.thread,Main.settings.timeout,Main.settings.intervalMin,Main.settings.intervalMax);
                Main.snifferTask.resume();
                pauseAndResume.setText("Pause");
            }
        });
        this.add(pauseAndResume);

        export.setBounds(saveAndStart.getX(),pauseAndResume.getY()+pauseAndResume.getHeight()+15,120,30);
        export.setBorder(BorderFactory.createEtchedBorder());
        export.addActionListener(e -> {
            Main.snifferTask.pause();
            pauseAndResume.setText("Resume");
            String fileName = JOptionPane.showInputDialog("Enter file name:");
            if (fileName==null) {
                return;
            }
            if (!fileName.endsWith(".json")) {
                fileName+=".json";
            }
            try {
                Main.snifferTask.exportProgress(fileName);
                JOptionPane.showMessageDialog(null,"Progress exported to "+fileName);
            } catch (Exception ioException) {
                ioException.printStackTrace();
                JOptionPane.showMessageDialog(null,"Error exporting progress: "+ioException.getMessage());
            }
        });
        this.add(export);

        load.setBounds(export.getX(),export.getY()+export.getHeight()+10,120,30);
        load.setBorder(BorderFactory.createEtchedBorder());
        load.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog("Enter file name:");
            if (fileName==null) {
                return;
            }
            if (!fileName.endsWith(".json")) {
                fileName+=".json";
            }
            try {
                pauseAndResume.setEnabled(true);
                pauseAndResume.setText("Resume");
                String jsonStr= FileIO.read(fileName);
                Progress progress=new Gson().fromJson(jsonStr,Progress.class);
                Main.snifferTask=new SnifferTask(progress);
                Main.mainFrame.dashboardPanel.progressPanel.update();

                addressRange.setValue(progress.settings.addresses);
                portRange.setValue(progress.settings.ports);
                proxyAddress.setValue(progress.settings.proxyURL);
                threadAmtSpinner.setValue(progress.settings.thread);
                timeoutAmtSpinner.setValue(progress.settings.timeout);
                randomIntervalMinSpinner.setValue(progress.settings.intervalMin);
                randomIntervalMaxSpinner.setValue(progress.settings.intervalMax);

                exportSettings();
            } catch (Exception ioException) {
                ioException.printStackTrace();
                JOptionPane.showMessageDialog(null,"Error loading progress: "+ioException.getMessage());
            }
        });
        this.add(load);
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

    public void exportSettings(){

        Main.settings.addresses=addressRange.getValue();
        Main.settings.ports=portRange.getValue();
        Main.settings.proxyURL=proxyAddress.getValue().trim();
        Main.settings.thread=(int)threadAmtSpinnerModel.getValue();
        Main.settings.timeout=(int)timeoutSpinnerModel.getValue();
        Main.settings.intervalMin=(int)randomIntervalMinSpinnerModel.getValue();
        Main.settings.intervalMax=(int)randomIntervalMaxSpinnerModel.getValue();
        try {
            Main.settings.dump();
        } catch (Exception ignored) {
        }
    }
}
