package hu.progmasters.servicebooker.exceptionhandling.controller;

import lombok.Getter;

@Getter
public class BooseNotFoundException extends ControllerException {

    public BooseNotFoundException(int id) {
        super(defaultMessage(id));
    }

    public BooseNotFoundException(Throwable cause) {
        super(cause);
    }

    private static String defaultMessage(int id) {
        return String.format("service with id %d not found", id);
    }
}
