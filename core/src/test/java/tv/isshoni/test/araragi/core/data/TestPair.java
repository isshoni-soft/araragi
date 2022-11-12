package tv.isshoni.test.araragi.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import tv.isshoni.araragi.data.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestPair {

    @Test
    public void testConstruction() {
        Pair<String, String> pair = new Pair<>("One", "Two");

        assertNotNull(pair);

        HashMap<String, String> map = new HashMap<>();
        map.put("One", "Two");
        map.put("Three", "Four");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            Pair<String, String> p = new Pair<>(entry);

            assertNotNull(p);
        }
    }

    @Test
    public void testGetters() {
        Pair<String, String> pair = new Pair<>("One", "Two");

        assertEquals("One", pair.getFirst());
        assertEquals("Two", pair.getSecond());
    }

    @Test
    public void testSetters() {
        Pair<String, String> pair = new Pair<>("One", "Two");

        pair.setFirst("Two");
        pair.setSecond("One");

        assertEquals("Two", pair.getFirst());
        assertEquals("One", pair.getSecond());
    }

    @Test
    public void testEquals() {
        Pair<String, String> p1 = new Pair<>("One", "Two");
        Pair<String, String> p2 = new Pair<>("One", "Two");
        Pair<String, String> p3 = new Pair<>("Two", "One");
        String string = "One Two";

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, string);
    }

    @Test
    public void testHashCode() {
        Pair<String, String> p1 = new Pair<>("One", "Two");
        Pair<String, String> p2 = new Pair<>("One", "Two");
        Pair<String, String> p3 = new Pair<>("Five", "Six");
        Pair<String, String> p4 = new Pair<>("Two", "One");

        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
        assertNotEquals(p1.hashCode(), p4.hashCode());
    }

    @Test
    public void testToString() {
        Pair<String, String> pair = new Pair<>("One", "Two");

        assertEquals(pair.toString(), "Pair[first=One,second=Two]");
    }

    @Test
    public void testFirstComparator() {
        Pair<Integer, String> p1 = new Pair<>(0, "Zero");
        Pair<Integer, String> p2 = new Pair<>(1, "One");
        Pair<Integer, String> p3 = new Pair<>(2, "Two");
        Pair<Integer, String> p4 = new Pair<>(3, "Three");
        Pair<Integer, String> p5 = new Pair<>(4, "Four");
        Pair<Integer, String> p6 = new Pair<>(5, "Five");

        List<Pair<Integer, String>> set = new LinkedList<>();
        set.add(p6);
        set.add(p2);
        set.add(p4);
        set.add(p5);
        set.add(p3);
        set.add(p1);

        List<Pair<Integer, String>> expected = new LinkedList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        expected.add(p4);
        expected.add(p5);
        expected.add(p6);

        assertEquals(set.stream()
                .sorted(Pair.compareFirst())
                .collect(Collectors.toCollection(LinkedList::new)), expected);
    }

    @Test
    public void testSecondComparator() {
        Pair<String, Integer> p1 = new Pair<>("Zero", 0);
        Pair<String, Integer> p2 = new Pair<>("One", 1);
        Pair<String, Integer> p3 = new Pair<>("Two", 2);
        Pair<String, Integer> p4 = new Pair<>("Three", 3);
        Pair<String, Integer> p5 = new Pair<>("Four", 4);
        Pair<String, Integer> p6 = new Pair<>("Five", 5);

        List<Pair<String, Integer>> set = new LinkedList<>();
        set.add(p6);
        set.add(p2);
        set.add(p4);
        set.add(p5);
        set.add(p3);
        set.add(p1);

        List<Pair<String, Integer>> expected = new LinkedList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        expected.add(p4);
        expected.add(p5);
        expected.add(p6);

        assertEquals(set.stream()
                .sorted(Pair.compareSecond())
                .collect(Collectors.toCollection(LinkedList::new)), expected);
    }

    @Test
    public void testMapMethod() {
        Pair<String, String> pair = new Pair<>("One", "Two");

        Pair<Integer, Integer> p2 = pair.map((f, s) -> {
            assertEquals("One", f);
            assertEquals("Two", s);

            return new Pair<>(1, 2);
        });

        assertEquals(p2.getFirst().intValue(), 1);
        assertEquals(p2.getSecond().intValue(), 2);
    }

    @Test
    public void testMapFirst() {
        Pair<String, String> pair = new Pair<>("One", "Two");

        Pair<Integer, String> p2 = pair.mapFirst((f, s) -> {
            assertEquals("One", f);
            assertEquals("Two", s);

            return 1;
        });

        assertEquals(p2.getFirst().intValue(), 1);
        assertEquals(p2.getSecond(), "Two");
    }

    @Test
    public void testMapSecond() {
        Pair<String, String> pair = new Pair<>("One", "Two");

        Pair<String, Integer> p2 = pair.mapSecond((f, s) -> {
            assertEquals("One", f);
            assertEquals("Two", s);

            return 2;
        });

        assertEquals(p2.getFirst(), "One");
        assertEquals(p2.getSecond().intValue(), 2);
    }

    @Test
    public void testToMapVarargs() {
        Map<String, String> map = Pair.toMap(new Pair<>("First", "Second"), new Pair<>("Second", "Third"));

        assertEquals("Second", map.get("First"));
        assertEquals("Third", map.get("Second"));
    }
}
