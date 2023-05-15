package tv.isshoni.araragi.logging.format;

import tv.isshoni.araragi.logging.model.format.ILoggerFormatter;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.reflect.JStack;
import tv.isshoni.araragi.stream.Streams;
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

    private static final int NUMBER_OF_CALLS_FOR_PARENT_METHOD = 23;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss.SS")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    private final Map<String, BiFunction<String[], IMessageContext, String>> functions;

    private final Map<String, Supplier<String>> suppliers;

    private final Map<String, String> aliases;
    private final Map<String, Runnable> aliasTriggers;

    private boolean suppressTriggers;

    public SimpleLoggerFormatter() {
        this.suppressTriggers = false;
        this.functions = new ConcurrentHashMap<>();
        this.suppliers = new ConcurrentHashMap<>();
        this.aliases = new ConcurrentHashMap<>();
        this.aliasTriggers = new ConcurrentHashMap<>();

        registerGlobalSupplier("a:now", () -> Instant.now().toString());
        registerGlobalSupplier("a:thread", () -> Thread.currentThread().getName());
        registerGlobalSupplier("a:method", () -> {
            try {
                return JStack.getMethodInStack(NUMBER_OF_CALLS_FOR_PARENT_METHOD).getName();
            } catch (NoSuchMethodException e) {
                return "Cannot find method!";
            }
        });

        registerFunction("a:dashes", (args, c) -> {
            int num = Integer.parseInt(args[0]);

            return "-".repeat(num);
        });

        registerAlias("dashes", "a:dashes", () -> System.out.println("Please use a:dashes instead!"));
    }

    // TODO: Maybe look into making logger prefix go through the string formatter too.
    @Override
    public void format(IMessageContext context) {
        StringFormatter formatter = new StringFormatter();
        formatter.registerSuppliers(this.suppliers);
        formatter.registerSuppliers(context.getData());
        formatter.setSuppressTriggers(this.suppressTriggers);
        this.functions.forEach((key, fun) -> formatter.registerFunction(key, (args, msg) -> fun.apply(args, context)));
        this.aliasTriggers.forEach((key, trig) -> formatter.registerAlias(key, this.aliases.get(key), trig));
        Streams.to(this.aliases)
                .filterInverted((alias, target) -> this.aliasTriggers.containsKey(alias))
                .forEach(formatter::registerAlias);

        context.setMessage(formatter.format(context.getMessage()));
        context.setPrefix((lg, lv, t) -> "[" + DATE_FORMATTER.format(t) + "]: " + lg.getName() + " " + lv.getName() + " -] ");
    }

    @Override
    public void setSuppressTriggers(boolean suppressTriggers) {
        this.suppressTriggers = suppressTriggers;
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
    public void registerAlias(String key, String target) {
        this.aliases.put(key, target);
    }

    @Override
    public void registerAlias(String key, String target, Runnable trigger) {
        this.aliases.put(key, target);
        this.aliasTriggers.put(key, trigger);
    }

    @Override
    public Map<String, BiFunction<String[], IMessageContext, String>> getFunctions() {
        return Collections.unmodifiableMap(this.functions);
    }

    @Override
    public Map<String, Supplier<String>> getGlobalSuppliers() {
        return Collections.unmodifiableMap(this.suppliers);
    }

    @Override
    public boolean isSuppressingTriggers() {
        return this.suppressTriggers;
    }
}
