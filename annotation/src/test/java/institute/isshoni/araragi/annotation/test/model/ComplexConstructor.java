package institute.isshoni.araragi.annotation.test.model;

import institute.isshoni.araragi.annotation.test.model.annotation.TestAnnotation;

public class ComplexConstructor {

    public final String injected;
    public final String one;

    public final int two;

    public ComplexConstructor(@TestAnnotation("injected") String injected, String one, int two) {
        this.injected = injected;
        this.one = one;
        this.two = two;
    }
}
