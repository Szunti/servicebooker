package hu.progmasters.servicebooker.util.interval;

import lombok.Getter;

@Getter
public class OverlappingIntervalException extends RuntimeException {
    private final IntervalLike<?, ?> interval;

    public OverlappingIntervalException(IntervalLike<?, ?> interval) {
        super(interval.toString());
        this.interval = interval;
    }
}
