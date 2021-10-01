package tv.isshoni.araragi.data.collection;

import tv.isshoni.araragi.stream.Streams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Careful with this class, it's key aliases map isn't always updated if using the more advanced mutation methods.
 * TODO: Stop being lazy and wrap the mutation methods, consider writing an introspective method that 'recompiles'
 * TODO: the key aliases map.
 *
 * @param <K>
 * @param <V>
 */
public class TypeMap<K extends Class<?>, V> extends HashMap<K, V> {

    private Map<K, Set<K>> KEY_ALIASES_MAP = new HashMap<>();

    private final boolean parentFirst;

    public TypeMap() {
        this(true);
    }

    public TypeMap(boolean parentFirst) {
        this.parentFirst = parentFirst;
    }

    @Override
    public boolean containsKey(Object key) {
        return super.get(key) != null;
    }

    @Override
    public V put(K key, V value) {
        V result = super.put(key, value);

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
    public V remove(Object key) {
        if (!(key instanceof final Class<?> c)) {
            return null;
        }

        removeAlias((K) c);

        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (!(key instanceof final Class<?> c)) {
            return false;
        }

        removeAlias((K) c);

        return super.remove(key, value);
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
        return super.get(o);
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

            if (containsKey(current)) {
                return registerAlias(clazz, quickGet(current));
            }
        }

        return null;
    }

    private void updateAlias(K key, V value) {
        KEY_ALIASES_MAP.get(key).stream()
                .filter(this::containsKey)
                .forEach(c -> super.put(c, value));
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
        super.put(clazz, result);

        KEY_ALIASES_MAP.putIfAbsent(clazz, new HashSet<>());
        KEY_ALIASES_MAP.get(clazz).add(clazz);

        return result;
    }
}
