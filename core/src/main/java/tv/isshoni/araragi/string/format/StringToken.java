package tv.isshoni.araragi.string.format;

public class StringToken {

    private static final int TOKEN_DISCRIMINATOR_LENGTH = 3;

    private final int start;
    private final int finish;

    private final String key;
    private final String replacement;

    public StringToken(int start, int finish, String key, String replacement) {
        this.start = start;
        this.finish = finish;
        this.key = key;
        this.replacement = replacement;
    }

    public int getOffset() {
        return this.replacement.length() - (this.finish - this.start) - 1;
    }

    public String getReplacement() {
        return this.replacement;
    }

    public int getStart() {
        return this.start;
    }

    public int getFinish() {
        return this.finish;
    }

    public String getKey() {
        return this.key;
    }
}
