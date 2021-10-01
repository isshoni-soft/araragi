package tv.isshoni.araragi.logging.model.format;

import tv.isshoni.araragi.logging.model.format.message.IMessageContext;

import java.util.Map;
import java.util.function.BiFunction;

public interface IFormatter {

    void format(IMessageContext context);

    String processReplacement(String key, IMessageContext context);

    String processFunction(String key, IMessageContext context);

    void formatMessage(IMessageContext context);

    void registerFunction(String key, BiFunction<String[], IMessageContext, String> consumer);

    Map<String, BiFunction<String[], IMessageContext, String>> getFunctions();
}
