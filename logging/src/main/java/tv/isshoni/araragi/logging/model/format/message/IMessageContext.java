package tv.isshoni.araragi.logging.model.format.message;

import tv.isshoni.araragi.functional.QuadFunction;
import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IMessageContext {

    void setMessage(String message);

    default void setMessage(Function<IMessageContext, String> function) {
        setMessage(function.apply(this));
    }

    default void setMessage(QuadFunction<String, IAraragiLogger, ILevel, ZonedDateTime, String> function) {
        setMessage(function.apply(getMessage(), getLogger(), getLevel(), getTime()));
    }

    String getMessage();

    IAraragiLogger getLogger();

    ILevel getLevel();

    ZonedDateTime getTime();

    Map<String, Supplier<Object>> getData();
}
