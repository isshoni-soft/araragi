package tv.isshoni.araragi.string.format;

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

    // TODO - Implement these.
//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return super.equals(obj);
//    }
}
