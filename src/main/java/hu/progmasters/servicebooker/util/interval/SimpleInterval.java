package hu.progmasters.servicebooker.util.interval;

import lombok.ToString;
import lombok.Value;

import java.util.Objects;

import static hu.progmasters.servicebooker.util.interval.Pair.pair;

/**
 * This class is value-based.
 *
 * @param <T>
 */
@Value
@ToString(includeFieldNames = false)
public class SimpleInterval<T extends Comparable<? super T>> implements Interval<T> {
    T start;
    T end;

    public static <T extends Comparable<? super T>> SimpleInterval<T> interval(T start, T end) {
        return new SimpleInterval<>(start, end);
    }

    private SimpleInterval(T start, T end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        if (!isInOrder(start, end)) {
            throw new IllegalArgumentException(String.format("start(%s) > end(%s)", start, end));
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public SimpleInterval<T> intersect(Interval<T> other) {
        T maxStart = max(this.start, other.getStart());
        T minEnd = min(this.end, other.getEnd());

        SimpleInterval<T> intersection = null;

        if (isInOrderAndNotEmpty(maxStart, minEnd)) {
            intersection = interval(maxStart, minEnd);
        }
        return intersection;
    }

    @Override
    public Pair<Interval<T>> subtract(Interval<T> other) {
        T endOfFirstPart = min(this.end, other.getStart());
        T startOfSecondPart = max(this.start, other.getEnd());

        SimpleInterval<T> leftRemaining = null;
        if (isInOrderAndNotEmpty(this.start, endOfFirstPart)) {
            leftRemaining = interval(this.start, endOfFirstPart);
        }

        SimpleInterval<T> rightRemaining = null;
        if (isInOrderAndNotEmpty(startOfSecondPart, this.end)) {
            rightRemaining = interval(startOfSecondPart, this.end);
        }
        return pair(leftRemaining, rightRemaining);
    }

    public static <T extends Comparable<? super T>> T min(T a, T b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static <T extends Comparable<? super T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    public static <T extends Comparable<? super T>> boolean isInOrder(T start, T end) {
        return start.compareTo(end) <= 0;
    }

    public static <T extends Comparable<? super T>> boolean isInOrderAndNotEmpty(T start, T end) {
        return start.compareTo(end) < 0;
    }
}
