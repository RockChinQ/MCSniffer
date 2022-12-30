package ui.components;

import javax.swing.*;
import java.awt.*;

public class InputAreaField extends JPanel {
    public static final Font LABEL_FONT = new Font("Dialog.bold", Font.BOLD, 14);
    public static final Font CONTENT_FONT = new Font("Dialog.bold", Font.PLAIN, 13);
    public JLabel text = new JLabel("undefined");
    public JTextArea input = new JTextArea();
    JScrollPane scro;
    public static final Color fcl = new Color(56, 56, 56);
    public static final Color bgf = new Color(255, 255, 255);
    public static final Font font = new Font("", Font.PLAIN, 15);

    public InputAreaField(String name, int width, int height, int sepx) {
        this.setLayout(null);
        this.setSize(width, height);

        text.setText(name);
        text.setSize(sepx, 30);
        text.setLocation(0, 0);
        text.setForeground(fcl);
//        text.setFont(LABEL_FONT);
        this.add(text);

        scro = new JScrollPane(input);

        scro.setSize(width - sepx, height);
        scro.setLocation(sepx, 0);
        scro.setBorder(null);
        input.setFont(CONTENT_FONT);
        input.setBorder(BorderFactory.createEtchedBorder());
        input.setForeground(fcl);
        input.setBackground(bgf);
        input.setCaretColor(fcl);
//		input.setFont(font);
        input.setLineWrap(true);

        this.add(scro);
        //this.add(input);

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        text.setEnabled(enabled);
        input.setEnabled(enabled);
    }

    public String getValue() {
        return input.getText();
    }

    public void setValue(String s) {
        this.input.setText(s);
    }

    public void updateCom() {
        text.setBackground(getBackground());
    }
}
