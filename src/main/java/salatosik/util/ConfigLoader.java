package salatosik.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

import arc.util.Log;

public class ConfigLoader {

    private static String timeZone;

    public static void init(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);

        String timeZoneFromConfig = properties.getProperty("time.timezone");
        boolean timeZoneFound = false;

        for(String zone: TimeZone.getAvailableIDs()) {
            if(timeZoneFromConfig.equals(zone)) {
                timeZoneFound = true;
            }
        }

        if(!timeZoneFound) {
            Log.info("Time zone " + timeZoneFromConfig + " not found!\nSet the classic time zone..");

        } else {
            timeZone = timeZoneFromConfig;
            Log.info("Time zone " + timeZone + " has loaded");
        }
    }


    public static Calendar getTimeZoneCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone(timeZone));
    }

    public static SimpleDateFormat getTimeZoneFormatter() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formater.setTimeZone(TimeZone.getTimeZone(timeZone));
        return formater; 
    }
}
