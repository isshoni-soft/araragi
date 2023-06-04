package tv.isshoni.araragi.data;

import java.util.function.Consumer;

public class Constant<E> {

    private E instance;

    private boolean locked;

    public Constant() {
        this(null);
        this.locked = false;
    }

    public Constant(E value) {
        this.instance = value;
        this.locked = true;
    }

    public synchronized void ifPresent(Consumer<E> consumer) {
        if (isPresent()) {
            consumer.accept(this.instance);
        }
    }

    public synchronized boolean isLocked() {
        return this.locked;
    }

    public synchronized boolean isPresent() {
        return this.instance != null;
    }

    public synchronized void set(E value) {
        if (this.locked) {
            throw new IllegalStateException("Cannot update a constant value!");
        }

        this.instance = value;
        this.locked = true;
    }

    public synchronized E get() {
        return this.instance;
    }
}
