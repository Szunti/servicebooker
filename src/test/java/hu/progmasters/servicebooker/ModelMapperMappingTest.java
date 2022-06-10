package hu.progmasters.servicebooker;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelMapperMappingTest {

    static final ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    @Test
    void testMap_WeeklyPeriodToWeeklyPeriodInfo() {
        Boose boose = createBoose(1, "doctor");

        WeeklyPeriod weeklyPeriod = createWeeklyPeriod(1, boose,
                DayOfWeekTime.of(DayOfWeek.MONDAY, LocalTime.of(10, 0)),
                DayOfWeekTime.of(DayOfWeek.TUESDAY, LocalTime.of(15, 0)));

        WeeklyPeriodInfo info = modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class);

        assertEquals(1, info.getId());

        assertEquals(boose.getId(), info.getBoose().getId());
        assertEquals(boose.getName(), info.getBoose().getName());
        assertEquals(boose.getDescription(), info.getBoose().getDescription());

        assertEquals(weeklyPeriod.getStart(), info.getStart());
        assertEquals(weeklyPeriod.getEnd(), info.getEnd());
        assertEquals(weeklyPeriod.getComment(), info.getComment());
    }

    Boose createBoose(int id, String name) {
        Boose boose = new Boose();
        boose.setId(id);
        boose.setName(name);
        boose.setDescription("description for" + name + "service");
        return boose;
    }

    WeeklyPeriod createWeeklyPeriod(int id, Boose boose, DayOfWeekTime start, DayOfWeekTime end) {
        WeeklyPeriod weeklyPeriod = new WeeklyPeriod();
        weeklyPeriod.setId(id);
        weeklyPeriod.setBoose(boose);
        weeklyPeriod.setStart(start);
        weeklyPeriod.setEnd(end);
        weeklyPeriod.setComment("comment for period " + id);
        return weeklyPeriod;
    }
}
