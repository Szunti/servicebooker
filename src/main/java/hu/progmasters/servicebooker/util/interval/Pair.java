package hu.progmasters.servicebooker.util.interval;

import lombok.ToString;
import lombok.Value;

// Don't use staticConstructor element of Value, generated factory methods can't be statically imported
@Value
@ToString(includeFieldNames = false)
public class Pair<E> {
    E left;
    E right;

    public static <E> Pair<E> pair(E left, E right) {
        return new Pair<>(left, right);
    }

    private Pair(E left, E right) {
        this.left = left;
        this.right = right;
    }
}