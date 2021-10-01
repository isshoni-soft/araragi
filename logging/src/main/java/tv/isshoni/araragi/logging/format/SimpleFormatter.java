package tv.isshoni.araragi.logging.format;

import tv.isshoni.araragi.logging.model.format.IFormatter;
import tv.isshoni.araragi.logging.model.format.message.IMessageContext;
import tv.isshoni.araragi.stream.Streams;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SimpleFormatter implements IFormatter {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss.SS")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    private final Map<String, BiFunction<String[], IMessageContext, String>> FUNCTIONS = new ConcurrentHashMap<>();

    private final Map<String, Supplier<String>> GLOBAL_SUPPLIERS = new ConcurrentHashMap<>();

    public SimpleFormatter() {
        registerGlobalSupplier("now", () -> Instant.now().toString());

        registerFunction("dashes", (args, c) -> {
            int num = Integer.parseInt(args[0]);

            return "-".repeat(num);
        });
    }

    @Override
    public void format(IMessageContext context) {
        String message = context.getMessage();

        int first = -1;
        int second = -1;

        for (int x = 0; x < message.length(); x++) {
            char current = message.charAt(x);

            if (current == '$' && x < message.length() - 1) {
                if (message.charAt(x + 1) == '{') {
                    first = x;
                }
            } else if (current == '}' && first != -1) {
                second = x;
            }

            if (first != -1 && second != -1) {
                String key = message.substring(first + 2, second);

                Optional<String> result = Optional.ofNullable(processSupplier(key, context))
                        .or(() -> Optional.ofNullable(processFunction(key, context)));

                String temp = context.getMessage();
                temp = temp.substring(0, first) + result.orElse(key) + temp.substring(second + 1);

                context.setMessage(temp);

                message = context.getMessage();
                first = -1;
                second = -1;
                x = 0;
            }
        }

        formatMessage(context);
    }

    @Override
    public String processSupplier(String key, IMessageContext context) {
        return Streams.to(context.getData())
                .add(GLOBAL_SUPPLIERS)
                .filter((k, s) -> k.equals(key))
                .mapSecond()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String processFunction(String key, IMessageContext context) {
        String[] splitCommand = key.split("%");
        String[] args;

        if (splitCommand.length >= 1) {
            args = Arrays.copyOfRange(splitCommand, 1, splitCommand.length);
        } else {
            args = new String[0];
        }

        key = splitCommand[0];

        return Optional.ofNullable(FUNCTIONS.get(key))
                .map(f -> f.apply(args, context))
                .orElse(null);
    }

    @Override
    public void formatMessage(IMessageContext context) {
        context.setMessage((m, lg, lv, t) -> "[" + DATE_FORMATTER.format(t) + "]: " + lg.getName() + " " + lv.getName() + " -] " + m);
    }

    @Override
    public void registerFunction(String key, BiFunction<String[], IMessageContext, String> consumer) {
        FUNCTIONS.put(key, consumer);
    }

    @Override
    public void registerGlobalSupplier(String key, Supplier<String> suppler) {
        GLOBAL_SUPPLIERS.put(key, suppler);
    }

    @Override
    public Map<String, BiFunction<String[], IMessageContext, String>> getFunctions() {
        return Collections.unmodifiableMap(FUNCTIONS);
    }

    @Override
    public Map<String, Supplier<String>> getGlobalSuppliers() {
        return Collections.unmodifiableMap(GLOBAL_SUPPLIERS);
    }
}
