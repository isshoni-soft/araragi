package tv.isshoni.araragi.collection;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class InheritedTypeMap<K extends Class<?>, V> extends ConcurrentHashMap<K, V> {

    @Override
    public boolean containsKey(Object key) {
        return super.get(key) != null;
    }

    @Override
    public V get(Object o) {
        if (!(o instanceof Class<?>)) {
            return null;
        }

        K clazz = (K) o;

        if (containsKey(clazz)) {
            return super.get(clazz);
        }

        Queue<Class<?>> classQueue = new LinkedList<>();
        classQueue.add(clazz);

        while (!classQueue.isEmpty()) {
            Optional<Class<?>> currentOptional = Optional.ofNullable(classQueue.poll());

            if (!clazz.getSuperclass().equals(Object.class)) {
                classQueue.add(clazz.getSuperclass());
            }

            if (clazz.getInterfaces().length > 0) {
                classQueue.addAll(Arrays.asList(clazz.getInterfaces()));
            }

            if (currentOptional.isPresent()) {
                Class<?> current = currentOptional.get();

                if (containsKey(current)) {
                    V result = super.get(current);

                    super.put(clazz, result); // cache this association for better runtime later :)
                    return result;
                }
            }
        }

        return null;
    }
}
