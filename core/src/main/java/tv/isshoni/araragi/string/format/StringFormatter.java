package tv.isshoni.araragi.string.format;

import tv.isshoni.araragi.stream.Streams;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class StringFormatter {

    private final Map<String, BiFunction<String[], String, String>> functions = new ConcurrentHashMap<>();

    private final Map<String, Supplier<String>> suppliers = new ConcurrentHashMap<>();

    private final String discriminatorPrefix;
    private final String discriminatorSuffix;

    public StringFormatter(String discriminatorPrefix, String discriminatorSuffix) {
        this.discriminatorPrefix = discriminatorPrefix;
        this.discriminatorSuffix = discriminatorSuffix;
    }

    public StringFormatter() {
        this("${", "}");
    }

    public void registerFunction(String key, BiFunction<String[], String, String> function) {
        this.functions.put(key, function);
    }

    public void registerSuppliers(Map<String, Supplier<String>> suppliers) {
        this.suppliers.putAll(suppliers);
    }

    public void registerSupplier(String key, Supplier<String> supplier) {
        this.suppliers.put(key, supplier);
    }

    public List<StringToken> tokenize(String message) { // list order is SUPER important.
        List<StringToken> result = new LinkedList<>();
        int first = -1;
        int second = -1;

        for (int x = 0; x < message.length(); x++) {
            char current;
            int temp = -1;
            for (int y = 0; y < this.discriminatorPrefix.length() && first == -1; y++, x++) {
                current = message.charAt(x);

                if (current != this.discriminatorPrefix.charAt(y)) {
                    break;
                }

                if (this.discriminatorPrefix.length() == 1) {
                    first = x;
                } else if (y == 0) {
                    temp = x;
                } else if (y == this.discriminatorPrefix.length() - 1) {
                    first = temp;
                }
            }

            for (int y = 0; y < this.discriminatorSuffix.length() && first != -1; y++, x++) {
                current = message.charAt(x);

                if (current != this.discriminatorSuffix.charAt(y)) {
                    break;
                }

                if (this.discriminatorSuffix.length() == 1) {
                    second = x;
                } else if (y == this.discriminatorSuffix.length() - 1) {
                    second = x;
                }
            }

            if (first != -1 && second != -1) {
                String key = message.substring(first + this.discriminatorPrefix.length(),
                        second - (this.discriminatorSuffix.length() - 1));

                String replacement = Optional.ofNullable(processSupplier(key))
                        .or(() -> Optional.ofNullable(processFunction(key, message)))
                        .orElse(key);

                result.add(new StringToken(first, second, key, format(replacement)));

                first = -1;
                second = -1;
            }
        }

        return result;
    }

    public String format(String message) {
        List<StringToken> tokens = tokenize(message);

        int offset = 0;
        for (StringToken token : tokens) {
            String replacement = token.getReplacement();
            int first = token.getStart() + offset;
            int last = token.getFinish() + 1 + offset;

            message = message.substring(0, first) + replacement + message.substring(last);
            offset += token.getOffset();
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
