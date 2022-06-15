package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.dto.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.BooseInfo;
import hu.progmasters.servicebooker.dto.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
class ConcurrentBooseServiceIT {

    @TestConfiguration
    static class Config {
        @Bean
        LockingOrdererAspect lockingAspect() {
            return new LockingOrdererAspect();
        }
    }

    @Autowired
    BooseService booseService;

    @Test
    void testConcurrentSave(@Autowired AsyncTaskExecutor executor) {
        BooseInfo boose = saveSampleBoose();

        List<Future<WeeklyPeriodInfo>> threadFutures = new ArrayList<>();
        threadFutures.add(executor.submit(() -> {
            ThreadOrder.setMyOrder(ThreadOrder.FIRST);
            return saveSampleWeeklyPeriod(boose);
        }));

        threadFutures.add(executor.submit(() -> {
            ThreadOrder.setMyOrder(ThreadOrder.SECOND);
            return saveOtherWeeklyPeriod(boose);
        }));

        assertThatNoException().isThrownBy(() -> {
            for (Future<?> future : threadFutures) {
                future.get();
            }
        });

        List<WeeklyPeriodInfo> weeklyPeriodsInDatabase = booseService.findAllWeeklyPeriodsForBoose(boose.getId());

        assertThat(threadFutures)
                .extracting(Future::get)
                .containsExactlyInAnyOrderElementsOf(weeklyPeriodsInDatabase);
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
        return booseService.addWeeklyPeriodForBoose(boose.getId(), command);
    }

    WeeklyPeriodInfo saveOtherWeeklyPeriod(BooseInfo boose) {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(DayOfWeekTime.of(DayOfWeek.THURSDAY, 6, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.MONDAY, 14, 0));
        command.setComment("test period saved concurrently");
        return booseService.addWeeklyPeriodForBoose(boose.getId(), command);
    }
}