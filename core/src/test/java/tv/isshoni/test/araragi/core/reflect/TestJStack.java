package tv.isshoni.test.araragi.core.reflect;

import org.junit.Test;
import tv.isshoni.araragi.reflect.JStack;

import static org.junit.Assert.assertEquals;

public class TestJStack {

    @Test
    public void testGetParentMethod() throws NoSuchMethodException {
        assertEquals(new Object(){}.getClass().getEnclosingMethod(), JStack.getParentMethod());
    }
}
