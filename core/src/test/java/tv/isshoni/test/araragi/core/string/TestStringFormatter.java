package tv.isshoni.test.araragi.core.string;

import org.junit.Test;
import tv.isshoni.araragi.string.format.StringFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStringFormatter {

    @Test
    public void testSupplier() {
        StringFormatter formatter = new StringFormatter();
        formatter.registerSupplier("test", () -> "value!");

        assertEquals("value!", formatter.format("${test}"));
    }

    @Test
    public void testFormatNoTokenString() {
        StringFormatter formatter = new StringFormatter();

        assertEquals("test string!", formatter.format("test string!"));
    }

    @Test
    public void testTokenizeNoTokenString() {
        StringFormatter formatter = new StringFormatter();

        assertTrue(formatter.tokenize("test string!").isEmpty());
    }

    @Test
    public void testCustomDiscriminator() {
        StringFormatter formatter = new StringFormatter("{{", "}}");
        formatter.registerSupplier("test", () -> "value!");

        assertEquals("value!", formatter.format("{{test}}"));
    }
}
