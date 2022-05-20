package hu.progmasters.servicebooker.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;

public class DayOfWeekTime implements TemporalAccessor {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE HH:mm:ss");

    private final DayOfWeek dayOfWeek;
    private final LocalTime time;

    private DayOfWeekTime(DayOfWeek dayOfWeek, LocalTime time) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    public static DayOfWeekTime of(DayOfWeek dayOfWeek, LocalTime time) {
        return new DayOfWeekTime(dayOfWeek, time);
    }

    public static DayOfWeekTime of(DayOfWeek dayOfWeek, int hour, int minute, int second) {
        return of(dayOfWeek, LocalTime.of(hour, minute, second));
    }

    public static DayOfWeekTime of(DayOfWeek dayOfWeek, int hour, int minute) {
        return of(dayOfWeek, LocalTime.of(hour, minute));
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getTime() {
        return time;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DayOfWeekTime)) return false;
        DayOfWeekTime that = (DayOfWeekTime) o;
        return dayOfWeek == that.dayOfWeek && time.equals(that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, time);
    }

    @Override
    public String toString() {
        return "DayOfWeekTime(" + formatter.format(this) + ")";
    }
}
