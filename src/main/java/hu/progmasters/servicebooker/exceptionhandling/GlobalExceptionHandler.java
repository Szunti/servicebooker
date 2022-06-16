package hu.progmasters.servicebooker.exceptionhandling;

import hu.progmasters.servicebooker.dto.error.SimpleError;
import hu.progmasters.servicebooker.dto.error.ValidationError;
import hu.progmasters.servicebooker.exceptionhandling.controller.BooseNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.boose.NoSuchBooseException;
import hu.progmasters.servicebooker.exceptionhandling.controller.SpecificPeriodNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.controller.WeeklyPeriodNotFoundException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.NoSuchSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.OverlappingSpecificPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.specificperiod.SpecificPeriodNotInBooseException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.NoSuchWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.OverlappingWeeklyPeriodException;
import hu.progmasters.servicebooker.exceptionhandling.weeklyperiod.WeeklyPeriodNotInBooseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler({
            BooseNotFoundException.class,
            WeeklyPeriodNotFoundException.class,
            SpecificPeriodNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<SimpleError> handleNotFound(Exception exception) {
        log.info(LOG_EXCEPTION, exception);
        return List.of(SimpleError.from(exception));
    }

    @ExceptionHandler({
            NoSuchBooseException.class,
            NoSuchWeeklyPeriodException.class,
            NoSuchSpecificPeriodException.class,
            WeeklyPeriodNotInBooseException.class,
            SpecificPeriodNotInBooseException.class,
            OverlappingWeeklyPeriodException.class,
            OverlappingSpecificPeriodException.class,
            DateOutOfBookableBoundsException.class,
            IntervalOutOfBookableBoundsException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<SimpleError> handleCommonExceptions(Exception exception) {
        log.info(LOG_EXCEPTION, exception);
        return List.of(SimpleError.from(exception));
    }
}
