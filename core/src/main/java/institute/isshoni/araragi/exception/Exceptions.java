package institute.isshoni.araragi.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class Exceptions {

    public static String toString(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter print = new PrintWriter(writer);
        throwable.printStackTrace(print);

        String result = writer.toString();

        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        print.close();

        return result.trim();
    }

    public static Throwable rootCause(Throwable throwable) {
        if (throwable.getCause() != null) {
            return rootCause(throwable.getCause());
        }

        return throwable;
    }

    public static RuntimeException rethrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else {
            return new RuntimeException(throwable);
        }
    }
}
