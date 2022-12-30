package ui.dashborad;

import javax.swing.*;

public class LogPanel extends JPanel {

    public JTextArea logTextArea=new JTextArea();
    public JScrollPane logScrollPane=new JScrollPane(logTextArea);
    public LogPanel(){
        this.setLayout(null);

        logScrollPane.setBounds(10,40,555,170);
        logTextArea.setEditable(false);
        logTextArea.setBorder(BorderFactory.createEtchedBorder());
        this.add(logScrollPane);

    }

    public void append(String text){
        logTextArea.append(text);
        logTextArea.setCaretPosition(logTextArea.getText().length());
    }
}
