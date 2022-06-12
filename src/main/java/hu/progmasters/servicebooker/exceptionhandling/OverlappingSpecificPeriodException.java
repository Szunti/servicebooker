package hu.progmasters.servicebooker.exceptionhandling;

public class OverlappingSpecificPeriodException extends RuntimeException {
    public OverlappingSpecificPeriodException() {
        super("Period to save overlaps already existing periods in database");
    }

    public OverlappingSpecificPeriodException(String message) {
        super(message);
    }
}
