package tv.isshoni.araragi.data.collection;

import java.util.Arrays;
import java.util.LinkedList;

public final class Lists {

    @SafeVarargs
    public static <T> LinkedList<T> linkedListOf(T... values) {
        return new LinkedList<>(Arrays.asList(values));
    }
}
