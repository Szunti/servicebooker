package hu.progmasters.servicebooker.validation;

import hu.progmasters.servicebooker.configuration.BookerProperties;
import hu.progmasters.servicebooker.dto.error.ValidationError;
import hu.progmasters.servicebooker.exceptionhandling.DatesOutOfBookableBoundsException;
import hu.progmasters.servicebooker.exceptionhandling.IntervalOutOfBookableBoundsException;
import hu.progmasters.servicebooker.util.interval.Interval;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;

@Component
public class DateTimeBoundChecker {

    private final Interval<LocalDateTime> boundingInterval;

    public DateTimeBoundChecker(BookerProperties bookerProperties) {
        this.boundingInterval = interval(bookerProperties.getMinBookableDate(), bookerProperties.getMaxBookableDate());
    }

    public void checkInBound(Interval<LocalDateTime> interval) {
        checkInBound(interval, "start", "end");
    }

    public void checkInBound(Interval<LocalDateTime> interval, String startField, String endField) {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (!boundingInterval.contains(interval.getStart())) {
            validationErrors.add(new ValidationError(startField, outsideBoundsMessage()));
        }
        if (!boundingInterval.contains(interval.getEnd())) {
            validationErrors.add(new ValidationError(endField, outsideBoundsMessage()));
        }
        if (!validationErrors.isEmpty()) {
            throw new DatesOutOfBookableBoundsException(validationErrors);
        }
    }

    private String outsideBoundsMessage() {
        return String.format("outside global bounds [%s, %s)", boundingInterval.getStart(), boundingInterval.getEnd());
    }

    public Interval<LocalDateTime> constrain(Interval<LocalDateTime> interval) {
        Interval<LocalDateTime> constrained = boundingInterval.intersect(interval);
        if (constrained == null) {
            throw new IntervalOutOfBookableBoundsException(
                    String.format("queried interval [%s, %s) does not have any points inside global bounds [%s, %s)",
                            interval.getStart(), interval.getEnd(),
                            boundingInterval.getStart(), boundingInterval.getEnd()));
        }
        return constrained;
    }
}
