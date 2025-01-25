package institute.isshoni.araragi.stream;

import institute.isshoni.araragi.data.Pair;
import institute.isshoni.araragi.stream.model.IAraragiStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Streams {

    private Streams() { }

    public static <F, S> Function<Collection<Pair<F, S>>, PairStream<F, S>> collectionToPairStream() {
        return PairStream::new;
    }

    public static <F, S> Function<Pair<F, S>[], PairStream<F, S>> arrayToPairStream() {
        return PairStream::new;
    }

    public static <T> Function<Collection<T>, AraragiStream<T>> collectionToAraragiStream() {
        return AraragiStream::new;
    }

    public static <F, S> Function<Stream<Pair<F, S>>, PairStream<F, S>> streamToPairStream() {
        return PairStream::new;
    }

    public static <T> Function<Stream<T>, AraragiStream<T>> streamToAraragiStream() {
        return AraragiStream::new;
    }

    public static <T> AraragiStream<T> to(Stream<T> stream) {
        return new AraragiStream<>(stream);
    }

    public static <T> AraragiStream<T> to(Stream<T>... streams) {
        Stream<T> result = null;

        for (Stream<T> stream : streams) {
            if (result == null) {
                result = stream;
            } else {
                result = Stream.concat(result, stream);
            }
        }

        return to(result);
    }

    public static <T, S extends IAraragiStream<T>> S to(T[] array, Function<Collection<T>, S> constructor) {
        return constructor.apply(Arrays.asList(array));
    }

    public static <T> AraragiStream<T> to(T[] array) {
        return to(Arrays.stream(array));
    }

    public static <T, S extends IAraragiStream<T>> S to(Collection<T> collection, Function<Collection<T>, S> constructor) {
        return constructor.apply(collection);
    }

    public static <T> AraragiStream<T> to(Collection<T> collection) {
        return to(collection.stream());
    }

    public static <P, T extends P, S extends IAraragiStream<P>> S to(Class<P> clazz, Collection<T> collection, Function<Collection<P>, S> constructor) {
        return to((Collection<P>) collection, constructor);
    }

    public static <F, S> PairStream<F, S> to(Map<F, S> map) {
        return new PairStream<>(map);
    }

    public static <F, S> PairStream<F, S> to(Pair<F, S>[] array) {
        return new PairStream<>(array);
    }

    // TODO: Rewrite me so that I don't need to rely on SimpleCollector, as it was shortcut.
    public static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> collectPairsToMap() {
        return new SimpleCollector<>(HashMap::new,
                SimpleCollector.uniqKeysMapAccumulator(Pair::getFirst, Pair::getSecond),
                SimpleCollector.uniqKeysMapMerger(),
                SimpleCollector.CH_ID);
    }

    public static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> collectPairsToUnmodifiableMap() {
        return Collectors.collectingAndThen(
                collectPairsToMap(),
                map -> Map.ofEntries(map.entrySet().toArray(new Map.Entry[0])));
    }
}
