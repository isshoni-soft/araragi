package tv.isshoni.araragi.stream.model;

import tv.isshoni.araragi.data.Pair;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IPairStream<F, S> extends IAraragiStream<Pair<F, S>> {

    <NF> IPairStream<NF, S> mapFirst(Function<? super F, ? extends NF> mapper);

    <NS> IPairStream<F, NS> mapSecond(Function<? super S, ? extends NS> mapper);

    <NF, NS> IPairStream<NF, NS> map(Function<? super F, ? extends NF> firstMapper, Function<? super S, ? extends NS> secondMapper);

    <R> IAraragiStream<R> map(BiFunction<? super F, ? super S, ? extends R> mapper);

    Map<F, S> toMap();

    Map<F, S> toUnmodifiableMap();

    void forEach(BiConsumer<? super F, ? super S> consumer);

    void forEachOrdered(BiConsumer<? super F, ? super S> consumer);
}
