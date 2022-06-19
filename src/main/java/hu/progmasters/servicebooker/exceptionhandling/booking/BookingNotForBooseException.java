package hu.progmasters.servicebooker.exceptionhandling.booking;

import lombok.Getter;

@Getter
public class BookingNotForBooseException extends RuntimeException {

    private final int id;
    private final int booseId;

    public BookingNotForBooseException(int id, int booseId) {
        super(defaultMessage(id, booseId));
        this.id = id;
        this.booseId = booseId;
    }

    private static String defaultMessage(int id, int booseId) {
        return String.format("booking with id %d is not for the given service with id %d", id, booseId);
    }
}
