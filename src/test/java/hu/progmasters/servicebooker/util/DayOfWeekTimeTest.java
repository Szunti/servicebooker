package hu.progmasters.servicebooker.util;

import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Locale;

import static hu.progmasters.servicebooker.util.DayOfWeekTime.SECONDS_PER_DAY;
import static hu.progmasters.servicebooker.util.DayOfWeekTime.SECONDS_PER_WEEK;
import static java.time.DayOfWeek.*;
import static java.time.Month.JUNE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DayOfWeekTimeTest {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;

    @Test
    void testToString() {
        Locale.setDefault(Locale.ENGLISH);
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(MONDAY, 10, 24, 5);
        assertEquals("Mon 10:24:05", dayOfWeekTime.toString());
    }

    @Test
    void fromTemporal() {
        LocalDateTime fridayEvening = LocalDateTime.of(2022, JUNE, 17, 18, 0);
        assertThat(DayOfWeekTime.from(fridayEvening))
                .isEqualTo(DayOfWeekTime.of(FRIDAY, 18, 0));
    }

    @Test
    void toSecondsFromWeekStart() {
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(TUESDAY, 6, 35);
        assertThat(dayOfWeekTime.toSecondsFromWeekStart()).isEqualTo(
                1 * SECONDS_PER_DAY + 6 * SECONDS_PER_HOUR + 35 * SECONDS_PER_MINUTE);
    }

    @Test
    void ofSecondsFromWeekStart() {
        int wednesdayMorning = 2 * SECONDS_PER_DAY + 7 * SECONDS_PER_HOUR + 32 * SECONDS_PER_MINUTE + 17;
        assertThat(DayOfWeekTime.of(wednesdayMorning)).isEqualTo(
                DayOfWeekTime.of(WEDNESDAY, 7, 32, 17)
        );
    }

    @Test
    void ofSecondFromWeekStart_tooLarge() {
        int firstSecondOfNextWeek = SECONDS_PER_WEEK;
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> {
            DayOfWeekTime.of(firstSecondOfNextWeek);
        });
    }

    @Test
    void ofSecondFromWeekStart_tooSmall() {
        int negativeSeconds = -5;
        assertThatExceptionOfType(DateTimeException.class).isThrownBy(() -> {
            DayOfWeekTime.of(negativeSeconds);
        });
    }

    @Test
    void parse() {
        assertThat(DayOfWeekTime.parse("Thu 15:42:55")).isEqualTo(
                DayOfWeekTime.of(THURSDAY, 15, 42, 55)
        );
    }

    @Test
    void sameWeekBeforeAfter_different() {
        DayOfWeekTime first = DayOfWeekTime.of(TUESDAY, 6, 0);
        DayOfWeekTime second = DayOfWeekTime.of(SATURDAY, 5, 0);

        assertThat(first.sameWeekBefore(second)).isTrue();
        assertThat(second.sameWeekBefore(first)).isFalse();
        assertThat(first.sameWeekAfter(second)).isFalse();
        assertThat(second.sameWeekAfter(first)).isTrue();
    }

    @Test
    void sameWeekBeforeAfter_same() {
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(TUESDAY, 6, 0);
        assertThat(dayOfWeekTime.sameWeekBefore(dayOfWeekTime)).isFalse();
        assertThat(dayOfWeekTime.sameWeekBefore(dayOfWeekTime)).isFalse();
    }

    @Test
    void next_different() {
        LocalDateTime datetime = LocalDateTime.of(2022, JUNE, 16, 10, 0);
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(TUESDAY, 6, 0);

        assertThat(datetime.with(DayOfWeekTime.next(dayOfWeekTime))).isEqualTo(
                LocalDateTime.of(2022, JUNE, 21, 6, 0)
        );
    }

    @Test
    void next_same() {
        LocalDateTime datetime = LocalDateTime.of(2022, JUNE, 16, 10, 0);
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(THURSDAY, 10, 0);

        assertThat(datetime.with(DayOfWeekTime.next(dayOfWeekTime))).isEqualTo(
                LocalDateTime.of(2022, JUNE, 23, 10, 0)
        );
    }

    @Test
    void nextOrSame_different() {
        LocalDateTime datetime = LocalDateTime.of(2022, JUNE, 16, 10, 0);
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(TUESDAY, 6, 0);

        assertThat(datetime.with(DayOfWeekTime.nextOrSame(dayOfWeekTime))).isEqualTo(
                LocalDateTime.of(2022, JUNE, 21, 6, 0)
        );
    }

    @Test
    void nextOrSame_same() {
        LocalDateTime datetime = LocalDateTime.of(2022, JUNE, 16, 10, 0);
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(THURSDAY, 10, 0);

        assertThat(datetime.with(DayOfWeekTime.nextOrSame(dayOfWeekTime))).
                isEqualTo(
                        LocalDateTime.of(2022, JUNE, 16, 10, 0)
                );
    }
}
