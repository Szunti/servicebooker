package hu.progmasters.servicebooker.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleError {
    @Schema(example = "service with id 1 not found")
    private String errorMessage;

    public SimpleError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static SimpleError from(Throwable throwable) {
        return new SimpleError(throwable.getMessage());
    }

    @Override
    public String toString() {
        return "SimpleError(\"" + errorMessage + "\")";
    }
}
