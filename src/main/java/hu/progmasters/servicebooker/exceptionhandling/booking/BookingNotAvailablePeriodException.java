package hu.progmasters.servicebooker.exceptionhandling.booking;

import hu.progmasters.servicebooker.domain.entity.Booking;

public class BookingNotAvailablePeriodException extends RuntimeException {
    public BookingNotAvailablePeriodException(Booking booking) {
        super(defaultMessage(booking));
    }

    private static String defaultMessage(Booking booking) {
        return String.format("there is no bookable period between %s and %s", booking.getStart(), booking.getEnd());
    }
}
