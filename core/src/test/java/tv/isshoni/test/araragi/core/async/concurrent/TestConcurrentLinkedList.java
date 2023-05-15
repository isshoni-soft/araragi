package tv.isshoni.test.araragi.core.async.concurrent;

import org.junit.Before;
import org.junit.Test;
import tv.isshoni.araragi.concurrent.collection.ConcurrentLinkedList;

import java.util.Iterator;
import java.util.ListIterator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestConcurrentLinkedList {

    private static final String[] A6 = new String[] {"a", "b", "c", "d", "e", "f", "g"};

    private ConcurrentLinkedList<String> concurrentList;

    @Before
    public void setup() {
        this.concurrentList = new ConcurrentLinkedList<>();
    }

    @Test
    public void testBasicCapacityFunctions() {
        assertTrue(this.concurrentList.isEmpty());
        assertTrue(this.concurrentList.add("value!"));
        assertEquals(1, this.concurrentList.size());
        assertEquals("value!", this.concurrentList.getFirst());
        assertEquals("value!", this.concurrentList.get(0));

        this.concurrentList.add("second");

        assertEquals(2, this.concurrentList.size());
        assertEquals("second", this.concurrentList.get(1));
    }

    @Test
    public void testAddIndex() {
        this.concurrentList.add("test1");
        this.concurrentList.add("test2");
        this.concurrentList.add("test3");

        this.concurrentList.add(1, "test4");

        assertEquals("test4", this.concurrentList.get(1));
        assertEquals("test2", this.concurrentList.get(2));
    }

    @Test
    public void testRemove() {
        this.concurrentList.add("test1");
        assertEquals("test1", this.concurrentList.remove(0));
        assertEquals(0, this.concurrentList.size());
        assertTrue(this.concurrentList.isEmpty());

        try {
            this.concurrentList.get(0);
            fail("get didn't throw exception");
        } catch (IndexOutOfBoundsException e) { /* Expected */ }

        this.concurrentList.add("test2");
        assertEquals("test2", this.concurrentList.get(0));
        assertEquals("test2", this.concurrentList.remove(0));
        assertEquals(0, this.concurrentList.size());
        assertTrue(this.concurrentList.isEmpty());
    }

    @Test
    public void testToArray() {
        a6();

        assertArrayEquals(A6, this.concurrentList.toArray());
        assertArrayEquals(A6, this.concurrentList.toArray(new String[0]));
    }

    @Test
    public void testIterator() {
        a6();

        Iterator<String> iterator = this.concurrentList.iterator();
        assertEquals("a", iterator.next());
        iterator.remove();

        assertEquals(6, this.concurrentList.size());
        assertEquals("b", this.concurrentList.getFirst());
    }

    @Test
    public void testListIterator() {
        a6();

        ListIterator<String> iterator = this.concurrentList.listIterator();
        assertEquals("a", iterator.next());
        iterator.add("aa");

        assertArrayEquals(new String[] {"aa", "a", "b", "c", "d", "e", "f", "g"}, this.concurrentList.toArray());

        assertEquals("aa", iterator.previous());
        iterator.remove();

        assertArrayEquals(A6, this.concurrentList.toArray());
    }

    @Test
    public void testListIteratorWithIndex() {
        a6();

        ListIterator<String> iterator = this.concurrentList.listIterator(3);
        assertEquals("d", iterator.next());
        iterator.add("aa");

        assertArrayEquals(new String[] {"a", "b", "c", "aa", "d", "e", "f", "g"}, this.concurrentList.toArray());

        assertEquals("aa", iterator.previous());
        iterator.remove();

        assertArrayEquals(A6, this.concurrentList.toArray());
    }

    private void a6() {
        this.concurrentList.add("a");
        this.concurrentList.add("b");
        this.concurrentList.add("c");
        this.concurrentList.add("d");
        this.concurrentList.add("e");
        this.concurrentList.add("f");
        this.concurrentList.add("g");
    }
}
