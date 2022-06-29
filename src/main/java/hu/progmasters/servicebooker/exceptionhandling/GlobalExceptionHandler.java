package hu.progmasters.servicebooker.exceptionhandling;

import hu.progmasters.servicebooker.dto.error.SimpleError;
import hu.progmasters.servicebooker.dto.error.ValidationError;
import hu.progmasters.servicebooker.exceptionhandling.booking.*;
import hu.progmasters.servicebooker.exceptionhandling.boose.BooseNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.customer.CustomerNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.OverlappingSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.OverlappingWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotForBooseException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotFoundException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@ApiResponse(responseCode = "400", description = "Bad Request\n\nThere was a problem with the request. " +
        "For example a validation error, malformed input or trying to book an already booked period",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(oneOf = {ValidationError.class, SimpleError.class}))))
@ApiResponse(responseCode = "404", content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        array = @ArraySchema(schema = @Schema(implementation = SimpleError.class))))
public class GlobalExceptionHandler {
    private static final String LOG_EXCEPTION = "Exception caught while handling request:";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleValidationError(MethodArgumentNotValidException exception) {
        log.info(LOG_EXCEPTION, exception);
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
    public List<ValidationError> handleGlobalBoundValidationError(DatesOutOfBookableBoundsException exception) {
        log.info(LOG_EXCEPTION, exception);
        return exception.getValidationErrors();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<SimpleError> handleMalformedRequestBody(HttpMessageNotReadableException exception) {
        log.info(LOG_EXCEPTION, exception);
        return List.of(new SimpleError("malformed request body"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<SimpleError> handleMissingRequestParameter(MissingServletRequestParameterException exception) {
        log.info(LOG_EXCEPTION, exception);
        String message = String.format("required request parameter '%s' not present", exception.getParameterName());
        return List.of(new SimpleError(message));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<SimpleError> handleParameterTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.info(LOG_EXCEPTION, exception);
        return List.of(new SimpleError("malformed request parameters"));
    }

    @ExceptionHandler({
            BooseNotFoundException.class,
            WeeklyPeriodNotFoundException.class,
            SpecificPeriodNotFoundException.class,
            CustomerNotFoundException.class,
            BookingNotFoundException.class,
            WeeklyPeriodNotForBooseException.class,
            SpecificPeriodNotForBooseException.class,
            BookingNotForBooseException.class,
            BookingNotByCustomerException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<SimpleError> handleNotFound(Exception exception) {
        log.info(LOG_EXCEPTION, exception);
        return List.of(SimpleError.from(exception));
    }

    @ExceptionHandler({
            OverlappingWeeklyPeriodException.class,
            OverlappingSpecificPeriodException.class,
            IntervalOutOfBookableBoundsException.class,
            BookingNotAvailablePeriodException.class,
            AlreadyBookedException.class,
            NoSuchBooseException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<SimpleError> handleCommonExceptions(Exception exception) {
        log.info(LOG_EXCEPTION, exception);
        return List.of(SimpleError.from(exception));
    }
}
