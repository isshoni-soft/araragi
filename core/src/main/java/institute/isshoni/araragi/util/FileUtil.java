package institute.isshoni.araragi.util;

import java.io.InputStream;

public final class FileUtil {

    public static InputStream getResource(String path) {
        return Thread.currentThread().getContextClassLoader()
                .getResourceAsStream((path.startsWith("/") ? path.substring(1) : path));
    }
}
