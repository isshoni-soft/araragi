package tv.isshoni.araragi.data.collection.map;

import tv.isshoni.araragi.data.Pair;

import java.util.Map;

public interface ISubMap<K1, K2, V, M extends Map<K2, V>> extends Map<K1, M> {

    void put(K1 firstKey, Pair<K2, V> entry);

    M getOrDefault(K1 firstKey);

    void removeIn(K1 firstKey, K2 secondKey);

    boolean containsKey(K1 firstKey, K2 contains);

    boolean containsValue(K1 firstKey, V contains);
}
