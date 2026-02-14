package hexlet.code.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ActualTime {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final ZoneId ZONE = ZoneId.systemDefault();

    public static String getActualTime(Timestamp time) {
        if (time == null) {
            return "";
        }
        return getActualTime(time.toLocalDateTime());
    }

    public static String getActualTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        String formattedDate = dateTime.format(DATE_FORMATTER);
        int minutes = dateTime.getMinute();
        String formattedTime = dateTime.getHour() + ":" + (minutes > 9 ? minutes : "0" + minutes);
        return formattedDate + " " + formattedTime;
    }

    public static String getActualTime(Instant instant) {
        if (instant == null) {
            return "";
        }
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZONE);
        return getActualTime(ldt);
    }
}
