package tv.isshoni.araragi.logging.format;

import tv.isshoni.araragi.logging.model.format.ILoggerFormatter;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.string.format.StringFormatter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SimpleLoggerFormatter implements ILoggerFormatter {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss.SS")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    private final Map<String, BiFunction<String[], IMessageContext, String>> functions = new ConcurrentHashMap<>();

    private final Map<String, Supplier<String>> suppliers = new ConcurrentHashMap<>();

    public SimpleLoggerFormatter() {
        registerGlobalSupplier("now", () -> Instant.now().toString());

        registerFunction("dashes", (args, c) -> {
            int num = Integer.parseInt(args[0]);

            return "-".repeat(num);
        });
    }

    @Override
    public void format(IMessageContext context) {
        StringFormatter formatter = new StringFormatter();
        formatter.registerSuppliers(this.suppliers);
        formatter.registerSuppliers(context.getData());
        this.functions.forEach((key, fun) -> formatter.registerFunction(key, (args, msg) -> fun.apply(args, context)));

        context.setMessage(formatter.format(context.getMessage()));
        context.setPrefix((lg, lv, t) -> "[" + DATE_FORMATTER.format(t) + "]: " + lg.getName() + " " + lv.getName() + " -] ");
    }

    @Override
    public void registerFunction(String key, BiFunction<String[], IMessageContext, String> consumer) {
        this.functions.put(key, consumer);
    }

    @Override
    public void registerGlobalSupplier(String key, Supplier<String> suppler) {
        this.suppliers.put(key, suppler);
    }

    @Override
    public Map<String, BiFunction<String[], IMessageContext, String>> getFunctions() {
        return Collections.unmodifiableMap(this.functions);
    }

    @Override
    public Map<String, Supplier<String>> getGlobalSuppliers() {
        return Collections.unmodifiableMap(this.suppliers);
    }
}
