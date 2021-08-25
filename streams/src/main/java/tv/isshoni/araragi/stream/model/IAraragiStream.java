package tv.isshoni.araragi.stream.model;

import tv.isshoni.araragi.data.Pair;

import java.util.Collection;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

public interface IAraragiStream<T> extends Stream<T> {

    IAraragiStream<T> add(Collection<? extends T> collection);

    IAraragiStream<T> add(Supplier<Collection<? extends T>> collection);

    <R> IAraragiStream<R> expand(Class<R> to, Function<? super T, Collection<? extends R>>... collectionSupplier);

    IAraragiStream<T> expand(Function<? super T, Collection<? extends T>>... collectionSupplier);

    <R> IAraragiStream<R> cast(Class<R> clazz);

    <F, S> IPairStream<F, S> mapToPair(Function<? super T, ? extends F> firstMapper, Function<? super T, ? extends S> secondMapper);

    <F, S> IPairStream<F, S> flatMapToPair(Function<? super T, ? extends Stream<? extends Pair<F, S>>> mapper);

    Object collapse(BiFunction<? super T, Object, Object> mapper);

    Optional<T> find(Predicate<T> selector, Function<IAraragiStream<T>, Optional<T>> otherwise);
}
