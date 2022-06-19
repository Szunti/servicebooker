package hu.progmasters.servicebooker.various;

import hu.progmasters.servicebooker.dto.boose.BooseCreateCommand;
import hu.progmasters.servicebooker.dto.boose.BooseInfo;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodInfo;
import hu.progmasters.servicebooker.service.BooseService;
import hu.progmasters.servicebooker.service.WeeklyPeriodService;
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
class ConcurrentWeeklyPeriodServiceIT {

    @TestConfiguration
    static class Config {
        @Bean
        LockingOrdererAspect lockingAspect() {
            return new LockingOrdererAspect();
        }
    }

    @Autowired
    BooseService booseService;

    @Autowired
    WeeklyPeriodService weeklyPeriodService;

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

        List<WeeklyPeriodInfo> weeklyPeriodsInDatabase = weeklyPeriodService.findAllForBoose(boose.getId());

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
        return weeklyPeriodService.addForBoose(boose.getId(), command);
    }

    WeeklyPeriodInfo saveOtherWeeklyPeriod(BooseInfo boose) {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(DayOfWeekTime.of(DayOfWeek.THURSDAY, 6, 0));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.MONDAY, 14, 0));
        command.setComment("test period saved concurrently");
        return weeklyPeriodService.addForBoose(boose.getId(), command);
    }
}