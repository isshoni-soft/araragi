package tv.isshoni.araragi.collection;

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
        return Optional.ofNullable(getChild(o))
                .or(() -> Optional.ofNullable(getParent(o)))
                .orElse(null);
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

        Queue<K> classQueue = new LinkedList<>();
        classQueue.add(clazz);

        while (!classQueue.isEmpty()) {
            Optional<K> currentOptional = Optional.ofNullable(classQueue.poll());

            if (!clazz.getSuperclass().equals(Object.class)) {
                classQueue.add((K) clazz.getSuperclass());
            }

            if (clazz.getInterfaces().length > 0) {
                classQueue.addAll(Arrays.asList((K[]) clazz.getInterfaces()));
            }

            if (currentOptional.isPresent()) {
                K current = currentOptional.get();

                if (containsKey(current)) {
                    return registerAlias(clazz, quickGet(current));
                }
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

        for (Map.Entry<K, V> entry : this.entrySet()) {
            if (clazz.isAssignableFrom(entry.getKey())) {
                return registerAlias(clazz, quickGet(entry.getKey()));
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
                .map(k -> k, v -> v.stream()
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
