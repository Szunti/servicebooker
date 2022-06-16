package hu.progmasters.servicebooker.exceptionhandling.controller;

import hu.progmasters.servicebooker.exceptionhandling.controller.ControllerException;
import lombok.Getter;

@Getter
public class SpecificPeriodNotFoundException extends ControllerException {

    public SpecificPeriodNotFoundException(int id) {
        super(defaultMessage(id));
    }

    public SpecificPeriodNotFoundException(Throwable cause) {
        super(cause);
    }

    private static String defaultMessage(int id) {
        return String.format("specific period with id %d not found", id);
    }
}
