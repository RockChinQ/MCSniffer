package ui.result;

import com.google.gson.Gson;
import core.ClipBoard;
import process.Subtask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ServerEntry extends JPanel {
    Subtask subtask;

    JLabel faviconLabel=new JLabel(){
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if(subtask.getMinecraftServer().getFaviconImage()!=null){
                g.drawImage(subtask.getMinecraftServer().getFaviconImage(), 0,0,getWidth(),getHeight(),null);
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

        nameLabel.setBounds(40,5,240,15);
        nameLabel.setText(subtask.getAddress()+":"+subtask.getPort());
        nameLabel.setToolTipText(subtask.getAddress()+":"+subtask.getPort());
        this.add(nameLabel);

        descriptionLabel.setBounds(40,20,240,15);
        descriptionLabel.setText(subtask.getMinecraftServer().getDefaultDescriptionText());
        descriptionLabel.setToolTipText(subtask.getMinecraftServer().getDefaultDescriptionText());
        this.add(descriptionLabel);

        playersLabel.setBounds(40,35,140,15);
        playersLabel.setText("Players: "+subtask.getMinecraftServer().getOnlinePlayer()+"/"+subtask.getMinecraftServer().getMaxPlayer());
        playersLabel.setToolTipText(Arrays.toString(subtask.getPlayerList()));
        playersLabel.setForeground(Color.RED);
        this.add(playersLabel);

        versionLabel.setBounds(40,50,160,15);
        versionLabel.setText(subtask.getMinecraftServer().getVersionName());
        versionLabel.setForeground(Color.blue);
        this.add(versionLabel);

        copyAddressPortButton.setBounds(180,5,40,25);
        copyAddressPortButton.setBorder(BorderFactory.createEtchedBorder());
        copyAddressPortButton.addActionListener(e -> {
            ClipBoard.dumpToClipBoard(subtask.getAddress()+":"+subtask.getPort());
        });
        this.add(copyAddressPortButton);

        modLabel.setBounds(150,50,80,15);
        modLabel.setText("mod detected");
        modLabel.setForeground(Color.RED);
        if (subtask.getMinecraftServer().getModInfo()!=null||subtask.getMinecraftServer().getModPackData()!=null||subtask.getMinecraftServer().getForgeData()!=null){
            String mod="";
            if (subtask.getMinecraftServer().getModInfo()!=null){
                mod+="ModInfo:"+(new Gson().toJson(subtask.getMinecraftServer().getModInfo()));
            }
            if (subtask.getMinecraftServer().getModPackData()!=null){
                mod+="ModPackData:"+(new Gson().toJson(subtask.getMinecraftServer().getModPackData()));
            }
            if (subtask.getMinecraftServer().getForgeData()!=null){
                mod+="ForgeData:"+(new Gson().toJson(subtask.getMinecraftServer().getForgeData()));
            }
            modLabel.setToolTipText(mod);
            this.add(modLabel);
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                copyAddressPortButton.setBounds(getWidth()-50,5,40,25);
                modLabel.setBounds(getWidth()-90,50,80,15);
            }
        });
    }

}
