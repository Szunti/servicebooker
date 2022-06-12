package hu.progmasters.servicebooker.util;

import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.Objects;

@Value
public class DayOfWeekTime implements TemporalAccessor {
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;
    private static final int SECONDS_PER_WEEK = 7 * SECONDS_PER_DAY;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE HH:mm:ss");

    DayOfWeek dayOfWeek;
    LocalTime time;

    private DayOfWeekTime(DayOfWeek dayOfWeek, LocalTime time) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    public static DayOfWeekTime of(int secondsFromWeekStart) {
        checkValidValue(secondsFromWeekStart);

        int daysBefore = secondsFromWeekStart / SECONDS_PER_DAY;
        DayOfWeek dayOfWeek = DayOfWeek.of(daysBefore + 1);

        int secondOfDay = secondsFromWeekStart % SECONDS_PER_DAY;
        LocalTime time = LocalTime.ofSecondOfDay(secondOfDay);

        return new DayOfWeekTime(dayOfWeek, time);
    }

    public static void checkValidValue(int secondsFromWeekStart) {
        ValueRange.of(0, SECONDS_PER_WEEK - 1).checkValidValue(secondsFromWeekStart, null);
    }

    public static DayOfWeekTime of(DayOfWeek dayOfWeek, LocalTime time) {
        Objects.requireNonNull(dayOfWeek);
        Objects.requireNonNull(time);
        return new DayOfWeekTime(dayOfWeek, time);
    }

    public static DayOfWeekTime of(DayOfWeek dayOfWeek, int hour, int minute, int second) {
        return of(dayOfWeek, LocalTime.of(hour, minute, second));
    }

    public static DayOfWeekTime of(DayOfWeek dayOfWeek, int hour, int minute) {
        return of(dayOfWeek, LocalTime.of(hour, minute));
    }

    public int toSecondsFromWeekStart() {
        int daysBefore = dayOfWeek.getValue() - 1;
        int secondOfDay = time.toSecondOfDay();
        return daysBefore * SECONDS_PER_DAY + secondOfDay;
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return dayOfWeek.isSupported(field) || time.isSupported(field);
        } else {
            return field != null && field.isSupportedBy(this);
        }
    }

    @Override
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            if (dayOfWeek.isSupported(field)) {
                return dayOfWeek.getLong(field);
            } else if (time.isSupported(field)) {
                return time.getLong(field);
            } else {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        } else {
            return field.getFrom(this);
        }
    }

    @Override
    public String toString() {
        return "DayOfWeekTime(" + formatter.format(this) + ")";
    }
}
