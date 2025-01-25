package institute.isshoni.araragi.string.format;

public class StringToken {

    private final int start;
    private final int finish;

    private final String key;
    private String replacement;

    public StringToken(int start, int finish, String key, String replacement) {
        this.start = start;
        this.finish = finish;
        this.key = key;
        this.replacement = replacement;
    }

    public StringToken clone() {
        return new StringToken(this.start, this.finish, this.key, this.replacement);
    }

    public void setReplacement(String replacement) {
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

    @Override
    public int hashCode() {
        return this.start + this.finish + this.key.hashCode() + this.replacement.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StringToken other)) {
            return false;
        }

        return this.start == other.start && this.finish == other.finish
                && this.key.equals(other.key) && this.replacement.equals(other.replacement);
    }
}
