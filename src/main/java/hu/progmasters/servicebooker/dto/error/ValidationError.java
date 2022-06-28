package hu.progmasters.servicebooker.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidationError extends SimpleError {
    @Schema(example = "booseId")
    private String field;

    public ValidationError(String field, String errorMessage) {
        super(errorMessage);
        this.field = field;
    }

    @Override
    public String toString() {
        return "ValidationError(\"" + getField() + "\", \"" + getErrorMessage() + "\")";
    }
}
