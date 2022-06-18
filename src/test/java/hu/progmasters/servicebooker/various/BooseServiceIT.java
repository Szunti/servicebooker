package hu.progmasters.servicebooker.various;

import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.OverlappingWeeklyPeriodException;
import hu.progmasters.servicebooker.service.BooseService;
import hu.progmasters.servicebooker.service.WeeklyPeriodService;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class BooseServiceIT {

    @Autowired
    BooseService booseService;

    @Autowired
    WeeklyPeriodService weeklyPeriodService;

    @Test
    void testAddWeeklyPeriodForBoose() {
        BooseInfo boose = saveSampleBoose();

        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(DayOfWeekTime.of(DayOfWeek.TUESDAY, 12, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.TUESDAY, 18, 0));
        command.setComment("overlapping test period");
        WeeklyPeriodInfo weeklyPeriod = weeklyPeriodService.addWeeklyPeriodForBoose(boose.getId(), command);

        assertThat(weeklyPeriod).as("weekly period saved as expected")
                .isNotNull()
                .extracting(
                        WeeklyPeriodInfo::getStart,
                        WeeklyPeriodInfo::getEnd,
                        WeeklyPeriodInfo::getComment)
                .containsExactly(command.getStart(), command.getEnd(), command.getComment());
    }

    @Test
    void testAddWeeklyPeriodForBoose_overlapping() {
        BooseInfo boose = saveSampleBoose();
        WeeklyPeriodInfo initialWp = saveSampleWeeklyPeriod(boose);

        assertThatExceptionOfType(OverlappingWeeklyPeriodException.class).isThrownBy(() -> {
            WeeklyPeriodInfo overlappingWp = saveSampleWeeklyPeriod(boose);
        });
    }

    BooseInfo saveSampleBoose() {
        BooseCreateCommand command = new BooseCreateCommand();
        command.setName("Doctor");
        command.setDescription("Reserve a time and become healthy.");
        return booseService.save(command);
    }

    WeeklyPeriodInfo saveSampleWeeklyPeriod(BooseInfo boose) {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(DayOfWeekTime.of(DayOfWeek.TUESDAY, 10, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.TUESDAY, 14, 0));
        command.setComment("test period");
        return weeklyPeriodService.addWeeklyPeriodForBoose(boose.getId(), command);
    }

    WeeklyPeriodInfo saveOverlappingWeeklyPeriod(BooseInfo boose) {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(DayOfWeekTime.of(DayOfWeek.TUESDAY, 12, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.TUESDAY, 18, 0));
        command.setComment("overlapping test period");
        return weeklyPeriodService.addWeeklyPeriodForBoose(boose.getId(), command);
    }

}