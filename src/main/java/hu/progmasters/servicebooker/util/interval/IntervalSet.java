package hu.progmasters.servicebooker.util.interval;

import java.util.*;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;

/**
 * A set of intervals with common set operations.
 * <p>
 * Intervals in the set are not overlapping. The set will never contain 0 length intervals.
 *
 * @param <T> the type the intervals are going over
 */
public class IntervalSet<T extends Comparable<? super T>> extends AbstractCollection<Interval<T>> {
    private static final Comparator<? extends Interval<?>> intervalStartComparator = getIntervalStartComparator();

    private static <T extends Comparable<? super T>> Comparator<Interval<T>> getIntervalStartComparator() {
        return Comparator.comparing(Interval::getStart);
    }

    private final NavigableSet<Interval<T>> intervalTree;

    public IntervalSet() {
        // all elements compared will be of type Interval<T>, can be suppressed safely
        @SuppressWarnings("unchecked")
        NavigableSet<Interval<T>> intervalTree = new TreeSet<>((Comparator<Interval<T>>) intervalStartComparator);
        this.intervalTree = intervalTree;
    }

    @SafeVarargs
    IntervalSet(Interval<T>... intervals) {
        this();
        for (Interval<T> interval : intervals) {
            addAssumingNoOverlap(interval);
        }
    }

    /**
     * A constructor to reuse a {@link NavigableSet}
     *
     * @param intervalTree the {@link NavigableSet} to reuse
     */
    private IntervalSet(NavigableSet<Interval<T>> intervalTree) {
        this.intervalTree = intervalTree;
    }

    public Iterator<Interval<T>> iterator() {
        return new DelegatingIteratorWithoutRemove<>(intervalTree.iterator());
    }

    private static class DelegatingIteratorWithoutRemove<T extends Comparable<? super T>> implements Iterator<Interval<T>> {
        private final Iterator<Interval<T>> underlyingIterator;

        public DelegatingIteratorWithoutRemove(Iterator<Interval<T>> underlyingIterator) {
            this.underlyingIterator = underlyingIterator;
        }

        @Override
        public boolean hasNext() {
            return underlyingIterator.hasNext();
        }

        @Override
        public Interval<T> next() {
            return underlyingIterator.next();
        }
    }

    @Override
    public int size() {
        return intervalTree.size();
    }

    /**
     * Adds an interval assuming there is no overlap with the current ones.
     * <p>
     * Main motivation is performance. Could be useful when adding the intervals from another {@link IntervalSet}.
     *
     * @param interval the interval to add
     * @return this interval set
     */
    public IntervalSet<T> addAssumingNoOverlap(Interval<T> interval) {
        Objects.requireNonNull(interval);
        if (!interval.isEmpty()) {
            intervalTree.add(interval);
        }
        return this;
    }

    public IntervalSet<T> addAssumingNoOverlap(IntervalSet<T> intervalSet) {
        Objects.requireNonNull(intervalSet);
        for (Interval<T> interval : intervalSet) {
            addAssumingNoOverlap(interval);
        }
        return this;
    }

    /**
     * Adds intervals from another interval set narrowing existing regions when there is an overlap.
     *
     * @param other the interval set containing the intervals to add
     * @return this interval set
     */
    public IntervalSet<T> addWithOverwrite(IntervalSet<T> other) {
        subtract(other);
        intervalTree.addAll(other);
        return this;
    }

    public IntervalSet<T> intersect(IntervalSet<T> other) {
        IntervalSet<T> thisNarrowed = this.getSubsetIntersecting(other.getCoveringInterval());
        IntervalSet<T> otherNarrowed = other.getSubsetIntersecting(this.getCoveringInterval());

        List<Interval<T>> toAdd = new ArrayList<>();

        for (Interval<T> thisInterval : thisNarrowed) {
            IntervalSet<T> otherIntersecting = otherNarrowed.getSubsetIntersecting(thisInterval);
            for (Interval<T> otherInterval : otherIntersecting) {
                Interval<T> intersection = thisInterval.intersect(otherInterval);
                if (intersection != null) {
                    toAdd.add(intersection);
                }
            }
        }
        intervalTree.clear();
        intervalTree.addAll(toAdd);
        return this;
    }

    public IntervalSet<T> subtract(IntervalSet<T> other) {
        IntervalSet<T> thisNarrowed = this.getSubsetIntersecting(other.getCoveringInterval());
        IntervalSet<T> otherNarrowed = other.getSubsetIntersecting(this.getCoveringInterval());

        List<Interval<T>> toRemove = new ArrayList<>();
        List<Interval<T>> toAdd = new ArrayList<>();

        for (Interval<T> thisInterval : thisNarrowed) {
            IntervalSet<T> otherIntersecting = otherNarrowed.getSubsetIntersecting(thisInterval);

            Interval<T> remaining = thisInterval;
            for (Interval<T> otherInterval : otherIntersecting) {
                assert remaining != null;
                Pair<Interval<T>> difference = remaining.subtract(otherInterval);

                Interval<T> leftRemaining = difference.getLeft();
                if (leftRemaining != null) {
                    toAdd.add(leftRemaining);
                }
                remaining = difference.getRight();
            }

            if (remaining != null) {
                toAdd.add(remaining);
            }
            toRemove.add(thisInterval);
        }
        toRemove.forEach(intervalTree::remove);
        intervalTree.addAll(toAdd);
        return this;
    }

    /**
     * Gets an interval that covers the whole set.
     *
     * @return the covering interval
     */
    public Interval<T> getCoveringInterval() {
        if (intervalTree.isEmpty()) {
            return null;
        } else {
            T firstStart = intervalTree.first().getStart();
            T lastEnd = intervalTree.last().getEnd();
            return interval(firstStart, lastEnd);
        }
    }

    private IntervalSet<T> getSubsetIntersecting(Interval<T> interval) {
        return new IntervalSet<>(getSubTreeIntersecting(interval));
    }

    private NavigableSet<Interval<T>> getSubTreeIntersecting(Interval<T> interval) {
        if (interval == null || interval.isEmpty()) {
            return Collections.emptyNavigableSet();
        }

        T start = interval.getStart();
        T end = interval.getEnd();

        Interval<T> lastCandidate = intervalTree.lower(interval(end, end));
        if (lastCandidate == null) {
            return Collections.emptyNavigableSet();
        }
        // by this point there must be an interval starting before interval.getEnd()
        Interval<T> firstIntersecting = null;

        Interval<T> floorInterval = intervalTree.floor(interval(start, start));
        if (floorInterval == null) {
            assert !intervalTree.isEmpty();
            firstIntersecting = intervalTree.first();
            assert firstIntersecting.intersects(interval);
        } else if (floorInterval.intersects(interval)) {
            firstIntersecting = floorInterval;
        } else {
            assert !floorInterval.getStart().equals(start);
            Interval<T> nextInterval = intervalTree.higher(floorInterval);
            if (nextInterval != null && nextInterval.intersects(interval)) {
                firstIntersecting = nextInterval;
            }
        }

        return  firstIntersecting == null ? Collections.emptyNavigableSet() :
                intervalTree.subSet(firstIntersecting, true, lastCandidate, true);
    }
}
