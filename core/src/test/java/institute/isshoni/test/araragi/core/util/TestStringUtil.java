package institute.isshoni.test.araragi.core.util;

import org.junit.Test;
import institute.isshoni.araragi.util.StringUtil;

import static org.junit.Assert.assertEquals;

public class TestStringUtil {

    @Test
    public void testGetCharsForNumber() {
        assertEquals("ZA", StringUtil.getCharsForNumber(27));
    }
}
