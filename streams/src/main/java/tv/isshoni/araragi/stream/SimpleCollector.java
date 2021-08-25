package tv.isshoni.araragi.stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * This is taken from the JDK because it was all private scoped, but I needed it.
 *
 * This code is excluded from any project-level licenses and is under the license that the JDK uses.
 * (I'll remove this if these helper objects became publicly referencable pretty please)
 */
public class SimpleCollector<T, A, R> implements Collector<T, A, R> {

    public static final Set<Collector.Characteristics> CH_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Function<A, R> finisher;
    private final Set<Characteristics> characteristics;

    public SimpleCollector(Supplier<A> supplier,
                           BiConsumer<A, T> accumulator,
                           BinaryOperator<A> combiner,
                           Function<A,R> finisher,
                           Set<Characteristics> characteristics) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
        this.characteristics = characteristics;
    }

    public SimpleCollector(Supplier<A> supplier,
                           BiConsumer<A, T> accumulator,
                           BinaryOperator<A> combiner,
                           Set<Characteristics> characteristics) {
        this(supplier, accumulator, combiner, castingIdentity(), characteristics);
    }

    @Override
    public BiConsumer<A, T> accumulator() {
        return accumulator;
    }

    @Override
    public Supplier<A> supplier() {
        return supplier;
    }

    @Override
    public BinaryOperator<A> combiner() {
        return combiner;
    }

    @Override
    public Function<A, R> finisher() {
        return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return characteristics;
    }

    public static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    public static <T, K, V> BiConsumer<Map<K, V>, T> uniqKeysMapAccumulator(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return (map, element) -> {
            K k = keyMapper.apply(element);
            V v = Objects.requireNonNull(valueMapper.apply(element));
            V u = map.putIfAbsent(k, v);
            if (u != null) throw duplicateKeyException(k, u, v);
        };
    }

    public static <K, V, M extends Map<K,V>> BinaryOperator<M> uniqKeysMapMerger() {
        return (m1, m2) -> {
            for (Map.Entry<K,V> e : m2.entrySet()) {
                K k = e.getKey();
                V v = Objects.requireNonNull(e.getValue());
                V u = m1.putIfAbsent(k, v);
                if (u != null) throw duplicateKeyException(k, u, v);
            }
            return m1;
        };
    }

    public static IllegalStateException duplicateKeyException(
            Object k, Object u, Object v) {
        return new IllegalStateException(String.format(
                "Duplicate key %s (attempted merging values %s and %s)",
                k, u, v));
    }
}
