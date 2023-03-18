package tv.isshoni.araragi.data.collection.map.token;

import tv.isshoni.araragi.data.Pair;
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
            return get(obj) != null;
        }
    }

    public Pair<V, List<StringToken>> getTokenized(Object obj) {
        Pair<V, List<StringToken>> nil = Pair.of(null, new LinkedList<>());

        if (!(obj instanceof String key)) {
            return nil;
        }

        // if it's a simple map path, aka 1:1 matches a stored key
        if (containsLiteralKey(key)) {
            return Pair.of(getLiteral(key), new LinkedList<>());
        } else { // not simple, time to start parsing and matching
            for (Entry<String, List<TokenMatcher>> entry : this.prefixShortcuts.entrySet()) {
                String k = entry.getKey();
                List<TokenMatcher> v = entry.getValue();

                if (key.startsWith(k)) {
                    for (TokenMatcher matcher : v) {
                        String[] mustMatch = matcher.getMatches();
                        List<StringToken> tokens = tokenize(matcher.getKey());
                        List<StringToken> resultTokens = new LinkedList<>();
                        tokens.forEach(t -> resultTokens.add(t.clone()));

                        boolean matches = false;

                        for (int x = 0; x < tokens.size(); x++) {
                            StringToken token = tokens.get(x);
                            String match = "";

                            int matchedIndex;
                            if (x < mustMatch.length) {
                                match = mustMatch[x];
                                matchedIndex = key.indexOf(match, token.getStart());
                            } else {
                                matchedIndex = key.length();
                            }

                            if (matchedIndex == -1) {
                                matches = false;
                                break;
                            }

                            String foundMatchString;
                            if (tokens.size() <= x + 1) {
                                foundMatchString = key.substring(matchedIndex);
                            } else {
                                foundMatchString = key.substring(matchedIndex, (tokens.get(x + 1).getStart() - 1));
                            }

                            String between = key.substring(token.getStart(), matchedIndex);
                            resultTokens.get(x).setReplacement(between);
                            matches = match.equals(foundMatchString);
                        }

                        if (matches) {
                            return Pair.of(getLiteral(matcher.getKey()), resultTokens);
                        }
                    }
                }
            }

            return nil;
        }
    }

    @Override
    public V get(Object obj) {
        return getTokenized(obj).getFirst();
    }

    @Override
    public V put(String key, V value) {
        List<StringToken> tokens = tokenize(key);

        if (!tokens.isEmpty()) {
            StringToken lastToken = tokens.get(tokens.size() - 1);
            if (lastToken.getFinish() == key.length() - 1) {
                throw new IllegalArgumentException("Cannot use string that ends with token!");
            }

            StringToken firstToken = tokens.get(0);
            String prefix = "";
            int firstFinish = firstToken.getFinish();

            if (firstToken.getStart() != 0) {
                prefix = key.substring(0, firstToken.getStart());
            }

            List<TokenMatcher> permutations = this.prefixShortcuts.getOrNew(prefix);

            boolean newPerm = true;
            int finish = firstFinish + 1;
            List<String> betweens = new LinkedList<>();

            for (int x = 1; x < tokens.size(); x++) {
                StringToken token = tokens.get(x);
                String between = key.substring(finish, token.getStart());
                betweens.add(between);

                finish = (token.getFinish() + 1);
            }

            if (finish != key.length()) {
                betweens.add(key.substring(finish));
            }

            for (TokenMatcher permutation : permutations) {
                if (permutation.getMatches().length != betweens.size()) {
                    continue;
                }

                for (int x = 0; x < betweens.size(); x++) {
                    if (permutation.getMatches()[x].equals(betweens.get(x))) {
                        newPerm = false;
                    } else {
                        newPerm = true;
                        break;
                    }
                }
            }

            if (newPerm) {
                this.prefixShortcuts.add(prefix, new TokenMatcher(key, betweens.toArray(new String[0])));
            }
        }

        return this.data.put(key, value);
    }
}
