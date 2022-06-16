package hu.progmasters.servicebooker.exceptionhandling.boose;

import lombok.Getter;

@Getter
public class NoSuchBooseException extends RuntimeException {

    private final int id;

    public NoSuchBooseException(int id) {
        super(defaultMessage(id));
        this.id = id;
    }

    private static String defaultMessage(int id) {
        return String.format("service with id %d not found", id);
    }
}
