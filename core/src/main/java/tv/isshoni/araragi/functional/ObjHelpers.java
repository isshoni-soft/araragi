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
}
