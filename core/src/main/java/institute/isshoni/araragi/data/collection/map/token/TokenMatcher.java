package institute.isshoni.araragi.data.collection.map.token;

import java.util.Arrays;

public class TokenMatcher {

    private final String[] matches;

    private final String key;

    public TokenMatcher(String key, String[] matches) {
        this.key = key;
        this.matches = matches;
    }

    public String getKey() {
        return this.key;
    }

    public String[] getMatches() {
        return Arrays.copyOf(this.matches, this.matches.length);
    }
}
