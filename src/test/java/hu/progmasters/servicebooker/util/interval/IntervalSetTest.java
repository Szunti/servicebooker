package hu.progmasters.servicebooker.util.interval;

import org.junit.jupiter.api.Test;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;
import static org.assertj.core.api.Assertions.assertThat;

class IntervalSetTest {

    IntervalSet<Integer> intervalSet = new IntervalSet<>();

    @Test
    void test_initial_empty() {
        assertThat(intervalSet).isEmpty();
    }

    @Test
    void testAdd_one_added() {
        addInterval(1, 2);
        assertThat(intervalSet)
                .singleElement()
                .isEqualTo(interval(1, 2));
    }

    @Test
    void testAdd_multiple_allAdded() {
        addInterval(1, 2);
        addInterval(3, 4);
        assertThat(intervalSet)
                .containsExactly(
                        interval(1, 2),
                        interval(3, 4)
                );
    }

    @Test
    void tesAdd_zeroLength_notAdded() {
        addInterval(1, 2);
        addInterval(0, 0);
        assertThat(intervalSet)
                .containsExactly(interval(1, 2));
    }

    @Test
    void testSubtract_noOverlap_unchanged() {
        addInterval(1, 4);
        subtract(interval(4, 6));
        assertThat(intervalSet)
                .containsExactly(interval(1, 4));
    }

    @Test
    void testSubtract_overlap_changed() {
        addInterval(1, 4);
        subtract(interval(0, 2));
        assertThat(intervalSet)
                .containsExactly(interval(2, 4));
    }

    @Test
    void testSubtract_overlapMultiple_allChanged() {
        addInterval(1, 4);
        addInterval(5, 8);
        subtract(interval(3, 6));
        assertThat(intervalSet)
                .containsExactly(interval(1, 3), interval(6, 8));
    }

    @Test
    void testSubtract_covering_empty() {
        addInterval(1, 4);
        addInterval(5, 8);
        subtract(interval(1, 8));
        assertThat(intervalSet)
                .isEmpty();
    }

    @Test
    void testSubtract_subtractMultiple_changed() {
        addInterval(1, 4);
        subtract(interval(0, 2), interval(3, 5));
        assertThat(intervalSet)
                .containsExactly(interval(2, 3));
    }

    @Test
    void testSubtract_fromEmpty_empty() {
        subtract(interval(0, 2));
        assertThat(intervalSet)
                .isEmpty();
    }

    @Test
    void testSubtract_subtractEmpty_unchanged() {
        addInterval(1, 2);
        subtract();
        assertThat(intervalSet)
                .containsExactly(interval(1, 2));
    }

    @Test
    void testSubtract_emptyFromEmpty_empty() {
        subtract();
        assertThat(intervalSet)
                .isEmpty();
    }

    @Test
    void testIntersect_withEmpty_empty() {
        addInterval(1, 4);
        intersect();
        assertThat(intervalSet)
                .isEmpty();
    }

    @Test
    void testIntersect_noOverlap_empty() {
        addInterval(1, 4);
        intersect(interval(4, 7));
        assertThat(intervalSet)
                .isEmpty();
    }

    @Test
    void testIntersect_overlap_changed() {
        addInterval(1, 2);
        addInterval(3, 6);
        intersect(interval(2, 5));
        assertThat(intervalSet)
                .containsExactly(interval(3, 5));
    }

    @Test
    void testIntersect_covering_changed() {
        addInterval(1, 2);
        addInterval(3, 5);
        intersect(interval(2, 8));
        assertThat(intervalSet)
                .containsExactly(interval(3, 5));
    }

    @Test
    void testIntersect_multiple_changed() {
        addInterval(1, 4);
        addInterval(7, 10);
        intersect(interval(2, 8), interval(9, 10));
        assertThat(intervalSet)
                .containsExactly(interval(2, 4), interval(7, 8), interval(9, 10));
    }

    @Test
    void testAddWithOverwrite_addMultiple_changed() {
        addInterval(1, 4);
        addInterval(7, 10);
        addWithOverWrite(interval(4, 8), interval(9, 10));
        assertThat(intervalSet)
                .containsExactly(interval(1, 4), interval(4, 8), interval(8, 9), interval(9, 10));
    }

    void addInterval(Integer start, Integer end) {
        Interval<Integer> interval = interval(start, end);
        intervalSet.addAssumingNoOverlap(interval);
    }

    @SafeVarargs
    private void subtract(Interval<Integer>... intervals) {
        intervalSet.subtract(new IntervalSet<>(intervals));
    }

    @SafeVarargs
    private void intersect(Interval<Integer>... intervals) {
        intervalSet.intersect(new IntervalSet<>(intervals));
    }

    @SafeVarargs
    private void addWithOverWrite(Interval<Integer>... intervals) {
        intervalSet.addWithOverwrite(new IntervalSet<>(intervals));
    }
}