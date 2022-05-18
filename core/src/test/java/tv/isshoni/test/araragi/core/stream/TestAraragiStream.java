package tv.isshoni.test.araragi.core.stream;

import static org.junit.Assert.assertEquals;

import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.test.araragi.model.ClassA;
import tv.isshoni.test.araragi.model.ClassB;
import tv.isshoni.test.araragi.model.ClassC;
import tv.isshoni.test.araragi.model.ParentInterface;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void testStreamInvertedFilter() {
        this.list.add(null);
        this.list.add(null);
        this.list.add(null);

        assertEquals(5, this.list.size());
        assertEquals(2, Streams.to(this.list).filterInverted(Objects::isNull).count());
        assertEquals(3, Streams.to(this.list).filterInverted(Objects::nonNull).count());
    }
}
