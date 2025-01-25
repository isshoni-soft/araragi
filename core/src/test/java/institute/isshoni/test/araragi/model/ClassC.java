package institute.isshoni.test.araragi.model;

import java.util.LinkedList;
import java.util.List;

public class ClassC implements ParentInterface {

    private final List<ClassA> clazzA;

    private final List<ClassB> clazzB;

    public ClassC() {
        this.clazzA = new LinkedList<>();
        this.clazzB = new LinkedList<>();
    }

    public ClassC(ClassC c) {
        this.clazzA = new LinkedList<>(c.clazzA);
        this.clazzB = new LinkedList<>(c.clazzB);
    }

    public List<ClassA> getClassA() {
        return this.clazzA;
    }

    public List<ClassB> getClassB() {
        return this.clazzB;
    }

    @Override
    public String getStr() {
        return "ClassC";
    }
}
