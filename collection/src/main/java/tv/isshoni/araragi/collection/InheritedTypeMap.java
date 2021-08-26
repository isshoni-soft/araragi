package tv.isshoni.araragi.collection;

import tv.isshoni.araragi.stream.Streams;

import java.util.concurrent.ConcurrentHashMap;

public class InheritedTypeMap<K extends Class, V> extends ConcurrentHashMap<K, V> {

    @Override
    public V get(Object o) {
        if (!(o instanceof final Class<?> clazz)) {
            throw new IllegalArgumentException("Type of " + o.getClass() + " is not Class!");
        }

        return super.get(Streams.to(this.keySet())
                .filter(c -> c.isAssignableFrom(clazz))
                .findFirst()
                .orElse(null));
    }
}
