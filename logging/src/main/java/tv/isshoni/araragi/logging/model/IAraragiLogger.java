package tv.isshoni.araragi.logging.model;

import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.logging.Levels;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;

public interface IAraragiLogger {

    void registerDriver(ILoggerDriver driver);

    void setLevel(ILevel level);

    default void log(String message, ILevel level, Pair<String, Supplier<Object>>... data) {
        if (level.compareTo(this.getLevel()) < 0) {
            return;
        }

        // TODO: Add message parsing & supplier filling

        for (ILoggerDriver driver : this.getDrivers()) {
            driver.process(message, this, level, ZonedDateTime.now());
        }
    }

    default void info(String message, Pair<String, Supplier<Object>>... data) {
        this.log(message, Levels.INFO, data);
    }

    ILevel getLevel();

    String getName();

    List<ILoggerDriver> getDrivers();
}
