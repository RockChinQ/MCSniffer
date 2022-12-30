package core;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
    public static void log(String subject,String message){
        String timeStr=TimeUtil.nowMMDDHHmmSS();

        String logStr=timeStr+" ["+subject+"] "+message;
        System.out.println(logStr);
        Main.mainFrame.dashboardPanel.logPanel.append(logStr+"\n");
    }

    /**
     * Return the message and stack trace of an exception object
     *
     * @param e exception object
     * @return message and stack trace
     */
    public static String getErrorInfo(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replaceAll("\t", "    ");
    }
}
