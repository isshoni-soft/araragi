package institute.isshoni.araragi.data.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public final class Lists {

    public static <T> T[] newArray(Class<T> clazz, int capacity) {
        return (T[]) Array.newInstance(clazz, capacity);
    }

    @SafeVarargs
    public static <T> LinkedList<T> linkedListOf(T... values) {
        return new LinkedList<>(Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> ArrayList<T> arrayListOf(T... values) {
        return new ArrayList<>(Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> T[] arrayOf(Class<T> clazz, T... values) {
        T[] result = newArray(clazz, values.length);

        for (int x = 0; x < values.length; x++) {
            result[x] = values[x];
        }

        return result;
    }
}
