package hu.progmasters.servicebooker.exceptionhandling.weeklyperiod;

import lombok.Getter;

@Getter
public class WeeklyPeriodNotForBooseException extends RuntimeException {

    private final int id;
    private final int booseId;

    public WeeklyPeriodNotForBooseException(int id, int booseId) {
        super(defaultMessage(id, booseId));
        this.id = id;
        this.booseId = booseId;
    }

    private static String defaultMessage(int id, int booseId) {
        return String.format("weekly period with id %d is not for the given service with id %d", id, booseId);
    }

}
