package tv.isshoni.araragi.data.collection.map;

import tv.isshoni.araragi.stream.Streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Write update/prune method for key aliases map
public class TypeMap<K extends Class<?>, V> implements Map<K, V> {

    private Map<K, Set<K>> KEY_ALIASES_MAP = new HashMap<>();

    private final Map<K, V> map;

    private final boolean parentFirst;

    public TypeMap() {
        this(true);
    }

    public TypeMap(boolean parentFirst) {
        this.parentFirst = parentFirst;
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
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        V result = this.map.put(key, value);

        if (KEY_ALIASES_MAP.containsKey(key)) {
            updateAlias(key, value);
        }

        return result;
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
        if (this.parentFirst) {
            return Optional.ofNullable(getParent(o))
                    .orElseGet(() -> getChild(o));
        }

        return Optional.ofNullable(getChild(o))
                .orElseGet(() -> getParent(o));
    }

    public V quickGet(Object o) {
        return this.map.get(o);
    }

    public V getChild(Object o) {
        V result = quickGet(o);

        if (Objects.nonNull(result)) {
            return result;
        }

        K clazz = (K) o;

        // TODO: Add a small check to make sure that the closest child value is returned.
        for (Map.Entry<K, V> entry : this.entrySet()) {
            K current = entry.getKey();

            if (clazz.isAssignableFrom(current)) {
                return registerAlias(clazz, quickGet(current));
            }
        }

        return null;
    }

    public V getParent(Object o) {
        V result = quickGet(o);

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
                return registerAlias(clazz, quickGet(current));
            }
        }

        return null;
    }

    private void updateAlias(K key, V value) {
        KEY_ALIASES_MAP.get(key).stream()
                .filter(this::containsKey)
                .forEach(c -> this.map.put(c, value));
    }

    private void removeAlias(K clazz) {
        KEY_ALIASES_MAP = Streams.to(KEY_ALIASES_MAP)
                .filter((k, v) -> !k.equals(clazz))
                .mapSecond(v -> v.stream()
                        .filter(cl -> cl.equals(clazz))
                        .collect(Collectors.toSet()))
                .toMap();
    }

    private V registerAlias(K clazz, V result) {
        this.map.put(clazz, result);

        KEY_ALIASES_MAP.putIfAbsent(clazz, new HashSet<>());
        KEY_ALIASES_MAP.get(clazz).add(clazz);

        return result;
    }
}
