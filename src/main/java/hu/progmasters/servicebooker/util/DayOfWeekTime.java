package hu.progmasters.servicebooker.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.Comparator;
import java.util.Objects;

@Value
public class DayOfWeekTime implements TemporalAccessor {
    public static final int SECONDS_PER_DAY = 24 * 60 * 60;
    public static final int SECONDS_PER_WEEK = 7 * SECONDS_PER_DAY;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE HH:mm:ss");

    public static final Comparator<DayOfWeekTime> SAME_WEEK_COMPARATOR =
            Comparator.comparing(DayOfWeekTime::toSecondsFromWeekStart);

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

    private static void checkValidValue(int secondsFromWeekStart) {
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

    public static DayOfWeekTime from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal);
        DayOfWeek dayOfWeek = DayOfWeek.from(temporal);
        LocalTime time = LocalTime.from(temporal);
        return new DayOfWeekTime(dayOfWeek, time);
    }

    // TODO use StdSerializer, @JsonComponent
    @JsonCreator
    public static DayOfWeekTime parse(CharSequence text) {
        return formatter.parse(text, DayOfWeekTime::from);
    }

    private static class NextAdjuster implements TemporalAdjuster {
        private final int targetSecondsFromWeekStart;
        private final boolean allowSame;


        public NextAdjuster(DayOfWeekTime dayOfWeekTime, boolean allowSame) {
            this.targetSecondsFromWeekStart = dayOfWeekTime.toSecondsFromWeekStart();
            this.allowSame = allowSame;
        }

        @Override
        public Temporal adjustInto(Temporal temporal) {
            int sourceSecondsFromWeekStart = DayOfWeekTime.from(temporal).toSecondsFromWeekStart();
            int differenceInSeconds = targetSecondsFromWeekStart - sourceSecondsFromWeekStart;
            if (differenceInSeconds < 0) {
                differenceInSeconds += SECONDS_PER_WEEK;
            } else if (!allowSame && differenceInSeconds == 0) {
                differenceInSeconds = SECONDS_PER_WEEK;
            }
            return temporal.plus(differenceInSeconds, ChronoUnit.SECONDS);
        }
    }

    public static TemporalAdjuster next(DayOfWeekTime dayOfWeekTime) {
        return new NextAdjuster(dayOfWeekTime, false);
    }

    public static TemporalAdjuster nextOrSame(DayOfWeekTime dayOfWeekTime) {
        return new NextAdjuster(dayOfWeekTime, true);
    }

    public boolean sameWeekBefore(DayOfWeekTime dayOfWeekTime) {
        return SAME_WEEK_COMPARATOR.compare(this, dayOfWeekTime) < 0;
    }

    public boolean sameWeekAfter(DayOfWeekTime dayOfWeekTime) {
        return SAME_WEEK_COMPARATOR.compare(this, dayOfWeekTime) > 0;
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

    @JsonValue
    @Override
    public String toString() {
        return formatter.format(this);
    }
}
