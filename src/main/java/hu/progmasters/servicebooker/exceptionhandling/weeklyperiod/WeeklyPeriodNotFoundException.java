package hu.progmasters.servicebooker.exceptionhandling.weeklyperiod;

import lombok.Getter;

@Getter
public class WeeklyPeriodNotFoundException extends RuntimeException {

    private final int id;

    public WeeklyPeriodNotFoundException(int id) {
        super(defaultMessage(id));
        this.id = id;
    }

    private static String defaultMessage(int id) {
        return String.format("weekly period with id %d not found", id);
    }
}
