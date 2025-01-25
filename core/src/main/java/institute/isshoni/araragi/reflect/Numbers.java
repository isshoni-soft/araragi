package institute.isshoni.araragi.reflect;

public final class Numbers {

    // I like to call this type laundering
    public static <T extends Number> T convertTo(Number number, Class<T> clazz) {
        Class<?> complex = Primitives.toComplexType(clazz);

        if (complex.equals(Integer.class)) {
            return (T) Integer.valueOf(number.intValue());
        } else if (complex.equals(Long.class)) {
            return (T) Long.valueOf(number.longValue());
        } else if (complex.equals(Double.class)) {
            return (T) Double.valueOf(number.doubleValue());
        } else if (complex.equals(Float.class)) {
            return (T) Float.valueOf(number.floatValue());
        } else if (complex.equals(Short.class)) {
            return (T) Short.valueOf(number.shortValue());
        } else if (complex.equals(Byte.class)) {
            return (T) Byte.valueOf(number.byteValue());
        }

        throw new IllegalArgumentException("Cannot convert " + number + " to " + clazz);
    }
}
