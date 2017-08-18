package es.dsrroma.garantator.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.dsrroma.garantator.data.model.Warranty;

import static es.dsrroma.garantator.utils.MyStringUtils.isNotEmpty;

public class MyDateUtils {

    public static long getNoonTime(Date date) {
        // Set to noon on the selected day
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public static long getNoonTime(int year, int month, int day) {
        // Set to noon on the selected day
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public static int getCalendarField(String period) {
        switch (period) {
            case "D":
                return Calendar.DAY_OF_MONTH;
            case "M":
                return Calendar.MONTH;
            case "Y":
                return Calendar.YEAR;
        }
        return -1; // Impossible
    }

    public static void calculateExpirationDate(Warranty warranty) {
        if (warranty.getStartDate() != null && isNotEmpty(warranty.getPeriod()) && warranty.getLength() > 0) {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(warranty.getStartDate().getTime());
            c.add(getCalendarField(warranty.getPeriod()), warranty.getLength());
            warranty.setEndDate(c.getTime());
        }
    }
}
