package institute.isshoni.test.araragi.core.reflect;

import org.junit.Test;
import institute.isshoni.araragi.reflect.JStack;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class TestJStack {

    @Test
    public void testGetEnclosingMethod() throws NoSuchMethodException {
        assertEquals(new Object(){}.getClass().getEnclosingMethod(), JStack.getEnclosingMethod());
        assertEquals(this.getClass().getMethod("testEnclosing"), testEnclosing());
    }

    @Test
    public void testGetParentMethod() throws NoSuchMethodException {
        assertEquals(new Object(){}.getClass().getEnclosingMethod(), testParent());
    }

    public Method testEnclosing() throws NoSuchMethodException {
        return JStack.getEnclosingMethod();
    }

    public Method testParent() throws NoSuchMethodException {
        return JStack.getParentMethod();
    }
}
