package tasks.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateService {
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int HOURS_IN_A_DAY = 24;

    private final TasksService service;

    public DateService(TasksService service) {
        this.service = service;
    }

    /**
     * Converts a Date object to LocalDate.
     * @param date Date to convert.
     * @return LocalDate equivalent.
     */
    public static LocalDate getLocalDateValueFromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts a LocalDate object to Date.
     * @param localDate LocalDate to convert.
     * @return Date equivalent.
     */
    public Date getDateValueFromLocalDate(LocalDate localDate) {
        return Date.from(Instant.from(localDate.atStartOfDay(ZoneId.systemDefault())));
    }

    /**
     * Combines a date and a time string into a Date object.
     * @param time Time string in "HH:MM" format.
     * @param baseDate Base Date object.
     * @return Date with merged time.
     */
    public Date getDateMergedWithTime(String time, Date baseDate) {
        String[] timeUnits = time.split(":");
        if (timeUnits.length != 2) throw new IllegalArgumentException("Invalid time format. Use HH:MM");

        int hour = Integer.parseInt(timeUnits[0]);
        int minute = Integer.parseInt(timeUnits[1]);

        if (hour >= HOURS_IN_A_DAY || minute >= MINUTES_IN_HOUR) {
            throw new IllegalArgumentException("Time values exceed valid bounds");
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(baseDate);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /**
     * Extracts time from a Date object and formats it as HH:MM.
     * @param date Date object.
     * @return Time string in "HH:MM" format.
     */
    public String getTimeOfTheDayFromDate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        return service.formatTimeUnit(hours) + ":" + service.formatTimeUnit(minutes);
    }
}
