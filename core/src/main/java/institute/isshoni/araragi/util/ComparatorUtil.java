package institute.isshoni.araragi.util;

import java.util.function.Predicate;

public final class ComparatorUtil {

    public static <T> int simpleCompare(T f, T s, Predicate<T> comparator) {
        boolean fResult = comparator.test(f);
        boolean sResult = comparator.test(s);

        if (fResult && sResult) {
            return 0;
        } else if (sResult) {
            return 1;
        } else if (fResult) {
            return -1;
        }

        return 2;
    }
}
