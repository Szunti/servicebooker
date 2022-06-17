package hu.progmasters.servicebooker.util.interval;

import lombok.Getter;

@Getter
public class EmptyIntervalException extends RuntimeException {
    private final IntervalLike<?, ?> interval;

    public EmptyIntervalException(IntervalLike<?, ?> interval) {
        super(interval.toString());
        this.interval = interval;
    }
}
