package tv.isshoni.araragi.logging;

import tv.isshoni.araragi.logging.driver.ConsoleDriver;
import tv.isshoni.araragi.logging.format.StringFormatterLoggerFormatter;
import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.ILoggerDriver;
import tv.isshoni.araragi.logging.model.format.ILoggerFormatter;
import tv.isshoni.araragi.logging.model.level.ILevel;
import tv.isshoni.araragi.logging.model.level.Level;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.logging.model.format.message.factory.IMessageContextFactory;
import tv.isshoni.araragi.logging.format.message.factory.SimpleMessageContextFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AraragiLogger implements IAraragiLogger {

    public static AraragiLogger create(String name) {
        AraragiLogger result = new AraragiLogger(name);
        result.registerDriver(new ConsoleDriver());

        return result;
    }

    public static AraragiLogger create(String name, ILevel level) {
        AraragiLogger result = new AraragiLogger(name);
        result.registerDriver(new ConsoleDriver());
        result.setLevel(level);

        return result;
    }

    private final String name;

    private final List<ILoggerDriver> drivers;

    private IMessageContextFactory<IMessageContext> contextFactory;

    private ILoggerFormatter formatter;

    private ILevel level;

    public AraragiLogger(String name) {
        this.name = name;
        this.formatter = new StringFormatterLoggerFormatter();
        this.drivers = new LinkedList<>();
        this.level = Level.INFO;
        this.contextFactory = new SimpleMessageContextFactory();
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
    public void setFormatter(ILoggerFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setMessageContextFactory(IMessageContextFactory<IMessageContext> context) {
        this.contextFactory = context;
    }

    @Override
    public IMessageContextFactory<IMessageContext> getMessageContextFactory() {
        return this.contextFactory;
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
    public ILoggerFormatter getFormatter() {
        return this.formatter;
    }

    @Override
    public List<ILoggerDriver> getDrivers() {
        return Collections.unmodifiableList(this.drivers);
    }
}
