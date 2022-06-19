package hu.progmasters.servicebooker.exceptionhandling.specificperiod;

import lombok.Getter;

@Getter
public class SpecificPeriodNotForBooseException extends RuntimeException {

    private final int id;
    private final int booseId;

    public SpecificPeriodNotForBooseException(int id, int booseId) {
        super(defaultMessage(id, booseId));
        this.id = id;
        this.booseId = booseId;
    }

    private static String defaultMessage(int id, int booseId) {
        return String.format("specific period with id %d is not for the given service with id %d", id, booseId);
    }

}
