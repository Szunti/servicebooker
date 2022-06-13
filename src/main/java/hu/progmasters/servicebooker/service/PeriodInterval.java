package hu.progmasters.servicebooker.service;

import hu.progmasters.servicebooker.util.interval.Interval;
import hu.progmasters.servicebooker.util.interval.Pair;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

import static hu.progmasters.servicebooker.util.interval.Pair.pair;

@Value
public class PeriodInterval implements Interval<LocalDateTime> {
    public static PeriodInterval periodInterval(Period period) {
        return new PeriodInterval(period);
    }

    Period period;

    private PeriodInterval(Period period) {
        Objects.requireNonNull(period);
        this.period = period;
    }

    @Override
    public LocalDateTime getStart() {
        return null;
    }

    @Override
    public LocalDateTime getEnd() {
        return null;
    }

    @Override
    public Interval<LocalDateTime> intersect(Interval<LocalDateTime> other) {
        // no idea how it should behave
        throw new UnsupportedOperationException("intersection of PeriodIntervals is not implemented");
    }

    @Override
    public Pair<Interval<LocalDateTime>> subtract(Interval<LocalDateTime> other) {
        // remove whole interval, do not keep partial ones
        return pair(null, null);
    }
}
