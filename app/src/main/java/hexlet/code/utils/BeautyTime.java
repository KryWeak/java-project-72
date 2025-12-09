package hexlet.code.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BeautyTime {
    public static String getBeautyTime(Timestamp time) {
        LocalDateTime dateTime = time.toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = dateTime.format(dateFormatter);
        int minutes = dateTime.getMinute();
        String formattedTime = dateTime.getHour() + ":" + (minutes > 9 ? minutes : "0" + minutes);
        return formattedDate + " " + formattedTime;
    }
}
