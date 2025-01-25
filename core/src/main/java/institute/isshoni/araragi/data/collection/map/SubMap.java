package institute.isshoni.araragi.data.collection.map;

import institute.isshoni.araragi.data.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SubMap<K1, K2, V, M extends Map<K2, V>> implements ISubMap<K1, K2, V, M> {

    private final Map<K1, M> map;

    private final Supplier<M> mapSupplier;

    public SubMap(Supplier<M> mapSupplier) {
        this.map = new HashMap<>();
        this.mapSupplier = mapSupplier;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public M get(Object key) {
        return this.map.get(key);
    }

    @Override
    public M put(K1 key, M value) {
        return this.map.put(key, value);
    }

    @Override
    public M remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K1, ? extends M> m) {
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<K1> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<M> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<K1, M>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public void put(K1 firstKey, Pair<K2, V> entry) {
        if (this.map.containsKey(firstKey)) {
            this.map.get(firstKey).put(entry.getFirst(), entry.getSecond());
        } else {
            M insert = this.mapSupplier.get();
            insert.put(entry.getFirst(), entry.getSecond());

            this.map.put(firstKey, insert);
        }
    }

    @Override
    public M getOrDefault(K1 firstKey) {
        if (!this.map.containsKey(firstKey)) {
            this.map.put(firstKey, this.mapSupplier.get());
        }

        return this.map.get(firstKey);
    }

    @Override
    public void removeIn(K1 firstKey, K2 secondKey) {
        this.map.getOrDefault(firstKey, this.mapSupplier.get()).remove(secondKey);
    }

    @Override
    public boolean containsKey(K1 firstKey, K2 contains) {
        return this.map.getOrDefault(firstKey, this.mapSupplier.get()).containsKey(contains);
    }

    @Override
    public boolean containsValue(K1 firstKey, V contains) {
        return this.map.getOrDefault(firstKey, this.mapSupplier.get()).containsValue(contains);
    }
}
