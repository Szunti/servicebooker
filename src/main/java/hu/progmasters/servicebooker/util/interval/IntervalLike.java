package hu.progmasters.servicebooker.util.interval;

public interface IntervalLike<I extends IntervalLike<I, T>, T extends Comparable<? super T>> {
    T getStart();

    T getEnd();

    default boolean isBefore(I other) {
        return this.getEnd().compareTo(other.getStart()) <= 0;
    }

    default boolean isAfter(I other) {
        return this.getStart().compareTo(other.getEnd()) >= 0;
    }

    default boolean isEmpty() {
        return getStart().equals(getEnd());
    }

    default boolean intersects(I other) {
        return !isBefore(other) && !isAfter(other);
    }

    default boolean contains(T value) {
        boolean atOrAfterStart = this.getStart().compareTo(value) <= 0;
        boolean beforeEnd = this.getEnd().compareTo(value) > 0;
        return atOrAfterStart && beforeEnd;
    }

    I intersect(I other);

    Pair<I> subtract(I other);
}
