package tv.isshoni.araragi.logging.model.format;

import tv.isshoni.araragi.logging.model.format.message.IMessageContext;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ILoggerFormatter {

    void format(IMessageContext context);

    void setSuppressTriggers(boolean suppressTriggers);

    void registerFunction(String key, BiFunction<String[], IMessageContext, String> consumer);

    void registerGlobalSupplier(String key, Supplier<String> suppler);

    void registerAlias(String key, String target);

    void registerAlias(String key, String target, Runnable trigger);

    Map<String, BiFunction<String[], IMessageContext, String>> getFunctions();

    Map<String, Supplier<String>> getGlobalSuppliers();

    boolean isSuppressingTriggers();
}
