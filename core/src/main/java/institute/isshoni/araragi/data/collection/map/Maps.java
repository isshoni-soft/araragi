package institute.isshoni.araragi.data.collection.map;

import institute.isshoni.araragi.data.Pair;
import institute.isshoni.araragi.stream.Streams;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Maps {

    @SafeVarargs
    public static <F, S> Map<F, S> ofPairs(Pair<F, S>... pairs) {
        return Streams.to(pairs)
                .toMap();
    }

    public static <F, S> Map<F, S> ofPairs(Collection<Pair<F, S>> pairs) {
        return Streams.to(pairs)
                .mapToPair(Pair.first(), Pair.second())
                .toMap();
    }

    public static <F, S> BucketMap<F, S> bucket(Map<F, List<S>> bucketed) {
        return new BucketMap<>(bucketed);
    }
}
