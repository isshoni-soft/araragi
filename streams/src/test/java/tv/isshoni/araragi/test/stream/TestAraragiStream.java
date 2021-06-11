package tv.isshoni.araragi.test.stream;

import org.junit.Before;
import org.junit.Test;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.araragi.test.stream.model.ClassA;
import tv.isshoni.araragi.test.stream.model.ClassB;
import tv.isshoni.araragi.test.stream.model.ClassC;
import tv.isshoni.araragi.test.stream.model.ParentInterface;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestAraragiStream {

    private List<ClassC> list;

    @Before
    public void before() {
        this.list = new LinkedList<>();

        ClassC c = new ClassC();

        c.getClassA().add(new ClassA());
        c.getClassB().add(new ClassB());

        ClassC b = new ClassC(c);
        b.getClassB().add(new ClassB());

        this.list.add(c);
        this.list.add(b);
    }

    @Test
    public void testStreamAdd() {
        assertEquals(4, Streams.to(this.list)
                .add(this.list)
                .count());
    }

    @Test
    public void testStreamExpand() {
        assertEquals(7, Streams.to(this.list)
                .expand(ParentInterface.class, ClassC::getClassA, ClassC::getClassB)
                .map(ParentInterface::getStr)
                .count());
    }
}
