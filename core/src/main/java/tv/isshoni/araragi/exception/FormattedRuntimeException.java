package tv.isshoni.araragi.exception;

import tv.isshoni.araragi.string.format.StringFormatter;

import java.util.Optional;

public class FormattedRuntimeException extends RuntimeException {

    public FormattedRuntimeException(String format, StringFormatter formatter) {
        super(formatter.format(format));
    }

    public FormattedRuntimeException(String format, Object... objects) {
        this(format, Optional.of(new StringFormatter())
                .map(f -> {
                    int x = 0;
                    for (Object obj : objects) {
                        f.registerSupplier(String.valueOf(x++), obj::toString);
                    }

                    return f;
                }).get());
    }
}
