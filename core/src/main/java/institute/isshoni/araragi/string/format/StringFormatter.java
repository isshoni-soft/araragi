package institute.isshoni.araragi.string.format;

import institute.isshoni.araragi.stream.Streams;

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

    private final Map<String, BiFunction<String[], String, String>> functions;

    private final Map<String, Supplier<String>> suppliers;

    private final Map<String, String> aliases;
    private final Map<String, Runnable> aliasTrigger;

    private final String discriminatorPrefix;
    private final String discriminatorSuffix;

    private boolean suppressTriggers;

    public StringFormatter(String discriminatorPrefix, String discriminatorSuffix) {
        this.discriminatorPrefix = discriminatorPrefix;
        this.discriminatorSuffix = discriminatorSuffix;
        this.functions = new ConcurrentHashMap<>();
        this.suppliers = new ConcurrentHashMap<>();
        this.aliases = new ConcurrentHashMap<>();
        this.aliasTrigger = new ConcurrentHashMap<>();
        this.suppressTriggers = false;
    }

    public StringFormatter() {
        this("${", "}");
    }

    public void setSuppressTriggers(boolean suppressTriggers) {
        this.suppressTriggers = suppressTriggers;
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

    public void registerAlias(String alias, String target) {
        this.aliases.put(alias, target);
    }

    public void registerAlias(String alias, String target, Runnable trigger) {
        this.aliases.put(alias, target);
        this.aliasTrigger.put(alias, trigger);
    }

    public List<StringToken> tokenize(String message) { // list order is SUPER important.
        List<StringToken> result = new LinkedList<>();
        int first = -1;
        int second = -1;

        for (int x = 0; x < message.length(); x++) {
            char current;
            int temp = -1;
            for (int y = 0; y < this.discriminatorPrefix.length() && first == -1; y++, x++) {
                if (x >= message.length()) {
                    break;
                }

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

                String replacement = findReplacement(key, message);

                result.add(new StringToken(first, second, key, format(replacement)));

                first = -1;
                second = -1;
            }
        }

        return result;
    }

    public String findReplacement(String key, String message) {
        final String prevKey = key;
        String[] splitCommand = key.split("%");

        if (this.aliases.containsKey(splitCommand[0])) {
            final String prevSplitKey = splitCommand[0];
            key = this.aliases.get(prevSplitKey);

            if (splitCommand.length > 1) {
                List<String> subCmd = Arrays.asList(splitCommand);
                key += "%" + String.join("%", subCmd.subList(1, subCmd.size()));
            }

            if (!this.suppressTriggers && this.aliasTrigger.containsKey(prevSplitKey)) {
                this.aliasTrigger.get(prevSplitKey).run();
            }
        }

        final String finalKey = key;

        return Optional.ofNullable(processSupplier(finalKey))
                .or(() -> Optional.ofNullable(processFunction(finalKey, message, prevKey)))
                .orElse(key);
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

    public String processFunction(String key, String message, String prevKey) {
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

    public boolean isSuppressingTriggers() {
        return this.suppressTriggers;
    }
}
