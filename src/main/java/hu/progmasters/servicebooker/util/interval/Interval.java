package hu.progmasters.servicebooker.util.interval;

public interface Interval<T extends Comparable<? super T>> {
    T getStart();
    T getEnd();

    default boolean isBefore(Interval<T> other) {
        return this.getEnd().compareTo(other.getStart()) <= 0;
    }

    default boolean isAfter(Interval<T> other) {
        return this.getStart().compareTo(other.getEnd()) >= 0;
    }

    default boolean isEmpty() {
        return getStart().equals(getEnd());
    }

    default boolean intersects(Interval<T> other) {
        return !isBefore(other) && !isAfter(other);
    }

    Interval<T> intersect(Interval<T> other);

    Pair<Interval<T>> subtract(Interval<T> other);
}
