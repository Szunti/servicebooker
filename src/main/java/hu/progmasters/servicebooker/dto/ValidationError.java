package hu.progmasters.servicebooker.dto;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class ValidationError extends Error {
    private String field;

    public ValidationError(String field, String errorMessage) {
        super(errorMessage);
        this.field = field;
    }
}
