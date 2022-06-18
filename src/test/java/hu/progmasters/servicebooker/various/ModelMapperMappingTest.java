package hu.progmasters.servicebooker.various;

import hu.progmasters.servicebooker.ServicebookerApplication;
import hu.progmasters.servicebooker.domain.entity.Boose;
import hu.progmasters.servicebooker.domain.entity.WeeklyPeriod;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelMapperMappingTest {

    static final ModelMapper modelMapper = new ServicebookerApplication().modelMapper();

    @Test
    void testMap_WeeklyPeriodToWeeklyPeriodInfo() {
        Boose boose = createBoose(1, "doctor");

        WeeklyPeriod weeklyPeriod = createWeeklyPeriod(1, boose,
                DayOfWeekTime.of(DayOfWeek.MONDAY, LocalTime.of(10, 0)),
                DayOfWeekTime.of(DayOfWeek.TUESDAY, LocalTime.of(15, 0)));

        WeeklyPeriodInfo info = modelMapper.map(weeklyPeriod, WeeklyPeriodInfo.class);

        assertThat(info).extracting(
                WeeklyPeriodInfo::getId,
                WeeklyPeriodInfo::getStart,
                WeeklyPeriodInfo::getEnd,
                WeeklyPeriodInfo::getComment
        ).containsExactly(
                weeklyPeriod.getId(),
                weeklyPeriod.getStart(),
                weeklyPeriod.getEnd(),
                weeklyPeriod.getComment()
        );
    }

    Boose createBoose(int id, String name) {
        Boose boose = new Boose();
        boose.setId(id);
        boose.setName(name);
        boose.setDescription("description for " + name + "service");
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
