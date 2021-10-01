package tv.isshoni.araragi.logging.model.format.message.factory;

import tv.isshoni.araragi.logging.model.IAraragiLogger;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.logging.model.level.ILevel;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Supplier;

public interface IMessageContextFactory<C extends IMessageContext> {

    C create(String message, IAraragiLogger logger, ILevel level, ZonedDateTime time, Map<String, Supplier<String>> data);
}
