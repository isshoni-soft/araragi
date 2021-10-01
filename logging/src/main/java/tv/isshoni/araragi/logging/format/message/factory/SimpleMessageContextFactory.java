package tv.isshoni.araragi.logging.format.message.factory;

import tv.isshoni.araragi.logging.format.message.SimpleMessageContext;
import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.logging.model.format.message.factory.IMessageContextFactory;
import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Supplier;

public class SimpleMessageContextFactory implements IMessageContextFactory<IMessageContext> {

    @Override
    public IMessageContext create(String message, IAraragiLogger logger, ILevel level, ZonedDateTime time, Map<String, Supplier<Object>> data) {
        return new SimpleMessageContext(message, logger, level, time, data);
    }
}
