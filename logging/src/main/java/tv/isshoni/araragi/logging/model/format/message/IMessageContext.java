package tv.isshoni.araragi.logging.model.format.message;

import tv.isshoni.araragi.functional.TriFunction;
import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Supplier;

public interface IMessageContext {

    default String build() {
        return getPrefix() + getMessage();
    }

    default void setPrefix(TriFunction<IAraragiLogger, ILevel, ZonedDateTime, String> function) {
        setPrefix(function.apply(getLogger(), getLevel(), getTime()));
    }

    void setPrefix(String prefix);

    void setMessage(String message);

    String getPrefix();

    String getMessage();

    IAraragiLogger getLogger();

    ILevel getLevel();

    ZonedDateTime getTime();

    Map<String, Supplier<String>> getData();
}
