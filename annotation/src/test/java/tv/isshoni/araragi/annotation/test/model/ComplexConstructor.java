package tv.isshoni.araragi.annotation.test.model;

public class ComplexConstructor {

    private final String injected;
    private final String one;

    private final int two;

    public ComplexConstructor(@TestAnnotation("injected") String injected, String one, int two) {
        this.injected = injected;
        this.one = one;
        this.two = two;
    }

    public String getInjected() {
        return this.injected;
    }

    public String getOne() {
        return this.one;
    }

    public int getTwo() {
        return this.two;
    }
}
