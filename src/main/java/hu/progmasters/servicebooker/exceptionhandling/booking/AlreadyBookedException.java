package hu.progmasters.servicebooker.exceptionhandling.booking;

import hu.progmasters.servicebooker.domain.TablePeriod;

public class AlreadyBookedException extends RuntimeException {
    public AlreadyBookedException(TablePeriod period) {
        super(defaultMessage(period));
    }

    private static String defaultMessage(TablePeriod period) {
        return String.format("period between %s and %s already booked", period.getStart(), period.getEnd());
    }
}
