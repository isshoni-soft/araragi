package tv.isshoni.araragi.stream.model;

import tv.isshoni.araragi.data.Pair;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface IAraragiStream<T> extends Stream<T> {

    IAraragiStream<T> add(Collection<? extends T> collection);

    IAraragiStream<T> add(Supplier<Collection<? extends T>> collection);

    <R> IAraragiStream<R> expand(Class<R> to, Function<? super T, Collection<? extends R>>... collectionSupplier);

    IAraragiStream<T> expand(Function<? super T, Collection<? extends T>>... collectionSupplier);

    @Deprecated
    <R> IAraragiStream<R> cast(Class<R> clazz);

    @Deprecated
    <R> IAraragiStream<T> tempCast(Class<R> clazz, Consumer<IAraragiStream<R>> castedStreamConsumer);

    <F, S> IPairStream<F, S> mapToPair(Function<? super T, ? extends F> firstMapper, Function<? super T, ? extends S> secondMapper);

    <F, S> IPairStream<F, S> flatMapToPair(Function<? super T, ? extends Stream<? extends Pair<F, S>>> mapper);
}
