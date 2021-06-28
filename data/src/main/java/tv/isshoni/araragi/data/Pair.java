package tv.isshoni.araragi.data;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class Pair<F, S> {

    public static <F extends Comparable<F>, S> Comparator<Pair<F, S>> compareFirst() {
        return Comparator.comparing(o -> o.first);
    }

    public static <F, S extends Comparable<S>> Comparator<Pair<F, S>> compareSecond() {
        return Comparator.comparing(o -> o.second);
    }

    private final F first;

    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Pair(Map.Entry<F, S> entry) {
        this.first = entry.getKey();
        this.second = entry.getValue();
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

    @Override
    public String toString() {
        return "Pair[first=" + this.first.toString() + ",second=" + this.second.toString() + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pair)) {
            return false;
        }

        Pair otherPair = (Pair) object;

        return this.first.equals(otherPair.first) && this.second.equals(otherPair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }
}
