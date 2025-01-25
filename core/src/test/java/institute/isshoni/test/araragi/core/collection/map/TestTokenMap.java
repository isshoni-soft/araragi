package institute.isshoni.test.araragi.core.collection.map;

import org.junit.Before;
import org.junit.Test;
import institute.isshoni.araragi.data.Pair;
import institute.isshoni.araragi.data.collection.map.token.TokenMap;
import institute.isshoni.araragi.string.format.StringFormatter;
import institute.isshoni.araragi.string.format.StringToken;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        assertNull(this.map.get("some key."));
        assertNull(this.map.get("some generic key."));
    }

    @Test
    public void testLimitsTokenMap() {
        this.map.put("some {other} special key", "data.");
        this.map.put("some {other} different key", "other data.");

        assertEquals("data.", this.map.get("some keykeykey keykey special key"));
        assertEquals("other data.", this.map.get("some keykeykey keykey different key"));
        assertEquals("other data.", this.map.get("some special key different key"));
        assertEquals("data.", this.map.get("some special key special key"));
        assertNull(this.map.get("some special key special key."));
    }

    @Test
    public void testMultipleTokens() {
        this.map.put("/users/{path}/post/{second}/delete", "data");

        assertEquals("data", this.map.get("/users/name/post/id/delete"));
        assertNull(this.map.get("/users/name/post/id/delete/other"));
    }

    @Test
    public void testGetTokenized() {
        this.map.put("/users/{userId}/", "data!");

        assertEquals(Pair.of("data!", new LinkedList<StringToken>() {{
            add(new StringToken(7, 14, "userId", "testuser"));
        }}), this.map.getTokenized("/users/testuser/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionOnEndToken() {
        this.map.put("test {good}!", "good!");

        assertEquals("good!", this.map.get("test magic!"));

        this.map.put("test {bad}", "bad!");
    }
}
