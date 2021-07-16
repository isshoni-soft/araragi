package tv.isshoni.araragi.logging;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.ILevel;
import tv.isshoni.araragi.logging.model.ILoggerDriver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AraragiLogger implements IAraragiLogger {

    public static AraragiLogger create(String name) {
        AraragiLogger result = new AraragiLogger(name);
        // TODO: Register console driver here.

        return result;
    }

    private final String name;

    private final List<ILoggerDriver> drivers;

    private ILevel level;

    public AraragiLogger(String name) {
        this.name = name;
        this.drivers = new LinkedList<>();
        this.level = Levels.INFO;
    }

    @Override
    public void registerDriver(ILoggerDriver driver) {
        this.drivers.add(driver);
    }

    @Override
    public void setLevel(ILevel level) {
        this.level = level;
    }

    @Override
    public ILevel getLevel() {
        return this.level;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<ILoggerDriver> getDrivers() {
        return Collections.unmodifiableList(this.drivers);
    }
}
