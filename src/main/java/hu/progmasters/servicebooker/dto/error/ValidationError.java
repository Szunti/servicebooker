package hu.progmasters.servicebooker.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ValidationError extends SimpleError {
    @Schema(example = "booseId")
    private final String field;

    public ValidationError(String field, String errorMessage) {
        super(errorMessage);
        this.field = field;
    }
}
