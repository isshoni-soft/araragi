package institute.isshoni.araragi.logging.format;

import institute.isshoni.araragi.logging.model.format.ILoggerFormatter;
import institute.isshoni.araragi.logging.model.format.message.IMessageContext;
import institute.isshoni.araragi.reflect.JStack;
import institute.isshoni.araragi.stream.Streams;
import institute.isshoni.araragi.string.format.StringFormatter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

// Bulky name; but it's a logger formatter that makes heavy use of the StringFormatter
// nothing simple about it anymore. Especially now that I've been adding goodies to it.
public class StringFormatterLoggerFormatter implements ILoggerFormatter {

    // Frankly I'm afraid to look into how deep the callstacks for Winry can get.
    private static final int NUMBER_OF_CALLS_FOR_PARENT_METHOD = 23;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss.SS")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    private final Map<String, BiFunction<String[], IMessageContext, String>> functions;

    private final Map<String, Supplier<String>> suppliers;

    private final Map<String, String> aliases;
    private final Map<String, Runnable> aliasTriggers;

    private boolean suppressTriggers;

    private String prefixFormat;

    public StringFormatterLoggerFormatter() {
        this.suppressTriggers = false;
        this.functions = new ConcurrentHashMap<>();
        this.suppliers = new ConcurrentHashMap<>();
        this.aliases = new ConcurrentHashMap<>();
        this.aliasTriggers = new ConcurrentHashMap<>();
        this.prefixFormat = "[${a:zdnow}]: ${al:name} ${al:level} -] ";

        registerGlobalSupplier("a:inow", () -> DATE_FORMATTER.format(Instant.now()));
        registerGlobalSupplier("a:zdnow", () -> DATE_FORMATTER.format(ZonedDateTime.now()));
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

        registerAlias("a:now", "a:inow");
        registerAlias("a:zonednow", "a:zdnow");
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

        context.setPrefix((lg, lv, t) -> {
            // add prefix-specific keys
            formatter.registerSupplier("al:name", lg::getName);
            formatter.registerSupplier("al:level", lv::getName);

            return formatter.format(this.prefixFormat);
        });
    }

    @Override
    public void setSuppressTriggers(boolean suppressTriggers) {
        this.suppressTriggers = suppressTriggers;
    }

    public void setPrefixFormat(String prefixFormat) {
        this.prefixFormat = prefixFormat;
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

    public String getPrefixFormat() {
        return this.prefixFormat;
    }
}
