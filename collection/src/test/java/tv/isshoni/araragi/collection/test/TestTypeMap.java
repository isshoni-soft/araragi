package tv.isshoni.araragi.collection.test;

import static org.junit.Assert.assertEquals;

import tv.isshoni.araragi.collection.TypeMap;

import org.junit.Before;
import org.junit.Test;

public class TestTypeMap {

    private final TypeMap<Class<?>, String> STRING_TYPE_MAP = new TypeMap<>();

    @Before
    public void before() {
        STRING_TYPE_MAP.clear();
    }

    @Test
    public void testGetChild() {
        STRING_TYPE_MAP.put(Number.class, "NUMBER!!!");

        assertEquals("NUMBER!!!", STRING_TYPE_MAP.getChild(Integer.class));
        assertEquals("NUMBER!!!", STRING_TYPE_MAP.quickGet(Integer.class));
    }

    @Test
    public void testGetParent() {
        STRING_TYPE_MAP.put(Integer.class, "INTEGER!!!");

        assertEquals("INTEGER!!!", STRING_TYPE_MAP.getParent(Number.class));
        assertEquals("INTEGER!!!", STRING_TYPE_MAP.quickGet(Number.class));
    }
}
