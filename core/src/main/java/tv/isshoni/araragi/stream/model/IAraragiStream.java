package tv.isshoni.araragi.stream.model;

import tv.isshoni.araragi.data.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface IAraragiStream<T> extends Stream<T> {

    boolean matches(Stream<T> stream, BiFunction<T, T, Boolean> matcher);

    boolean matches(List<T> other, BiFunction<T, T, Boolean> matcher);

    IAraragiStream<T> filterInverted(Predicate<? super T> predicate);

    IAraragiStream<T> add(Collection<? extends T> collection);

    IAraragiStream<T> add(Supplier<Collection<? extends T>> collection);

    <R> IAraragiStream<R> expand(Class<R> to, Function<? super T, Collection<? extends R>>... collectionSupplier);

    IAraragiStream<T> expand(Function<? super T, Collection<? extends T>>... collectionSupplier);

    <R> IAraragiStream<R> cast(Class<R> clazz);

    <F, S> IPairStream<F, S> mapToPair(Function<? super T, ? extends F> firstMapper, Function<? super T, ? extends S> secondMapper);

    <F, S> IPairStream<F, S> flatMapToPair(Function<? super T, ? extends Stream<? extends Pair<F, S>>> mapper);

    <R> R collapse(BiFunction<? super T, R, R> mapper);

    <R> R collapse(BiFunction<? super T, R, R> mapper, R first);

    Optional<T> find(Predicate<T>... selectors);

    Optional<T> find(Predicate<T> selector, Function<IAraragiStream<T>, Optional<T>> otherwise);

    Optional<T> find(Predicate<T> selector, Optional<T> otherwise);

    Optional<T> find(Predicate<T> selector, Supplier<Optional<T>> otherwise);

    Optional<T> findLast();

    void addTo(Collection<T> collection);

    void addToOrdered(Collection<T> collection);
}
