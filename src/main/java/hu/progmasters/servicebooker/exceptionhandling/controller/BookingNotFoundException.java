package hu.progmasters.servicebooker.exceptionhandling.controller;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends ControllerException {

    public BookingNotFoundException(int id) {
        super(defaultMessage(id));
    }

    public BookingNotFoundException(Throwable cause) {
        super(cause);
    }

    private static String defaultMessage(int id) {
        return String.format("booking with id %d not found", id);
    }
}
