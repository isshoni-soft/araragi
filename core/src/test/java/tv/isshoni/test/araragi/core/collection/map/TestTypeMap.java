package tv.isshoni.test.araragi.core.collection.map;

import org.junit.Before;
import org.junit.Test;
import tv.isshoni.araragi.data.collection.map.TypeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTypeMap {

    private final TypeMap<Class<?>, String> STRING_TYPE_MAP = new TypeMap<>();

    @Before
    public void before() {
        STRING_TYPE_MAP.clear();
    }

    @Test
    public void testGetParent() {
        STRING_TYPE_MAP.put(Number.class, "NUMBER!!!");

        assertEquals("NUMBER!!!", STRING_TYPE_MAP.getParent(Integer.class));
        assertEquals("NUMBER!!!", STRING_TYPE_MAP.quickGet(Integer.class));
        assertEquals("NUMBER!!!", STRING_TYPE_MAP.getParent(Integer.class));
    }

    @Test
    public void testGetChild() {
        STRING_TYPE_MAP.put(Integer.class, "INTEGER!!!");

        assertEquals("INTEGER!!!", STRING_TYPE_MAP.getChild(Number.class));
        assertEquals("INTEGER!!!", STRING_TYPE_MAP.quickGet(Number.class));
        assertEquals("INTEGER!!!", STRING_TYPE_MAP.getChild(Number.class));
    }

    @Test
    public void testContainsKey() {
        STRING_TYPE_MAP.put(Number.class, "NUMBER!!!");

        assertTrue(STRING_TYPE_MAP.containsKey(Integer.class));
    }
}
