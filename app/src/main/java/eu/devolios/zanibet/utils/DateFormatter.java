package eu.devolios.zanibet.utils;

import android.content.res.Resources;
import android.os.Build;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Gromat Luidgi on 14/11/2017.
 */

public class DateFormatter {

    public static Date formatMongoDate(String date){
        try {
            SimpleDateFormat format;
            if (Build.VERSION.SDK_INT >= 24) {
                format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Resources.getSystem().getConfiguration().getLocales().get(0));
            } else {
                format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Resources.getSystem().getConfiguration().locale);
            }
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }

    public static String parseDateForLocal(Date date){
        Locale locale = Locale.getDefault();
        return DateTimeUtils.formatWithPattern(date, "EEEE dd MMMM, yyyy HH:mm:ss");
    }

    public static boolean isTomorrow(Date date){
        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, +1); // tomorrow

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date); //

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}
