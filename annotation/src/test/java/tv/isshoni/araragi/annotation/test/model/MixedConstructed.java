package tv.isshoni.araragi.annotation.test.model;

public class MixedConstructed {

    private final String value;
    private final String other;
    private final String other2;

    public MixedConstructed(@TestAnnotation("value!") String value, String other, String other2) {
        this.value = value;
        this.other = other;
        this.other2 = other2;
    }

    public String getValue() {
        return this.value;
    }

    public String getOther() {
        return this.other;
    }

    public String getOther2() {
        return this.other2;
    }

    @Override
    public String toString() {
        return "MIXED: " + this.value + " " + this.other + " " + this.other2;
    }
}
