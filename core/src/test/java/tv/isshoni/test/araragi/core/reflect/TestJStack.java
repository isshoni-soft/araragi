package tv.isshoni.test.araragi.core.reflect;

import org.junit.Test;
import tv.isshoni.araragi.reflect.JStack;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class TestJStack {

    @Test
    public void testGetParentMethod() throws NoSuchMethodException {
        Method enclosing = new Object(){}.getClass().getEnclosingMethod();
        String format = "%s.%s";

        JStack.forEach(f -> System.out.printf((format) + "%n", f.getClassName(), f.getMethodName()));
        assertEquals(enclosing, JStack.getParentMethod());
    }
}
