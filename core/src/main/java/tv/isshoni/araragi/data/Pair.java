package tv.isshoni.araragi.data;

import tv.isshoni.araragi.stream.Streams;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Pair<F, S> {

    public static <F extends Comparable<F>, S> Comparator<Pair<F, S>> compareFirst() {
        return Comparator.comparing(o -> o.first);
    }

    public static <F, S extends Comparable<S>> Comparator<Pair<F, S>> compareSecond() {
        return Comparator.comparing(o -> o.second);
    }

    public static <F> Function<Pair<F, ?>, F> first() {
        return Pair::getFirst;
    }

    public static <S> Function<Pair<?, S>, S> second() {
        return Pair::getSecond;
    }

    @SafeVarargs
    public static <F extends Comparable<F>, S> Map<F, S> toMap(Pair<F, S>... pairs) {
        return Streams.to(pairs)
                .toMap();
    }

    public static <F extends Comparable<F>, S> Map<F, S> toMap(Collection<Pair<F, S>> pairs) {
        return Streams.to(pairs)
                .mapToPair(first(), second())
                .toMap();
    }

    private F first;

    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Pair(Map.Entry<F, S> entry) {
        this.first = entry.getKey();
        this.second = entry.getValue();
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

    public <NF, NS> Pair<NF, NS> map(BiFunction<F, S, Pair<NF, NS>> mapper) {
        return mapper.apply(this.first, this.second);
    }

    public <NF> Pair<NF, S> mapFirst(BiFunction<F, S, NF> mapper) {
        return new Pair<>(mapper.apply(this.first, this.second), this.second);
    }

    public <NS> Pair<F, NS> mapSecond(BiFunction<F, S, NS> mapper) {
        return new Pair<>(this.first, mapper.apply(this.first, this.second));
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
