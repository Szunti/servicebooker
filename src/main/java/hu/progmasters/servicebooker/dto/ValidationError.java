package hu.progmasters.servicebooker.dto;

import lombok.Getter;

@Getter
public class ValidationError extends SimpleError {
    private final String field;

    public ValidationError(String field, String errorMessage) {
        super(errorMessage);
        this.field = field;
    }
}
