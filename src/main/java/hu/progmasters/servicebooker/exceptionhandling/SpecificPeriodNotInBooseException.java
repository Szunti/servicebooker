package hu.progmasters.servicebooker.exceptionhandling;

import lombok.Getter;

@Getter
public class SpecificPeriodNotInBooseException extends RuntimeException {

    private final int id;
    private final int booseId;

    public SpecificPeriodNotInBooseException(int id, int booseId) {
        super(defaultMessage(id, booseId));
        this.id = id;
        this.booseId = booseId;
    }

    private static String defaultMessage(int id, int booseId) {
        return String.format("specific period with id %d is not in the given service with id %d", id, booseId);
    }

}
