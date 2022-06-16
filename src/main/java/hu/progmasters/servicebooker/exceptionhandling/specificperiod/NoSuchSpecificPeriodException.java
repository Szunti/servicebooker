package hu.progmasters.servicebooker.exceptionhandling.specificperiod;

import lombok.Getter;

@Getter
public class NoSuchSpecificPeriodException extends RuntimeException {

    private final int id;

    public NoSuchSpecificPeriodException(int id) {
        super(defaultMessage(id));
        this.id = id;
    }

    private static String defaultMessage(int id) {
        return String.format("specific period with id %d not found", id);
    }
}
