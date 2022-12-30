package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DialogManager {
    public static ArrayList<Window> dialogs = new ArrayList<>();

    public static void register(Window component) {
        dialogs.add(component);
    }

    public static void disposeAll() {
        for (Window component : dialogs) {
            component.dispose();
        }
    }

    public static void clearAll() {
        dialogs.clear();
    }

    public static void showAll() {
        for (Window window : dialogs) {
            window.setVisible(true);
        }
    }

    public static void setCenter(JFrame frame){
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        double width = ss.getWidth();
        double height = ss.getHeight();
        frame.setLocation((int) ((width - frame.getWidth()) / 2), (int) ((height - frame.getHeight()) / 2));
    }
}
