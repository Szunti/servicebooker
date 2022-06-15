package hu.progmasters.servicebooker.exceptionhandling;

import lombok.Getter;

@Getter
public class NoSuchWeeklyPeriodException extends RuntimeException {

    private final int id;

    public NoSuchWeeklyPeriodException(int id) {
        super(defaultMessage(id));
        this.id = id;
    }

    private static String defaultMessage(int id) {
        return String.format("weekly period with id %d not found", id);
    }
}
