package hu.progmasters.servicebooker.exceptionhandling.weeklyperiod;

public class OverlappingWeeklyPeriodException extends RuntimeException {
    public OverlappingWeeklyPeriodException() {
        super("Period to save overlaps already existing periods in database");
    }

    public OverlappingWeeklyPeriodException(String message) {
        super(message);
    }
}
