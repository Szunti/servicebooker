package hu.progmasters.servicebooker.domain;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static java.time.DayOfWeek.MONDAY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DayOfWeekTimeTest {

    @Test
    void testToString() {
        Locale.setDefault(Locale.ENGLISH);
        DayOfWeekTime dayOfWeekTime = DayOfWeekTime.of(MONDAY, 10, 24, 5);
        assertEquals("DayOfWeekTime(Mon 10:24:05)", dayOfWeekTime.toString());
    }

}