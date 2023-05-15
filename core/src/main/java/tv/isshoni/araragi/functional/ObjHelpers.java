package tv.isshoni.araragi.functional;

import java.util.Objects;

public class ObjHelpers {

    public static boolean isOneNull(Object... objects) {
        for (Object obj : objects) {
            if (Objects.isNull(obj)) {
                return true;
            }
        }

        return false;
    }

    public static int hashCode(Object... objects) {
        int sum = 0;

        for (Object obj : objects) {
            if (Objects.nonNull(obj)) {
                sum += obj.hashCode();
            }
        }

        return sum;
    }
}
