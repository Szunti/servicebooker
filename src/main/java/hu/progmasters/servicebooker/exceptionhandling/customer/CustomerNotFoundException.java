package hu.progmasters.servicebooker.exceptionhandling.customer;

import lombok.Getter;

@Getter
public class CustomerNotFoundException extends RuntimeException {

    private final int id;

    public CustomerNotFoundException(int id) {
        super(defaultMessage(id));
        this.id = id;
    }

    private static String defaultMessage(int id) {
        return String.format("customer with id %d not found", id);
    }
}
