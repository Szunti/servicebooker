package hu.progmasters.servicebooker.exceptionhandling.controller;

import lombok.Getter;

@Getter
public class CustomerNotFoundException extends ControllerException {

    public CustomerNotFoundException(int id) {
        super(defaultMessage(id));
    }

    public CustomerNotFoundException(Throwable cause) {
        super(cause);
    }

    private static String defaultMessage(int id) {
        return String.format("customer with id %d not found", id);
    }
}
