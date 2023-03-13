package tv.isshoni.test.araragi.core.collection.map;

import org.junit.Before;
import org.junit.Test;
import tv.isshoni.araragi.data.collection.map.token.TokenMap;
import tv.isshoni.araragi.string.format.StringFormatter;

import static org.junit.Assert.assertEquals;

public class TestTokenMap {

    private TokenMap<String> map;

    @Before
    public void before() {
        this.map = new TokenMap<>(new StringFormatter("{", "}"));
    }

    @Test
    public void testSimpleTokenMap() {
        this.map.put("some key", "data.");
        this.map.put("some {replaced} key", "other data.");

        assertEquals("data.", this.map.get("some key"));
        assertEquals("other data.", this.map.get("some generic key"));
    }

    @Test
    public void testLimitsTokenMap() {
        this.map.put("some {other} special key", "data.");
        this.map.put("some {other} different key", "other data.");

        assertEquals("data.", this.map.get("some keykeykey keykey special key"));
        assertEquals("other data.", this.map.get("some keykeykey keykey different key"));
        assertEquals("other data.", this.map.get("some special key different key"));
    }
}
