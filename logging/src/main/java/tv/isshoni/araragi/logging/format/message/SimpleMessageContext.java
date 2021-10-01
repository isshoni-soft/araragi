package tv.isshoni.araragi.logging.format.message;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Supplier;

public class SimpleMessageContext implements IMessageContext {

    private String message;

    private final IAraragiLogger logger;

    private final ILevel level;

    private final ZonedDateTime time;

    private final Map<String, Supplier<Object>> data;

    public SimpleMessageContext(String message, IAraragiLogger logger, ILevel level, ZonedDateTime time, Map<String, Supplier<Object>> data) {
        this.message = message;
        this.logger = logger;
        this.level = level;
        this.time = time;
        this.data = data;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public IAraragiLogger getLogger() {
        return this.logger;
    }

    @Override
    public ILevel getLevel() {
        return this.level;
    }

    @Override
    public ZonedDateTime getTime() {
        return this.time;
    }

    @Override
    public Map<String, Supplier<Object>> getData() {
        return this.data;
    }
}
