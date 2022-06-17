package hu.progmasters.servicebooker.util.interval;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static hu.progmasters.servicebooker.util.interval.Interval.interval;
import static hu.progmasters.servicebooker.util.interval.Pair.pair;
import static org.assertj.core.api.Assertions.*;

class IntervalTest {

    @Nested
    class Creation {

        @Test
        void interval_nullFirstParam() {
            assertThatNullPointerException().isThrownBy(
                    () -> {
                        interval(null, 1);
                    }
            );
        }

        @Test
        void interval_nullSecondParam() {
            assertThatNullPointerException().isThrownBy(
                    () -> {
                        interval(3, null);
                    }
            );
        }

        @Test
        void interval_empty() {
            assertThat(interval(1, 1))
                    .extracting(Interval::isEmpty, as(BOOLEAN))
                    .isTrue();
        }

        @Test
        void interval_wrongOrder() {
            assertThatIllegalArgumentException().isThrownBy(
                    () -> {
                        interval(8, 5);
                    });
        }
    }

    @Nested
    class Intersection {
        @Test
        void intersect_intersecting() {
            Interval<Integer> first = interval(1, 6);
            Interval<Integer> second = interval(5, 8);
            Interval<Integer> intersection = interval(5, 6);

            assertThat(first.intersects(second)).isTrue();
            assertThat(second.intersects(first)).isTrue();
            assertThat(first.intersect(second)).isEqualTo(intersection);
            assertThat(second.intersect(first)).isEqualTo(intersection);
        }

        @Test
        void intersect_disjoint() {
            Interval<Integer> first = interval(1, 5);
            Interval<Integer> second = interval(6, 8);

            assertThat(first.intersects(second)).isFalse();
            assertThat(second.intersects(first)).isFalse();
            assertThat(first.intersect(second)).isEqualTo(null);
            assertThat(second.intersect(first)).isEqualTo(null);
        }

        @Test
        void intersect_touching() {
            Interval<Integer> first = interval(1, 5);
            Interval<Integer> second = interval(5, 8);

            assertThat(first.intersects(second)).isFalse();
            assertThat(second.intersects(first)).isFalse();
            assertThat(first.intersect(second)).isEqualTo(null);
            assertThat(second.intersect(first)).isEqualTo(null);
        }
    }

    @Nested
    class Contains {
        @Test
        void contains_inside() {
            assertThat(interval(1, 4).contains(2))
                    .isTrue();
        }

        @Test
        void contains_outside() {
            assertThat(interval(1, 4).contains(8))
                    .isFalse();
        }

        @Test
        void contains_start() {
            assertThat(interval(1, 4).contains(1))
                    .isTrue();
        }

        @Test
        void contains_end() {
            assertThat(interval(1, 4).contains(4))
                    .isFalse();
        }
    }

    @Nested
    class Subtract {
        @Test
        void subtract_contained() {
            Interval<Integer> minuend = interval(-2, 5);
            Interval<Integer> subtrahend = interval(3, 4);
            Pair<Interval<Integer>> difference = pair(interval(-2, 3), interval(4, 5));

            assertThat(minuend.subtract(subtrahend))
                    .isEqualTo(difference);
        }

        @Test
        void subtract_intersectingFromLeft() {
            Interval<Integer> minuend = interval(-2, 5);
            Interval<Integer> subtrahend = interval(-3, 0);
            Pair<Interval<Integer>> difference = pair(null, interval(0, 5));

            assertThat(minuend.subtract(subtrahend))
                    .isEqualTo(difference);
        }


        @Test
        void subtract_startingFromLeft() {
            Interval<Integer> minuend = interval(-2, 5);
            Interval<Integer> subtrahend = interval(-2, 0);
            Pair<Interval<Integer>> difference = pair(null, interval(0, 5));

            assertThat(minuend.subtract(subtrahend))
                    .isEqualTo(difference);
        }

        @Test
        void subtract_intersectingFromRight() {
            Interval<Integer> minuend = interval(-2, 5);
            Interval<Integer> subtrahend = interval(3, 8);
            Pair<Interval<Integer>> difference = pair(interval(-2, 3), null);

            assertThat(minuend.subtract(subtrahend))
                    .isEqualTo(difference);
        }

        @Test
        void subtract_disjointBefore() {
            Interval<Integer> minuend = interval(-2, 5);
            Interval<Integer> subtrahend = interval(-8, -4);
            Pair<Interval<Integer>> difference = pair(null, interval(-2, 5));

            assertThat(minuend.subtract(subtrahend))
                    .isEqualTo(difference);
        }

        @Test
        void subtract_disjointAfter() {
            Interval<Integer> minuend = interval(-2, 5);
            Interval<Integer> subtrahend = interval(8, 10);
            Pair<Interval<Integer>> difference = pair(interval(-2, 5), null);

            assertThat(minuend.subtract(subtrahend))
                    .isEqualTo(difference);
        }
    }

    @Nested
    class BeforeAfter {
        @Test
        void beforeAfter_disjoint() {
            Interval<Integer> first = interval(2, 6);
            Interval<Integer> second = interval(7, 9);
            assertThat(first.isBefore(second)).isTrue();
            assertThat(second.isBefore(first)).isFalse();
            assertThat(second.isAfter(first)).isTrue();
            assertThat(first.isAfter(second)).isFalse();
        }

        @Test
        void beforeAfter_intersecting() {
            Interval<Integer> first = interval(2, 6);
            Interval<Integer> second = interval(5, 9);
            assertThat(first.isBefore(second)).isFalse();
            assertThat(second.isBefore(first)).isFalse();
            assertThat(second.isAfter(first)).isFalse();
            assertThat(first.isAfter(second)).isFalse();
        }

        @Test
        void beforeAfter_touching() {
            Interval<Integer> first = interval(2, 6);
            Interval<Integer> second = interval(6, 9);
            assertThat(first.isBefore(second)).isTrue();
            assertThat(second.isBefore(first)).isFalse();
            assertThat(second.isAfter(first)).isTrue();
            assertThat(first.isAfter(second)).isFalse();
        }
    }

}