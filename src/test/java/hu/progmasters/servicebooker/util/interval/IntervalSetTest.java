package hu.progmasters.servicebooker.util.interval;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;
import static org.assertj.core.api.Assertions.*;

class IntervalSetTest {

    IntervalSet<Interval<Integer>, Integer> intervalSet = new IntervalSet<>();

    @Nested
    class Add {
        @Test
        void add_one() {
            intervalSet.add(interval(1, 2));
            assertThat(intervalSet)
                    .singleElement()
                    .isEqualTo(interval(1, 2));
        }

        @Test
        void add_multiple() {
            intervalSet.add(interval(1, 2));
            intervalSet.add(interval(3, 4));
            assertThat(intervalSet)
                    .containsExactly(interval(1, 2), interval(3, 4));
        }

        @Test
        void add_empty() {
            assertThatIllegalArgumentException().isThrownBy(() -> {
                intervalSet.add(interval(1, 1));
            });
        }

        @Test
        void add_overlapping() {
            addInterval(1, 4);
            assertThatIllegalStateException().isThrownBy(() -> {
                intervalSet.add(interval(3, 8));
            });
        }
    }

    @Nested
    class AddWithoutChecks {
        @Test
        void addAllWithoutChecks_collection() {
            addInterval(1, 4);
            intervalSet.addAllWithoutChecks(
                    List.of(interval(8, 10), interval(5, 8)));
            assertThat(intervalSet).isEqualTo(intervalSetOf(
                    interval(1, 4),
                    interval(5, 8),
                    interval(8, 10)
            ));
        }

        @Test
        void addAllWithoutChecks_intervalSet() {
            addInterval(1, 4);
            intervalSet.addAllWithoutChecks(intervalSetOf(
                    interval(8, 10),
                    interval(5, 8)
            ));
            assertThat(intervalSet).isEqualTo(intervalSetOf(
                    interval(1, 4),
                    interval(5, 8),
                    interval(8, 10)
            ));
        }
    }

    @Test
    void empty() {
        assertThat(intervalSet).isEmpty();
    }

    @Test
    void size() {
        assertThat(intervalSet.size()).isEqualTo(0);
        addInterval(1, 3);
        addInterval(4, 6);
        addInterval(8, 10);
        assertThat(intervalSet.size()).isEqualTo(3);
    }

    @Test
    void get() {
        class TestInterval implements IntervalLike<TestInterval, Integer> {
            @Override
            public Integer getStart() {
                return 1;
            }

            @Override
            public Integer getEnd() {
                return 4;
            }

            @Override
            public TestInterval intersect(TestInterval other) {
                return null;
            }

            @Override
            public Pair<TestInterval> subtract(TestInterval other) {
                return null;
            }
        }

        addInterval(1, 4);
        assertThat(intervalSet.get(new TestInterval())).isEqualTo(interval(1, 4));
    }

    @Nested
    class Intersect {
        @Test
        void intersect_withEmpty() {
            addInterval(1, 4);
            intersect();
            assertThat(intervalSet).isEmpty();
        }

        @Test
        void intersect_noOverlap() {
            addInterval(1, 4);
            intersect(interval(4, 7));
            assertThat(intervalSet).isEmpty();
        }

        @Test
        void intersect_overlap() {
            addInterval(1, 2);
            addInterval(3, 6);
            intersect(interval(2, 5));
            assertThat(intervalSet).containsExactly(interval(3, 5));
        }

        @Test
        void intersect_covering() {
            addInterval(1, 2);
            addInterval(3, 5);
            intersect(interval(2, 8));
            assertThat(intervalSet).containsExactly(interval(3, 5));
        }

        @Test
        void intersect_multiple() {
            addInterval(1, 4);
            addInterval(7, 10);
            intersect(
                    interval(2, 8),
                    interval(9, 10)
            );
            assertThat(intervalSet).isEqualTo(intervalSetOf(
                    interval(2, 4),
                    interval(7, 8),
                    interval(9, 10)
            ));
        }
    }

    @Nested
    class Subtract {
        @Test
        void subtract_noOverlap() {
            addInterval(1, 4);
            subtract(interval(4, 6));
            assertThat(intervalSet).containsExactly(interval(1, 4));
        }

        @Test
        void subtract_overlap() {
            addInterval(1, 4);
            subtract(interval(0, 2));
            assertThat(intervalSet).containsExactly(interval(2, 4));
        }

        @Test
        void subtract_overlapMultiple() {
            addInterval(1, 4);
            addInterval(5, 8);
            subtract(interval(3, 6));
            assertThat(intervalSet).containsExactly(interval(1, 3), interval(6, 8));
        }

        @Test
        void subtract_covering() {
            addInterval(1, 4);
            addInterval(5, 8);
            subtract(interval(1, 8));
            assertThat(intervalSet).isEmpty();
        }

        @Test
        void subtract_multiple() {
            addInterval(1, 4);
            subtract(
                    interval(0, 2),
                    interval(3, 5)
            );
            assertThat(intervalSet).containsExactly(interval(2, 3));
        }

        @Test
        void subtract_multipleContained() {
            addInterval(2, 8);
            addInterval(10, 18);
            subtract(
                    interval(3, 6),
                    interval(13, 15)
            );
            assertThat(intervalSet).isEqualTo(intervalSetOf(
                    interval(2, 3),
                    interval(6, 8),
                    interval(10, 13),
                    interval(15, 18)
            ));
        }

        @Test
        void subtract_fromEmpty() {
            subtract(interval(0, 2));
            assertThat(intervalSet).isEmpty();
        }

        @Test
        void subtract_subtractEmpty() {
            addInterval(1, 2);
            subtract();
            assertThat(intervalSet).containsExactly(interval(1, 2));
        }

        @Test
        void subtract_emptyFromEmpty() {
            subtract();
            assertThat(intervalSet).isEmpty();
        }
    }

    void addInterval(Integer start, Integer end) {
        Interval<Integer> interval = interval(start, end);
        intervalSet.addWithoutChecks(interval);
    }

    @SafeVarargs
    private IntervalSet<Interval<Integer>, Integer> intervalSetOf(Interval<Integer>... intervals) {
        IntervalSet<Interval<Integer>, Integer> intervalSetToCreate = new IntervalSet<>();
        for (Interval<Integer> interval : intervals) {
            intervalSetToCreate.addWithoutChecks(interval);
        }
        return intervalSetToCreate;
    }

    @SafeVarargs
    private void subtract(Interval<Integer>... intervals) {
        intervalSet.subtract(intervalSetOf(intervals));
    }

    @SafeVarargs
    private void intersect(Interval<Integer>... intervals) {
        intervalSet.intersect(intervalSetOf(intervals));
    }
}