package hu.progmasters.servicebooker.exceptionhandling;

public class DateOutOfBookableBoundsException extends RuntimeException {
    public DateOutOfBookableBoundsException() {
    }

    public DateOutOfBookableBoundsException(String message) {
        super(message);
    }
}
