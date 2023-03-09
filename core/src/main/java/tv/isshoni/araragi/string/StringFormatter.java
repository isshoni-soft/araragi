package tv.isshoni.araragi.string;

import tv.isshoni.araragi.stream.Streams;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class StringFormatter {

    private final Map<String, BiFunction<String[], String, String>> functions = new ConcurrentHashMap<>();

    private final Map<String, Supplier<String>> suppliers = new ConcurrentHashMap<>();

    public void registerFunction(String key, BiFunction<String[], String, String> function) {
        this.functions.put(key, function);
    }

    public void registerSuppliers(Map<String, Supplier<String>> suppliers) {
        this.suppliers.putAll(suppliers);
    }

    public void registerSupplier(String key, Supplier<String> supplier) {
        this.suppliers.put(key, supplier);
    }

    public String format(String message) {
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

                final String temp = message;

                Optional<String> result = Optional.ofNullable(processSupplier(key))
                        .or(() -> Optional.ofNullable(processFunction(key, temp)));

                message = message.substring(0, first) + result.orElse(key) + message.substring(second + 1);
                first = -1;
                second = -1;
                x = 0;
            }
        }

        return message;
    }

    public String processSupplier(String key) {
        return Streams.to(this.suppliers)
                .filter((k, s) -> k.equals(key))
                .mapSecond()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public String processFunction(String key, String message) {
        String[] splitCommand = key.split("%");
        String[] args;

        if (splitCommand.length >= 1) {
            args = Arrays.copyOfRange(splitCommand, 1, splitCommand.length);
        } else {
            args = new String[0];
        }

        key = splitCommand[0];

        return Optional.ofNullable(this.functions.get(key))
                .map(f -> f.apply(args, message))
                .orElse(null);
    }
}
