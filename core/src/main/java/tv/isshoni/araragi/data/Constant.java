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

    public void ifPresent(Consumer<E> consumer) {
        if (nonNull()) {
            consumer.accept(this.instance);
        }
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean nonNull() {
        return this.instance != null;
    }

    public boolean isNull() {
        return this.instance == null;
    }

    public void set(E value) {
        if (this.locked) {
            throw new IllegalStateException("Cannot update a constant value!");
        }

        this.instance = value;
        this.locked = true;
    }

    public E get() {
        return this.instance;
    }
}
