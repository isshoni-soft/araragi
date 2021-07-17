package tv.isshoni.araragi.logging.model;

import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;

public interface IAraragiLogger {

    void registerDriver(ILoggerDriver driver);

    void setLevel(ILevel level);

    void setFormatter(IFormatter formatter);

    default void log(String message, ILevel level, Pair<String, Supplier<Object>>... data) {
        if (level.compareTo(this.getLevel()) < 0) {
            return;
        }

        // TODO: Add message parsing & supplier filling

        for (ILoggerDriver driver : this.getDrivers()) {
            driver.process(this.getFormatter().format(message, this, level, ZonedDateTime.now()));
        }
    }

    default void info(String message, Pair<String, Supplier<Object>>... data) {
        this.log(message, Level.INFO, data);
    }

    default void warn(String message, Pair<String, Supplier<Object>>... data) {
        this.log(message, Level.WARNING, data);
    }

    ILevel getLevel();

    String getName();

    IFormatter getFormatter();

    List<ILoggerDriver> getDrivers();
}
