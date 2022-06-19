package hu.progmasters.servicebooker.domain;

import hu.progmasters.servicebooker.util.interval.IntervalLike;
import hu.progmasters.servicebooker.util.interval.Pair;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

import static hu.progmasters.servicebooker.util.interval.Pair.pair;

@Value
public class PeriodInterval implements IntervalLike<PeriodInterval, LocalDateTime> {
    public static PeriodInterval periodInterval(TablePeriod period) {
        return new PeriodInterval(period);
    }

    public static PeriodInterval periodInterval(Period period) {
        TablePeriod tablePeriod = new TablePeriod(period.getStart(), period.getEnd(), period.getComment());
        return new PeriodInterval(tablePeriod);
    }

    TablePeriod period;

    private PeriodInterval(TablePeriod period) {
        Objects.requireNonNull(period);
        this.period = period;
    }

    @Override
    public LocalDateTime getStart() {
        return period.getStart();
    }

    @Override
    public LocalDateTime getEnd() {
        return period.getEnd();
    }

    @Override
    public PeriodInterval intersect(PeriodInterval other) {
        // no idea how it should behave
        throw new UnsupportedOperationException("intersection of PeriodIntervals is not implemented");
    }

    @Override
    public Pair<PeriodInterval> subtract(PeriodInterval other) {
        // remove whole interval, do not keep partial ones
        return pair(null, null);
    }
}
