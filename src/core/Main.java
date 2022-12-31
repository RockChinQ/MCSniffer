package core;

import data.Settings;
import mapping.MappingRule;
import process.SnifferTask;
import ui.MainFrame;

public class Main {
    public static MainFrame mainFrame;
    public static SnifferTask snifferTask;
    public static Settings settings;

    public static MappingRule mappingRule;
    public static void main(String[] args) {
        settings=new Settings();
        try{
            settings.load();
        }catch (Exception ignored){}
        mainFrame=new MainFrame();
    }
}
