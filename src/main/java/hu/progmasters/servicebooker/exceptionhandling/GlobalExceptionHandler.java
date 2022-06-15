package hu.progmasters.servicebooker.exceptionhandling;

import hu.progmasters.servicebooker.dto.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleValidationError(MethodArgumentNotValidException exception) {
        return exception.getFieldErrors().stream()
                .map(fieldError -> {
                    String field = fieldError.getField();
                    String message = fieldError.getDefaultMessage();
                    return new ValidationError(field, message);
                })
                .collect(Collectors.toList());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleMalformedRequestBody(HttpMessageNotReadableException exception) {
        return List.of(new Error("malformed request body"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<Error> handleRequestedBooseNotFound(BooseNotFoundException exception) {
        return List.of(new Error(String.format("boose with id %d not found", exception.getId())));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleNoSuchBoose(NoSuchBooseException exception) {
        return List.of(new ValidationError("booseId", String.format("boose with id %d not found", exception.getId())));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleOverlappingWeeklyPeriod(OverlappingSpecificPeriodException exception) {
        return List.of(new Error("weekly period overlapping existing ones"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleOverlappingSpecificPeriod(OverlappingSpecificPeriodException exception) {
        return List.of(new Error("specific period overlapping existing ones"));
    }
}
