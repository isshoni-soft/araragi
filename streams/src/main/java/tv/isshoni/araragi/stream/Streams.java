package tv.isshoni.araragi.stream;

import tv.isshoni.araragi.data.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Streams {

    static <T> AraragiStream<T> to(Stream<T> stream) {
        return new AraragiStream<>(stream);
    }

    static <T> AraragiStream<T> to(Stream<T>... streams) {
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

    static <T> AraragiStream<T> to(Collection<T> collection) {
        return to(collection.stream());
    }

    static <P, T extends P> AraragiStream<P> to(Class<P> clazz, Collection<T> collection) {
        return to((Stream<P>) collection.stream());
    }

    static <F, S> PairStream<F, S> to(Map<F, S> map) {
        return new PairStream<>(map);
    }

    static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> collectPairsToMap() {
        return new SimpleCollector<>(HashMap::new,
                SimpleCollector.uniqKeysMapAccumulator(Pair::getFirst, Pair::getSecond),
                SimpleCollector.uniqKeysMapMerger(),
                SimpleCollector.CH_ID);
    }

    static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> collectPairsToUnmodifiableMap() {
        return Collectors.collectingAndThen(
                collectPairsToMap(),
                map -> Map.ofEntries(map.entrySet().toArray(new Map.Entry[0])));
    }
}
