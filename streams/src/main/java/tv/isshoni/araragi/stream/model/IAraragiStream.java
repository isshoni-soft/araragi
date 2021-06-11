package tv.isshoni.araragi.stream.model;

import tv.isshoni.araragi.stream.AraragiStream;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface IAraragiStream<T> extends Stream<T> {

    AraragiStream<T> add(Collection<? extends T> collection);

    @Deprecated
    AraragiStream<T> add(Supplier<Collection<? extends T>> collection);

    <R> AraragiStream<R> expand(Class<R> to, Function<? super T, Collection<? extends R>>... collectionSupplier);

    @Deprecated
    AraragiStream<T> expand(Function<? super T, Collection<? extends T>>... collectionSupplier);

    @Deprecated
    <R> AraragiStream<R> cast(Class<R> clazz);

    @Deprecated
    <R> AraragiStream<T> tempCast(Class<R> clazz, Consumer<AraragiStream<R>> castedStreamConsumer);
}
