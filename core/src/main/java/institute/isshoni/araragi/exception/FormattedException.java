package institute.isshoni.araragi.exception;

import institute.isshoni.araragi.string.format.StringFormatter;

import java.util.Optional;

public class FormattedException extends Exception {

    public FormattedException(String format, StringFormatter formatter) {
        super(formatter.format(format));
    }

    public FormattedException(String format, Object... objects) {
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
