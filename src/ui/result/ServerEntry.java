package ui.result;

import com.google.gson.Gson;
import core.ClipBoard;
import process.Subtask;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ServerEntry extends JPanel {
    Subtask subtask;

    JLabel faviconLabel=new JLabel(){
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if(subtask.getFavicon()!=null){
                g.drawImage(subtask.getFavicon(), 0,0,getWidth(),getHeight(),null);
            }else {
                g.setColor(Color.WHITE);
                g.fillRect(0,0,getWidth(),getHeight());
                g.setColor(Color.blue);
                g.drawString("No",2,12);
                g.drawString("Favicon",2,24);
            }
        }
    };
    JLabel nameLabel=new JLabel();
    JLabel descriptionLabel=new JLabel();
    JLabel playersLabel=new JLabel();
    JLabel versionLabel=new JLabel();

    JLabel modLabel=new JLabel();

    JButton copyAddressPortButton=new JButton("Copy");
    public ServerEntry(Subtask subtask){
        this.subtask=subtask;

        this.setLayout(null);

        faviconLabel.setBounds(5,5,30,30);
        this.add(faviconLabel);

        nameLabel.setBounds(40,5,140,15);
        nameLabel.setText(subtask.getAddress()+":"+subtask.getPort());
        nameLabel.setToolTipText(subtask.getAddress()+":"+subtask.getPort());
        this.add(nameLabel);

        descriptionLabel.setBounds(40,20,140,15);
        descriptionLabel.setText(subtask.getDescription());
        descriptionLabel.setToolTipText(subtask.getDescription());
        this.add(descriptionLabel);

        playersLabel.setBounds(40,35,140,15);
        playersLabel.setText("Players: "+subtask.getOnlinePlayers()+"/"+subtask.getMaxPlayers());
        playersLabel.setToolTipText(subtask.getPlayerList().toString());
        playersLabel.setForeground(Color.RED);
        this.add(playersLabel);

        versionLabel.setBounds(40,50,140,15);
        versionLabel.setText(subtask.getVersionName());
        versionLabel.setForeground(Color.blue);
        this.add(versionLabel);

        copyAddressPortButton.setBounds(180,5,40,25);
        copyAddressPortButton.setBorder(BorderFactory.createEtchedBorder());
        copyAddressPortButton.addActionListener(e -> {
            ClipBoard.dumpToClipBoard(subtask.getAddress()+":"+subtask.getPort());
        });
        this.add(copyAddressPortButton);

        modLabel.setBounds(150,50,120,15);
        modLabel.setText("mod detected");
        modLabel.setForeground(Color.RED);
        if (subtask.getModInfo()!=null||subtask.getModPackData()!=null){
            String mod="ModInfo:"+(new Gson().toJson(subtask.getModInfo()))+"\nModPackData:"+(new Gson().toJson(subtask.getModPackData()));
            modLabel.setToolTipText(mod);
            this.add(modLabel);
        }
    }

}
