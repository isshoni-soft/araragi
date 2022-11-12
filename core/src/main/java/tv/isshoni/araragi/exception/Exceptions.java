package tv.isshoni.araragi.exception;

public class Exceptions {

    public static RuntimeException rethrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else {
            return new RuntimeException(throwable);
        }
    }
}
