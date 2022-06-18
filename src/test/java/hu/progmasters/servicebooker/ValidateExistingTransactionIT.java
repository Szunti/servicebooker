package hu.progmasters.servicebooker;

import hu.progmasters.servicebooker.dto.weeklyperiod.WeeklyPeriodCreateCommand;
import hu.progmasters.servicebooker.service.WeeklyPeriodService;
import hu.progmasters.servicebooker.util.DayOfWeekTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
public class ValidateExistingTransactionIT {

    @Autowired
    WeeklyPeriodService weeklyPeriodService;

    @Test
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    void addWeeklyPeriod() {
        WeeklyPeriodCreateCommand command = new WeeklyPeriodCreateCommand();
        command.setStart(DayOfWeekTime.of(DayOfWeek.FRIDAY, LocalTime.of(19, 0)));
        command.setEnd(DayOfWeekTime.of(DayOfWeek.FRIDAY, LocalTime.of(19, 5)));
        assertThatExceptionOfType(IllegalTransactionStateException.class).isThrownBy(() -> {
            weeklyPeriodService.addWeeklyPeriodForBoose(1, command);
        });
    }
}
