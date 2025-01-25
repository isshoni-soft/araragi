package institute.isshoni.araragi.reflect;

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

    private static final HashMap<Class<?>, Class<?>> reverseMap = new HashMap<>() {{
        primitives.forEach((p, s) -> this.put(s, p));
    }};

    public static boolean checkType(Object obj, Class<?> clazz) {
        if (!isPrimitive(clazz)) {
            throw new IllegalArgumentException("class must match primitive type!");
        }

        return clazz.isAssignableFrom(obj.getClass())
                || reverseMap.get(clazz).isAssignableFrom(obj.getClass());
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return primitives.containsKey(clazz);
    }

    public static boolean isComplexPrimitive(Class<?> clazz) {
        return reverseMap.containsKey(clazz);
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

    // use toComplexType
    @Deprecated(forRemoval = true)
    public static Class<?> convert(Class<?> clazz) {
        return toComplexType(clazz);
    }

    public static Class<?> toComplexType(Class<?> clazz) {
        if (isPrimitive(clazz)) {
            return primitives.get(clazz);
        }

        return clazz; // throw exception if class isn't primitive?
    }

    public static Class<?> toPrimitiveType(Class<?> clazz) {
        if (isComplexPrimitive(clazz)) {
            return reverseMap.get(clazz);
        }

        return clazz;
    }

    public static Map<Class<?>, Class<?>> get() {
        return Collections.unmodifiableMap(primitives);
    }
}
