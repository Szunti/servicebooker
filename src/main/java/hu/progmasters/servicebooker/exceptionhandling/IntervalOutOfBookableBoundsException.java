package hu.progmasters.servicebooker.exceptionhandling;

public class IntervalOutOfBookableBoundsException extends RuntimeException {
    public IntervalOutOfBookableBoundsException() {
    }

    public IntervalOutOfBookableBoundsException(String message) {
        super(message);
    }
}
