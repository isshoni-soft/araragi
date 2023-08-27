package tv.isshoni.araragi.reflect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Primitives {

    private static final HashMap<Class<?>, Class<?>> primitives = new HashMap<>() {{
        put(int.class, Integer.class);
        put(byte.class, Byte.class);
        put(long.class, Long.class);
        put(short.class, Short.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
        put(boolean.class, Boolean.class);
        put(char.class, Character.class);
    }};

    public static boolean isPrimitive(Class<?> clazz) {
        return primitives.containsKey(clazz);
    }

    public static float[] to(Float[] floats) {
        float[] result = new float[floats.length];

        for (int x = 0; x < floats.length; x++) {
            result[x] = floats[x];
        }

        return result;
    }

    public static int[] to(Integer[] ints) {
        int[] result = new int[ints.length];

        for (int x = 0; x < ints.length; x++) {
            result[x] = ints[x];
        }

        return result;
    }

    public static double[] to(Double[] doubles) {
        double[] result = new double[doubles.length];

        for (int x = 0; x < doubles.length; x++) {
            result[x] = doubles[x];
        }

        return result;
    }

    public static Class<?> convert(Class<?> clazz) {
        if (isPrimitive(clazz)) {
            return primitives.get(clazz);
        }

        return clazz;
    }

    public static Map<Class<?>, Class<?>> get() {
        return Collections.unmodifiableMap(primitives);
    }
}
