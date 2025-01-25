package institute.isshoni.test.araragi.core.functional;

import org.junit.Test;
import institute.isshoni.araragi.functional.ObjHelpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestObjHelpers {

    @Test
    public void testIsOneNull() {
        assertTrue(ObjHelpers.isOneNull(1, 2, 3, 4, true, true, 5, "l", null));
        assertFalse(ObjHelpers.isOneNull("a", 'a', 1, true, 3, 4, "abcd", new Object()));
    }
}
