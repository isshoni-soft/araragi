package institute.isshoni.araragi.annotation.test.model;

import institute.isshoni.araragi.annotation.test.model.annotation.Second;
import institute.isshoni.araragi.annotation.test.model.annotation.TestAnnotation;

public class MixedConstructed {

    public final String value;
    public final String other;
    public final String other2;

    public MixedConstructed(@TestAnnotation("value!") String value, String other, String other2) {
        this.value = value;
        this.other = other;
        this.other2 = other2;
    }

    public MixedConstructed(@TestAnnotation("value!") String value, @Second String other, int other2) {
        this(value, other, Integer.toString(other2));
    }

    @Override
    public String toString() {
        return "MIXED: " + this.value + " " + this.other + " " + this.other2;
    }
}
