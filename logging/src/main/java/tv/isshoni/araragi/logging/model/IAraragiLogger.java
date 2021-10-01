package tv.isshoni.araragi.logging.model;

import tv.isshoni.araragi.logging.model.format.IFormatter;
import tv.isshoni.araragi.logging.model.level.ILevel;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.logging.model.format.message.factory.IMessageContextFactory;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface IAraragiLogger {

    void registerDriver(ILoggerDriver driver);

    void setLevel(ILevel level);

    void setFormatter(IFormatter formatter);

    void setMessageContextFactory(IMessageContextFactory<IMessageContext> context);

    default void log(String message, ILevel level, Map<String, Supplier<Object>> data) {
        if (level.compareTo(this.getLevel()) < 0) {
            return;
        }

        IMessageContext context = getMessageContextFactory().create(message, this, level, ZonedDateTime.now(), data);

        getFormatter().format(context);

        for (ILoggerDriver driver : this.getDrivers()) {
            driver.process(context);
        }
    }

    default void log(String message, ILevel level) {
        this.log(message, level, new HashMap<>());
    }

    default void info(String message, Map<String, Supplier<Object>> data) {
        this.log(message, Level.INFO, data);
    }

    default void info(String message) {
        this.info(message, new HashMap<>());
    }

    default void warn(String message, Map<String, Supplier<Object>> data) {
        this.log(message, Level.WARNING, data);
    }

    default void warn(String message) {
        this.warn(message, new HashMap<>());
    }

    default void error(String message, Map<String, Supplier<Object>> data) {
        this.log(message, Level.ERROR, data);
    }

    default void error(String message) {
        this.error(message, new HashMap<>());
    }

    default void debug(String message, Map<String, Supplier<Object>> data) {
        this.log(message, Level.DEBUG, data);
    }

    default void debug(String message) {
        this.debug(message, new HashMap<>());
    }

    IMessageContextFactory<IMessageContext> getMessageContextFactory();

    ILevel getLevel();

    String getName();

    IFormatter getFormatter();

    List<ILoggerDriver> getDrivers();
}
