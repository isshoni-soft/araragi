package tv.isshoni.test.araragi.model;

public class ClassB implements ParentInterface {

    private int someInt;

    public ClassB() { }

    public ClassB(int someInt) {
        this.someInt = someInt;
    }

    @Override
    public String getStr() {
        return "ClassB";
    }
}