package tv.isshoni.test.araragi.core.string;

import org.junit.Test;
import tv.isshoni.araragi.string.format.StringFormatter;

import static org.junit.Assert.assertEquals;

public class TestStringFormatter {

    @Test
    public void testSupplier() {
        StringFormatter formatter = new StringFormatter();
        formatter.registerSupplier("test", () -> "value!");

        assertEquals("value!", formatter.format("${test}"));
    }
}
