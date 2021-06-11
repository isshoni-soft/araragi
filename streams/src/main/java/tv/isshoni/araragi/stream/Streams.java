package tv.isshoni.araragi.stream;

import java.util.Collection;
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
}
