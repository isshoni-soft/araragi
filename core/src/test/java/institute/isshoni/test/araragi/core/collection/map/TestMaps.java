package institute.isshoni.test.araragi.core.collection.map;

import org.junit.Test;
import institute.isshoni.araragi.data.Pair;
import institute.isshoni.araragi.data.collection.map.Maps;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestMaps {

    @Test
    public void testOfPairsVarargs() {
        Map<String, String> map = Maps.ofPairs(new Pair<>("First", "Second"), new Pair<>("Second", "Third"));

        assertEquals("Second", map.get("First"));
        assertEquals("Third", map.get("Second"));
    }
}
