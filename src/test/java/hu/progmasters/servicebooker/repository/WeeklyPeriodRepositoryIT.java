package hu.progmasters.servicebooker.repository;

import hu.progmasters.servicebooker.domain.Boose;
import hu.progmasters.servicebooker.domain.WeeklyPeriod;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@SpringBootTest
class WeeklyPeriodRepositoryIT {

    @Autowired
    BooseRepository booseRepository;

    @Autowired
    WeeklyPeriodRepository repository;

    @Test
    void testConcurrentSave(@Autowired PlatformTransactionManager transactionManager) {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        Integer booseId = txTemplate.execute(status -> {
            return saveSampleBoose().getId();
        });

        CyclicBarrier barrier = new CyclicBarrier(2);

        Thread thread1 = new Thread(() -> {
            txTemplate.execute(status -> {
                Boose boose = booseRepository.findById(booseId).orElseThrow();
                WeeklyPeriod weeklyPeriod = saveSampleWeeklyPeriod(boose);
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                return weeklyPeriod;
            });
        });

        Thread thread2 = new Thread(() -> {
            txTemplate.execute(status -> {
                Boose boose = booseRepository.findById(booseId).orElseThrow();
                WeeklyPeriod weeklyPeriod = saveOtherWeeklyPeriod(boose);
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                return weeklyPeriod;
            });
        });

        List<Thread> threads = List.of(thread1, thread2);
        threads.forEach(t -> t.start());
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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