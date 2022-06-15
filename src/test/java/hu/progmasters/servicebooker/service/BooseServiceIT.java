package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.dto.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.BooseInfo;
import hu.progmasters.servicebooker.dto.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.exceptionhandling.OverlappingWeeklyPeriodException;
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
    BooseService service;

    @Test
    void testAddWeeklyPeriodForBoose() {
        BooseInfo boose = saveSampleBoose();

        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setBooseId(boose.getId());
        command.setStart(DayOfWeekTime.of(DayOfWeek.TUESDAY, 12, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.TUESDAY, 18, 0));
        command.setComment("overlapping test period");
        WeeklyPeriodInfo weeklyPeriod = service.addWeeklyPeriodForBoose(command);

        assertThat(weeklyPeriod).as("weekly period saved as expected")
                .isNotNull()
                .extracting(
                        wp -> wp.getBoose().getId(),
                        WeeklyPeriodInfo::getStart,
                        WeeklyPeriodInfo::getEnd,
                        WeeklyPeriodInfo::getComment)
                .containsExactly(command.getBooseId(), command.getStart(), command.getEnd(), command.getComment());

        Boose booseEntityAfterAddingPeriod = service.getFromIdOrThrow(boose.getId());
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
        return service.save(command);
    }

    WeeklyPeriodInfo saveSampleWeeklyPeriod(BooseInfo boose) {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setBooseId(boose.getId());
        command.setStart(DayOfWeekTime.of(DayOfWeek.TUESDAY, 10, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.TUESDAY, 14, 0));
        command.setComment("test period");
        return service.addWeeklyPeriodForBoose(command);
    }

    WeeklyPeriodInfo saveOverlappingWeeklyPeriod(BooseInfo boose) {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setBooseId(boose.getId());
        command.setStart(DayOfWeekTime.of(DayOfWeek.TUESDAY, 12, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.TUESDAY, 18, 0));
        command.setComment("overlapping test period");
        return service.addWeeklyPeriodForBoose(command);
    }

}