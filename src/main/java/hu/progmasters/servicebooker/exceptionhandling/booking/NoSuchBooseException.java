package hu.progmasters.servicebooker.exceptionhandling.booking;

import lombok.Getter;

@Getter
public class NoSuchBooseException extends RuntimeException {

    public NoSuchBooseException(String message) {
        super(message);
    }

    public NoSuchBooseException(Throwable cause) {
        super(causeMessage(cause), cause);
    }

    public NoSuchBooseException(String message, Throwable cause) {
        super(message, cause);
    }

    private static String causeMessage(Throwable cause) {
        return cause != null ? cause.getMessage() : null;
    }
}
