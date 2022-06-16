package hu.progmasters.servicebooker.dto.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleError {
    private final String errorMessage;

    public static SimpleError from(Throwable throwable) {
        return new SimpleError(throwable.getMessage());
    }
}
