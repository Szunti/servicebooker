package hu.progmasters.servicebooker.util.period;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

@Value
public class SimplePeriod implements Period {
    LocalDateTime start;
    LocalDateTime end;
    String comment;

    public static SimplePeriod of(LocalDateTime start, LocalDateTime end, String comment) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        if (!isInOrder(start, end)) {
            throw new IllegalArgumentException(String.format("start(%s) > end(%s)", start, end));
        }
        return new SimplePeriod(start, end, comment);
    }

    private SimplePeriod(LocalDateTime start, LocalDateTime end, String comment) {
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

    public static <T extends Comparable<? super T>> boolean isInOrder(T start, T end) {
        return start.compareTo(end) <= 0;
    }
}
