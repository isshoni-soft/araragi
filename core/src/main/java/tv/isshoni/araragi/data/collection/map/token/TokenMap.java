package tv.isshoni.araragi.data.collection.map.token;

import tv.isshoni.araragi.data.collection.map.BucketMap;
import tv.isshoni.araragi.data.collection.map.Maps;
import tv.isshoni.araragi.string.format.StringFormatter;
import tv.isshoni.araragi.string.format.StringToken;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: Maybe rename me & maybe make me NOT a map subclass and instead part of the StringFormatter.
public class TokenMap<V> implements Map<String, V> {

    private final Map<String, V> data;
    private final BucketMap<String, TokenMatcher> prefixShortcuts;

    private final StringFormatter formatter;

    public TokenMap(StringFormatter formatter) {
        this.data = new HashMap<>();
        this.prefixShortcuts = Maps.bucket(new HashMap<>());
        this.formatter = formatter;
    }

    public TokenMap() {
        this(new StringFormatter());
    }

    public StringFormatter getFormatter() {
        return this.formatter;
    }

    public V getLiteral(Object key) {
        return this.data.get(key);
    }

    public boolean containsLiteralKey(Object key) {
        return this.data.containsKey(key);
    }

    public List<StringToken> tokenize(String message) {
        return this.formatter.tokenize(message);
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.data.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.data.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return this.data.entrySet();
    }

    @Override
    public boolean containsValue(Object value) {
        return this.data.containsValue(value);
    }

    @Override
    public V remove(Object key) {
        return this.data.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public boolean containsKey(Object obj) {
        if (!(obj instanceof String key)) {
            return false;
        }

        if (containsLiteralKey(obj)) {
            return containsLiteralKey(key);
        } else {
            throw new UnsupportedOperationException(); // TODO
//            return true;
        }
    }

    @Override
    public V get(Object obj) {
        if (!(obj instanceof String key)) {
            return null;
        }

        // if it's a simple map path, aka 1:1 matches a stored key
        if (containsLiteralKey(key)) {
            return getLiteral(key);
        } else { // not simple, time to start parsing and matching
            V result = null;

            for (Entry<String, List<TokenMatcher>> entry : this.prefixShortcuts.entrySet()) {
                String k = entry.getKey();
                List<TokenMatcher> v = entry.getValue();

                if (key.startsWith(k)) {
                    for (TokenMatcher matcher : v) {
                        String[] mustMatch = matcher.getMatches();
                        List<StringToken> tokens = tokenize(matcher.getKey());

                        if (tokens.size() != mustMatch.length) {
                            continue;
                        }

                        boolean matches = false;

                        for (int x = 0; x < mustMatch.length; x++) {
                            StringToken token = tokens.get(x);
                            String match = mustMatch[x];
                            int matchedIndex = key.indexOf(match, token.getStart());

                            if (matchedIndex == -1) {
                                matches = false;
                                break;
                            }

//                            String between = key.substring(token.getStart(), matchedIndex);
                            matches = true;
                        }

                        if (matches) {
                            result = getLiteral(matcher.getKey());
                        }
                    }
                }
            }

            return result;
        }
    }

    @Override
    public V put(String key, V value) {
        List<StringToken> tokens = tokenize(key);

        if (!tokens.isEmpty()) {
            StringToken firstToken = tokens.get(0);
            String prefix = "";
            int firstFinish = firstToken.getFinish();

            if (firstToken.getStart() != 0) {
                prefix = key.substring(0, firstToken.getStart());
            }

            List<TokenMatcher> permutations = this.prefixShortcuts.getOrNew(prefix);

            if (permutations.isEmpty()) {
                int finish = firstFinish + 1;
                List<String> betweens = new LinkedList<>();

                for (int x = 1; x < tokens.size(); x++) {
                    StringToken token = tokens.get(x);
                    betweens.add(key.substring(finish, token.getStart()));

                    finish = (token.getFinish() + 1);
                }

                if (finish != key.length()) {
                    betweens.add(key.substring(finish));
                }

                this.prefixShortcuts.add(prefix, new TokenMatcher(key, betweens.toArray(new String[0])));
            } else {
                for (TokenMatcher permutation : permutations) {
                    int finish = firstFinish + 1;
                    boolean newPerm = false;
                    List<String> betweens = new LinkedList<>();

                    for (int x = 1; x < tokens.size(); x++) {
                        StringToken token = tokens.get(x);
                        String between = key.substring(finish, token.getStart());
                        betweens.add(between);

                        if (!between.equals(permutation.getMatches()[x])) {
                            newPerm = true;
                        }

                        finish = (token.getFinish() + 1);
                    }

                    if (finish != key.length()) {
                        betweens.add(key.substring(finish));
                    }

                    if (newPerm) {
                        this.prefixShortcuts.add(prefix, new TokenMatcher(key, betweens.toArray(new String[0])));
                    }
                }
            }
        }

        return this.data.put(key, value);
    }
}

// Assumptions: CANNOT assume that spaces are always present, might get a key like:
// -> /some/{path}/that/{needs}/{keyed}

// "some {generic} key" -> "some " [token:generic] + " key"
// "some awesome key" -> starts with "some " ends with " key" -> matches..?

// some {inbetween} sentence {type} key -> "some " [token:inbetween] " sentence " [token:type] " key"

// storage:
// - "some " : [[" sentence ", " key"], [" key"]]
