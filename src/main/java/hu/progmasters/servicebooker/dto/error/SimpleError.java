package hu.progmasters.servicebooker.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleError {
    @Schema(example = "service with id 1 not found")
    private final String errorMessage;

    public static SimpleError from(Throwable throwable) {
        return new SimpleError(throwable.getMessage());
    }
}
