package baliviya.com.github.cznBot.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat formatDateAndTimeSS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static SimpleDateFormat formatDateAndTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");

    public static String getDbMmYyyyHhMmSs(Date date) {
        formatDateAndTimeSS.setLenient(false);
        return formatDateAndTimeSS.format(date);
    }

    public static String getDayDate(Date date) {
        formatDateAndTime.setLenient(false);
        return formatDate.format(date);
    }

    public static String getString(Date date, String format) {
        return getResult(new SimpleDateFormat(format), date);
    }

    private static String getResult(SimpleDateFormat simpleDateFormat, Date date) {
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.format(date);
    }

    public static String getTimeDate(Date date) {
        return getString(date, "HH:mm:ss");
    }

}
