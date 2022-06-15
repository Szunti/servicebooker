package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.configuration.BookerProperties;
import hu.progmasters.servicebooker.exceptionhandling.DateOutOfBookableBoundsException;
import hu.progmasters.servicebooker.exceptionhandling.IntervalOutOfBookableBoundsException;
import hu.progmasters.servicebooker.util.interval.Interval;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static hu.progmasters.servicebooker.util.interval.SimpleInterval.interval;

@Component
public class DateTimeBoundChecker {

    private final Interval<LocalDateTime> boundingInterval;

    public DateTimeBoundChecker(BookerProperties bookerProperties) {
        this.boundingInterval = interval(bookerProperties.getMinBookableDate(), bookerProperties.getMaxBookableDate());
    }

    public void checkInBound(LocalDateTime date) {
        if (!boundingInterval.contains(date)) {
            throw new DateOutOfBookableBoundsException(
                    String.format("date(%s) is outside global bounds [%s, %s)",
                            date,
                            boundingInterval.getStart(),
                            boundingInterval.getEnd()));
        }
    }

    public void checkInBound(Interval<LocalDateTime> interval) {
        // TODO report both
        checkInBound(interval.getStart());
        checkInBound(interval.getEnd());
    }

    public Interval<LocalDateTime> constrain(Interval<LocalDateTime> interval) {
        Interval<LocalDateTime> constrained = boundingInterval.intersect(interval);
        if (constrained == null) {
            throw new IntervalOutOfBookableBoundsException(
                    String.format("start(%s), end(%s) is completely outside global bounds [%s, %s)",
                            interval.getStart(), interval.getEnd(),
                            boundingInterval.getStart(), boundingInterval.getEnd()));
        }
        return constrained;
    }
}
