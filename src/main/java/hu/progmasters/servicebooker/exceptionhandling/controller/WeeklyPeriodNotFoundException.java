package hu.progmasters.servicebooker.exceptionhandling.controller;

import hu.progmasters.servicebooker.exceptionhandling.controller.ControllerException;
import lombok.Getter;

@Getter
public class WeeklyPeriodNotFoundException extends ControllerException {

    public WeeklyPeriodNotFoundException(int id) {
        super(defaultMessage(id));
    }

    public WeeklyPeriodNotFoundException(Throwable cause) {
        super(cause);
    }

    private static String defaultMessage(int id) {
        return String.format("weekly period with id %d not found", id);
    }
}
