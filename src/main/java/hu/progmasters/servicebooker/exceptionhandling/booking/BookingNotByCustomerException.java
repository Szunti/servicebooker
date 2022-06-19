package hu.progmasters.servicebooker.exceptionhandling.booking;

import lombok.Getter;

@Getter
public class BookingNotByCustomerException extends RuntimeException {

    private final int id;
    private final int customerId;

    public BookingNotByCustomerException(int id, int customerId) {
        super(defaultMessage(id, customerId));
        this.id = id;
        this.customerId = customerId;
    }

    private static String defaultMessage(int id, int customerId) {
        return String.format("booking with id %d is not by the given customer with id %d", id, customerId);
    }
}
