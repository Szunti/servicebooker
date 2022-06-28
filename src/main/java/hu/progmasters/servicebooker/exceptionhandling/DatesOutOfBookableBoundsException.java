package hu.progmasters.servicebooker.exceptionhandling;

import hu.progmasters.servicebooker.dto.error.ValidationError;
import lombok.Getter;

import java.util.List;

@Getter
public class DatesOutOfBookableBoundsException extends RuntimeException {
    private final List<ValidationError> validationErrors;

    public DatesOutOfBookableBoundsException(List<ValidationError> validationErrors) {
        super("dates are out of the global bounds");
        this.validationErrors = validationErrors;
    }

    public DatesOutOfBookableBoundsException(String message, List<ValidationError> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }
}
