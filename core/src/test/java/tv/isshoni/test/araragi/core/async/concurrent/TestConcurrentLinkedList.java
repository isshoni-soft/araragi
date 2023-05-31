package tv.isshoni.test.araragi.core.async.concurrent;

import org.junit.Before;
import org.junit.Test;
import tv.isshoni.araragi.concurrent.collection.ConcurrentLinkedList;
import tv.isshoni.araragi.stream.Streams;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestConcurrentLinkedList {

    private static final String[] A6 = new String[] {"a", "b", "c", "d", "e", "f", "g"};

    private ConcurrentLinkedList<String> concurrentList;

    private ExecutorService executorService;

    @Before
    public void setup() {
        this.concurrentList = new ConcurrentLinkedList<>();
        this.executorService = Executors.newFixedThreadPool(20);
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
    public void testContains() {
        this.concurrentList.add("test2");

        assertTrue(this.concurrentList.contains("test2"));
        assertFalse(this.concurrentList.contains("test1"));
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
    public void testRemoveAll() {
        a6();

        this.concurrentList.removeAll(Arrays.asList(A6));
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

        assertArrayEquals(new String[] {"a", "aa", "b", "c", "d", "e", "f", "g"}, this.concurrentList.toArray());

        assertEquals("aa", iterator.previous());
        iterator.remove();

        assertArrayEquals(A6, this.concurrentList.toArray());
    }

    @Test
    public void testListIteratorWithIndex() {
        a6();

        ListIterator<String> iterator = this.concurrentList.listIterator(3);
        assertEquals("e", iterator.next());
        iterator.add("aa");

        assertArrayEquals(new String[] {"a", "b", "c", "d", "aa", "e", "f", "g"}, this.concurrentList.toArray());

        assertEquals("aa", iterator.previous());
        iterator.remove();

        assertArrayEquals(A6, this.concurrentList.toArray());
    }

    @Test
    public void testEmptyListIterator() {
        ListIterator<String> iterator = this.concurrentList.listIterator();

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testCopyConstructor() {
        a6();

        ConcurrentLinkedList<String> otherList = new ConcurrentLinkedList<>(this.concurrentList);

        assertTrue(Streams.to(this.concurrentList).matches(otherList, Object::equals));
    }

    @Test
    public void testScaledConcurrency() throws InterruptedException {
        int size = 50000;
        String[] expected = new String[size];

        for (int x = 0; x < expected.length; x++) {
            String str;
            do {
                int generated = (int) (Math.random() * 100);
                str = String.valueOf(generated);
            } while (x > 0 && expected[x - 1].equals(str));

            expected[x] = str;
        }

        HashSet<String> submitted = new HashSet<>();

        for (String str : expected) {
            executorService.submit(() -> {
                if (!this.concurrentList.contains(str)) {
                    this.concurrentList.add(str);
                }
            });

            submitted.add(str);
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        assertTrue(submitted.containsAll(this.concurrentList));
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
