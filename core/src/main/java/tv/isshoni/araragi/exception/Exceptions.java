package tv.isshoni.araragi.exception;

public class Exceptions {

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
