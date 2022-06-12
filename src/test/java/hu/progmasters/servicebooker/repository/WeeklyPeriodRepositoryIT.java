package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
class WeeklyPeriodRepositoryIT {

    @Autowired
    BooseRepository booseRepository;

    @Autowired
    WeeklyPeriodRepository repository;

    @Test
    void testConcurrentSave(@Autowired PlatformTransactionManager transactionManager,
                            @Autowired AsyncTaskExecutor executor) {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        Integer booseId = txTemplate.execute(status -> {
            return saveSampleBoose().getId();
        });

        CyclicBarrierWrapper barrier = new CyclicBarrierWrapper(2);

        List<Future<WeeklyPeriod>> threadFutures = new ArrayList<>();

        threadFutures.add(executor.submit(() ->
                txTemplate.execute(status -> {
                    Boose boose = booseRepository.findById(booseId).orElseThrow();
                    WeeklyPeriod weeklyPeriod = saveSampleWeeklyPeriod(boose);
                    barrier.await();
                    return weeklyPeriod;
                })));

        threadFutures.add(executor.submit(() ->
                txTemplate.execute(status -> {
                    Boose boose = booseRepository.findById(booseId).orElseThrow();
                    barrier.await();
                    return saveOtherWeeklyPeriod(boose);
                })));

        Boose boose = booseRepository.findById(booseId).orElseThrow();

        assertThatNoException().isThrownBy(() -> {
            for (Future<?> future : threadFutures) {
                future.get();
            }
        });

        List<WeeklyPeriod> weeklyPeriodsInDatabase = repository.findAllFor(boose);

        assertThat(threadFutures)
                .extracting(Future::get)
                .containsExactlyInAnyOrderElementsOf(weeklyPeriodsInDatabase);
    }

    Boose saveSampleBoose() {
        Boose boose = new Boose();
        boose.setName("Doctor");
        boose.setDescription("Reserve a time and become healthy.");
        return booseRepository.save(boose);
    }

    WeeklyPeriod saveSampleWeeklyPeriod(Boose boose) {
        WeeklyPeriod weeklyPeriod = new WeeklyPeriod();
        weeklyPeriod.setBoose(boose);
        weeklyPeriod.setStart(DayOfWeekTime.of(DayOfWeek.TUESDAY, 10, 0));
        weeklyPeriod.setEnd(DayOfWeekTime.of(DayOfWeek.TUESDAY, 14, 0));
        weeklyPeriod.setComment("test period");
        return repository.save(weeklyPeriod);
    }

    WeeklyPeriod saveOtherWeeklyPeriod(Boose boose) {
        WeeklyPeriod weeklyPeriod = new WeeklyPeriod();
        weeklyPeriod.setBoose(boose);
        weeklyPeriod.setStart(DayOfWeekTime.of(DayOfWeek.THURSDAY, 6, 0));
        weeklyPeriod.setEnd(DayOfWeekTime.of(DayOfWeek.MONDAY, 14, 0));
        weeklyPeriod.setComment("test period saved concurrently");
        return repository.save(weeklyPeriod);
    }
}