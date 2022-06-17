package hu.progmasters.servicebooker.util.interval;

import java.util.*;

/**
 * A set of intervals with common set operations.
 * <p>
 * Intervals in the set are not overlapping. The set will never contain empty (0 length) intervals.
 *
 * @param <T> the type the intervals are going over
 */
public class IntervalSet<I extends IntervalLike<I, T>, T extends Comparable<? super T>> extends AbstractCollection<I> {

    private final NavigableMap<T, I> intervalMap;

    public IntervalSet() {
        this.intervalMap = new TreeMap<>();
    }

    public IntervalSet(Collection<? extends I> intervals) {
        this();
        addAll(intervals);
    }

    /**
     * A constructor to reuse a {@link NavigableMap}
     *
     * @param intervalMap the {@link NavigableMap} to reuse
     */
    private IntervalSet(NavigableMap<T, I> intervalMap) {
        this.intervalMap = intervalMap;
    }

    @Override
    public Iterator<I> iterator() {
        return intervalMap.values().iterator();
    }

    @Override
    public int size() {
        return intervalMap.size();
    }

    @Override
    public boolean add(I interval) {
        Objects.requireNonNull(interval);
        if (interval.isEmpty()) {
            throw new EmptyIntervalException(interval);
        }
        if (!getIntervalSetIntersecting(interval).isEmpty()) {
            throw new OverlappingIntervalException(interval);
        }
        addWithoutChecks(interval);
        return true;
    }

    public boolean removeInterval(I interval) {
        return intervalMap.remove(interval.getStart(), interval);
    }

    public boolean removeAllIntervals(Collection<I> intervals) {
        boolean anyRemoved = false;
        for (I interval : intervals) {
            anyRemoved |= removeInterval(interval);
        }
        return anyRemoved;
    }

    @Override
    public void clear() {
        intervalMap.clear();
    }

    /**
     * Adds an interval assuming it is not null, not empty and there is no overlap with the current ones.
     * <p>
     * Main motivation is performance. Could be useful when adding the intervals from another {@link IntervalSet}.
     *
     * @param interval the interval to add
     * @return true
     */
    public boolean addWithoutChecks(I interval) {
        intervalMap.put(interval.getStart(), interval);
        return true;
    }

    public boolean addAllWithoutChecks(Collection<I> intervals) {
        if (intervals.isEmpty()) {
            return false;
        }
        for (I interval : intervals) {
            intervalMap.put(interval.getStart(), interval);
        }
        return true;
    }

    public boolean addAllWithoutChecks(IntervalSet<I, T> intervalSet) {
        if (intervalSet.isEmpty()) {
            return false;
        }
        intervalMap.putAll(intervalSet.intervalMap);
        return true;
    }

    public IntervalSet<I, T> intersect(IntervalSet<I, T> other) {

        List<I> toAdd = new ArrayList<>();

        for (I thisInterval : this) {
            IntervalSet<I, T> otherIntersecting = other.getIntervalSetIntersecting(thisInterval);
            for (I otherInterval : otherIntersecting) {
                I intersection = thisInterval.intersect(otherInterval);
                if (intersection != null) {
                    toAdd.add(intersection);
                }
            }
        }
        clear();
        addAllWithoutChecks(toAdd);
        return this;
    }

    public IntervalSet<I, T> subtract(IntervalSet<I, T> other) {

        List<I> toRemove = new ArrayList<>();
        List<I> toAdd = new ArrayList<>();

        for (I thisInterval : this) {
            IntervalSet<I, T> otherIntersecting = other.getIntervalSetIntersecting(thisInterval);

            I remaining = thisInterval;
            for (I otherInterval : otherIntersecting) {
                assert remaining != null;
                Pair<I> difference = remaining.subtract(otherInterval);

                I leftRemaining = difference.getLeft();
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
        removeAllIntervals(toRemove);
        addAllWithoutChecks(toAdd);
        return this;
    }

    private IntervalSet<I, T> getIntervalSetIntersecting(I interval) {
        return new IntervalSet<>(getSubMapIntersecting(interval));
    }

    private NavigableMap<T, I> getSubMapIntersecting(I interval) {
        if (interval == null || interval.isEmpty()) {
            return Collections.emptyNavigableMap();
        }

        T start = interval.getStart();
        T end = interval.getEnd();

        I lastCandidate = getIntervalFromEntry(intervalMap.lowerEntry(end));
        if (lastCandidate == null) {
            return Collections.emptyNavigableMap();
        }
        // by this point there must be an interval starting before interval.getEnd()
        I firstIntersecting = null;

        I floorInterval = getIntervalFromEntry(intervalMap.floorEntry(start));
        if (floorInterval == null) {
            assert !intervalMap.isEmpty();
            firstIntersecting = getIntervalFromEntry(intervalMap.firstEntry());
            assert firstIntersecting.intersects(interval);
        } else if (floorInterval.intersects(interval)) {
            firstIntersecting = floorInterval;
        } else {
            assert !floorInterval.getStart().equals(start);
            I nextInterval = getIntervalFromEntry(intervalMap.higherEntry(start));
            if (nextInterval != null && nextInterval.intersects(interval)) {
                firstIntersecting = nextInterval;
            }
        }

        return firstIntersecting == null ? Collections.emptyNavigableMap() :
                intervalMap.subMap(firstIntersecting.getStart(), true, lastCandidate.getStart(), true);
    }

    private I getIntervalFromEntry(Map.Entry<T, I> entry) {
        return entry == null ? null : entry.getValue();
    }
}
