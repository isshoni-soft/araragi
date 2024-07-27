package tv.isshoni.test.araragi.core.collection.map;

import org.junit.Before;
import org.junit.Test;
import tv.isshoni.araragi.data.collection.map.TypeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestTypeMap {

    private final TypeMap<Class<?>, String> STRING_TYPE_MAP = new TypeMap<>();

    @Before
    public void before() {
        STRING_TYPE_MAP.clear();
    }

    @Test
    public void testGet() {
        STRING_TYPE_MAP.put(Number.class, "NUMBER!!!");
        STRING_TYPE_MAP.put(Integer.class, "INTEGER!!!");

        assertEquals("NUMBER!!!", STRING_TYPE_MAP.get(Number.class));
        assertEquals("INTEGER!!!", STRING_TYPE_MAP.get(Integer.class));
        assertEquals("NUMBER!!!", STRING_TYPE_MAP.get(Float.class));
    }

    @Test
    public void testDirectGet() {
        STRING_TYPE_MAP.put(Integer.class, "INTEGER!!!");

        assertNull(STRING_TYPE_MAP.directGet(Number.class));
    }

    @Test
    public void testCacheGet() {
        STRING_TYPE_MAP.put(Number.class, "NUMBER!!!");

        assertNull(STRING_TYPE_MAP.cacheGet(Float.class));
        assertEquals("NUMBER!!!", STRING_TYPE_MAP.get(Float.class));
        assertEquals("NUMBER!!!", STRING_TYPE_MAP.cacheGet(Float.class));
    }

    @Test
    public void testResetCache() {
        testCacheGet();

        STRING_TYPE_MAP.resetCache();

        assertNull(STRING_TYPE_MAP.cacheGet(Float.class));
    }

    @Test
    public void testContainsKey() {
        STRING_TYPE_MAP.put(Integer.class, "NUMBER!!!");

        assertTrue(STRING_TYPE_MAP.containsKey(Integer.class));
        assertFalse(STRING_TYPE_MAP.containsKey(Number.class));
    }

    @Test
    public void testContainsParent() {
        STRING_TYPE_MAP.put(Number.class, "NUMBER!!!");

        assertTrue(STRING_TYPE_MAP.containsParent(Integer.class));
        assertFalse(STRING_TYPE_MAP.containsKey(Integer.class));
    }
}
