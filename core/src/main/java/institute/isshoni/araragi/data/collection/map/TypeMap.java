package institute.isshoni.araragi.data.collection.map;

import institute.isshoni.araragi.stream.Streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class TypeMap<K extends Class<?>, V> implements Map<K, V> {

    private Map<K, K> KEY_ALIASES_MAP = new HashMap<>();

    private final Map<K, V> map;

    public TypeMap() {
        this.map = new HashMap<>();
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

    public boolean containsParent(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof final Class<?> c)) {
            return null;
        }

        removeAlias((K) c);

        return this.map.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (!(key instanceof final Class<?> c)) {
            return false;
        }

        removeAlias((K) c);

        return this.map.remove(key, value);
    }

    @Override
    public V get(Object o) {
        V result = cacheGet(o);

        if (Objects.nonNull(result)) {
            return result;
        }

        K clazz = (K) o;

        Queue<Class<?>> classes = new LinkedList<>();
        classes.add(clazz);

        while (!classes.isEmpty()) {
            Class<?> current = classes.poll();

            if (Objects.isNull(current)) {
                continue;
            }

            if (Objects.nonNull(current.getSuperclass())) {
                classes.add(current.getSuperclass());
            }

            if (current.getInterfaces().length != 0) {
                classes.addAll(Arrays.asList(current.getInterfaces()));
            }

            if (this.map.containsKey(current)) {
                registerAlias(clazz, (K) current);
                return cacheGet(current);
            }
        }

        return null;
    }

    public V cacheGet(Object o) {
        return Optional.ofNullable(this.map.get(o))
                .orElseGet(() -> this.map.get(KEY_ALIASES_MAP.get(o)));
    }

    public V directGet(Object o) {
        return this.map.get(o);
    }

    public void resetCache() {
        KEY_ALIASES_MAP.clear();
    }

    private void removeAlias(K clazz) {
        KEY_ALIASES_MAP = Streams.to(KEY_ALIASES_MAP)
                .filter((k, v) -> !v.equals(clazz))
                .toMap();
    }

    private void registerAlias(K clazz, K to) {
        KEY_ALIASES_MAP.put(clazz, to);
    }
}
