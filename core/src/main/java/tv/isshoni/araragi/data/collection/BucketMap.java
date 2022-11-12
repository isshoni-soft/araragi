package tv.isshoni.araragi.data.collection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BucketMap<K, V> implements Map<K, List<V>> {

    private final Map<K, List<V>> wrapped;

    public BucketMap(Map<K, List<V>> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int size() {
        return this.wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return this.wrapped.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.wrapped.values().stream()
                .flatMap(List::stream)
                .anyMatch(value::equals);
    }

    @Override
    public List<V> get(Object key) {
        return this.wrapped.get(key);
    }

    @Override
    public List<V> put(K key, List<V> value) {
        return this.wrapped.put(key, value);
    }

    public void add(K key, V value) {
        if (containsKey(key)) {
            get(key).add(value);
        } else {
            put(key, Lists.linkedListOf(value));
        }
    }

    @Override
    public List<V> remove(Object key) {
        return this.wrapped.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> m) {
        this.wrapped.putAll(m);
    }

    @Override
    public void clear() {
        this.wrapped.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.wrapped.keySet();
    }

    @Override
    public Collection<List<V>> values() {
        return this.wrapped.values();
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return this.wrapped.entrySet();
    }
}
