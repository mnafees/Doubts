/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtil {

    public static long getMillis(String dateTime) {
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            String s = dateTime.replace("Z", "+00:00");
            try {
                s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Invalid length", 0);
            }
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
            calendar.setTime(date);
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

}
