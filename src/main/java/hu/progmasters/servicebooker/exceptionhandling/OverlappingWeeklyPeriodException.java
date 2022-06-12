package hu.progmasters.servicebooker.exceptionhandling;

public class OverlappingWeeklyPeriodException extends RuntimeException {
    public OverlappingWeeklyPeriodException() {
    }

    public OverlappingWeeklyPeriodException(String message) {
        super(message);
    }
}
