package core;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    public static String millsToMMDDHHmmSS(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) +
                "," + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
    }

    public static String dateToyyyyMMDD(Date date) {
        return String.format("%04d", date.getYear() + 1900) + "-" + String.format("%02d", date.getMonth() + 1) + "-" + String.format("%02d", date.getDate());
    }
    public static Date getSpecificDateDate(int year,int naturalMonth,int dateInMonth){
        Calendar calendar=Calendar.getInstance();
        calendar.set(year,naturalMonth-1,dateInMonth);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public static String nowMMDDHHmmSS() {
        return millsToMMDDHHmmSS(new Date().getTime());
    }

    public static String millsToFileNameValidMMDDHHmmSS(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) +
                "_" + calendar.get(Calendar.HOUR_OF_DAY) + "-" + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.SECOND);
    }

    public static String formattedMMDDHHmmSS(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return twoBitNum(calendar.get(Calendar.MONTH) + 1) + "-" + twoBitNum(calendar.get(Calendar.DAY_OF_MONTH)) +
                "," + twoBitNum(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + twoBitNum(calendar.get(Calendar.MINUTE)) + ":" + twoBitNum(calendar.get(Calendar.SECOND));
    }

    public static Date getTodayDate() {

        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
//        System.out.println("today:"+today.getTime().getTime());
        return today.getTime();
    }

    public static String millsToyyyyMMddHHmmSS(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return String.format("%04d", calendar.get(Calendar.YEAR)) + "年" + twoBitNum(calendar.get(Calendar.MONTH) + 1) + "月"
                + twoBitNum(calendar.get(Calendar.DAY_OF_MONTH)) + "日" + twoBitNum(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + twoBitNum(calendar.get(Calendar.MINUTE)) + ":" + twoBitNum(calendar.get(Calendar.SECOND));
    }

    public static String millsToMMddHHmmSSWithDash(long mills) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return twoBitNum(calendar.get(Calendar.MONTH) + 1) + "-"
                + twoBitNum(calendar.get(Calendar.DAY_OF_MONTH)) + " " + twoBitNum(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + twoBitNum(calendar.get(Calendar.MINUTE)) + ":" + twoBitNum(calendar.get(Calendar.SECOND));
    }

    public static String nowFormattedMMDDHHmmSS() {
        return formattedMMDDHHmmSS(new Date().getTime());
    }

    public static String nowFileNameValidMMDDHHmmSS() {
        return millsToFileNameValidMMDDHHmmSS(new Date().getTime());
    }

    public static String formattedHHmmSSms() {
        Calendar calendar = Calendar.getInstance();

        return twoBitNum(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + twoBitNum(calendar.get(Calendar.MINUTE)) + ":"
                + twoBitNum(calendar.get(Calendar.SECOND)) + "(" + String.format("%03d", calendar.get(Calendar.MILLISECOND)) + ")";
    }

    public static String twoBitNum(int num) {
        return String.format("%02d", num);
    }
}
