package ui.mapping;

import core.Logger;
import core.Main;
import mapping.MappingRule;
import ui.components.InputField;

import javax.swing.*;
import java.io.IOException;

public class MappingPanel extends JPanel {
    public JLabel localPortLabel=new JLabel("Local Port");
    public SpinnerNumberModel localPortModel=new SpinnerNumberModel(25565,1,65535,1);
    public JSpinner localPortSpinner=new JSpinner(localPortModel);

    public InputField remoteHostField=new InputField("Remote Host",260,35,80);

    public JLabel remotePortLabel=new JLabel("Remote Port");
    public SpinnerNumberModel remotePortModel=new SpinnerNumberModel(25565,1,65535,1);
    public JSpinner remotePortSpinner=new JSpinner(remotePortModel);

    public InputField proxyURLField=new InputField("Proxy URL",260,35,80);

    public JButton startAndStopButton=new JButton("Start");

    public MappingPanel(){
        this.setLayout(null);

        localPortLabel.setBounds(10,10,80,30);
        this.add(localPortLabel);

        localPortSpinner.setBounds(localPortLabel.getX()+localPortLabel.getWidth(),10,100,30);
        localPortSpinner.setValue(Main.mapping.localPort);
        this.add(localPortSpinner);

        remoteHostField.setLocation(10,50);
        remoteHostField.setValue(Main.mapping.remoteHost);
        //粘贴到remoteHostField.input的时候检查是否包含端口号，若包含，则将端口号分离出来，设置到remotePortSpinner
        remoteHostField.input.addCaretListener(e -> {
            new Thread(()->{
                String text=remoteHostField.input.getText();
                if(text.contains(":")){
                    String[] split=text.split(":");
                    remoteHostField.input.setText(split[0]);
                    remotePortSpinner.setValue(Integer.parseInt(split[1]));
                }
            }).start();
        });
        this.add(remoteHostField);

        remotePortLabel.setBounds(remoteHostField.getX()+remoteHostField.getWidth()+10,remoteHostField.getY(),80,30);
        this.add(remotePortLabel);

        remotePortSpinner.setBounds(remotePortLabel.getX()+remotePortLabel.getWidth(),remotePortLabel.getY(),70,30);
        remotePortSpinner.setValue(Main.mapping.remotePort);
        this.add(remotePortSpinner);

        proxyURLField.setLocation(10,remotePortLabel.getY()+remotePortLabel.getHeight()+10);
        proxyURLField.setValue(Main.mapping.proxyURL);
        this.add(proxyURLField);

        startAndStopButton.setBounds(proxyURLField.getX()+proxyURLField.getWidth()+40,proxyURLField.getY(),80,30);
        this.add(startAndStopButton);
        startAndStopButton.setBorder(BorderFactory.createEtchedBorder());
        startAndStopButton.addActionListener(e -> {
            if (startAndStopButton.getText().equals("Start")) {
                if (Main.mappingRule!=null){
                    Main.mappingRule.close();
                }
                boolean success=false;
                try {
                    Main.mappingRule=new MappingRule((int)localPortSpinner.getValue(),remoteHostField.getValue(),
                            (int)remotePortSpinner.getValue(),proxyURLField.getValue(),null);
                    Main.mappingRule.start();
                    startAndStopButton.setText("Stop");
                    this.setEditable(false);
                    success=true;

                    Main.mapping.localPort=(int)localPortSpinner.getValue();
                    Main.mapping.remoteHost=remoteHostField.getValue();
                    Main.mapping.remotePort=(int)remotePortSpinner.getValue();
                    Main.mapping.proxyURL=proxyURLField.getValue();
                    Main.mapping.dump();
                } catch (Exception ex) {
                    System.out.println("Failed to start mapping rule");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Failed to start mapping rule: "+ Logger.getErrorInfo(ex));
                }finally {
                    if (!success){
                        JOptionPane.showMessageDialog(null,"Failed to start mapping rule.Target remote server may be offline.");
                    }
                }

            } else {
                Main.mappingRule.close();
                startAndStopButton.setText("Start");
                this.setEditable(true);
            }
        });
    }

    public void setEditable(boolean b){
        localPortSpinner.setEnabled(b);
        remoteHostField.input.setEditable(b);
        remotePortSpinner.setEnabled(b);
        proxyURLField.input.setEditable(b);
    }
}
