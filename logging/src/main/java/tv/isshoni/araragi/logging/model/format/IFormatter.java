package tv.isshoni.araragi.logging.model.format;

import tv.isshoni.araragi.logging.model.format.message.IMessageContext;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IFormatter {

    void format(IMessageContext context);

    String processSupplier(String key, IMessageContext context);

    String processFunction(String key, IMessageContext context);

    void formatMessage(IMessageContext context);

    void registerFunction(String key, BiFunction<String[], IMessageContext, String> consumer);

    void registerGlobalSupplier(String key, Supplier<String> suppler);

    Map<String, BiFunction<String[], IMessageContext, String>> getFunctions();

    Map<String, Supplier<String>> getGlobalSuppliers();
}
