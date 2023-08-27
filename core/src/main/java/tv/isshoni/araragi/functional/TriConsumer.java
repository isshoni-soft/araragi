package tv.isshoni.araragi.functional;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A first, B second, C third);
}
