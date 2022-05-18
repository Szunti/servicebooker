package hu.progmasters.servicebooker.domain.interval;

import java.util.Objects;

public class Pair<E> {
    private final E left;
    private final E right;

    public static <E> Pair<E> pair(E left, E right) {
        return new Pair<>(left, right);
    }

    private Pair(E left, E right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?> pair = (Pair<?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public String toString() {
        return "Pair(" + left + ", " + right + ")";
    }

    public E getLeft() {
        return left;
    }

    public E getRight() {
        return right;
    }
}