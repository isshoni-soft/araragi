package tv.isshoni.test.araragi.core.reflect;

import org.junit.Test;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.test.araragi.model.ClassB;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestReflectionUtil {

    public void otherMethod(List<String> param, String other) { }

    @Test
    public void testIsParameterized() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("otherMethod", List.class, String.class);
        Type[] paramTypes =  Streams.to(method.getParameters())
                .map(Parameter::getParameterizedType)
                .toArray(Type[]::new);

        assertEquals(2, paramTypes.length);
        assertTrue(ReflectionUtil.isParameterized(paramTypes[0]));
        assertFalse(ReflectionUtil.isParameterized(paramTypes[1]));
    }

    @Test
    public void testGetParameterizedTypes() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("otherMethod", List.class, String.class);
        Type[] paramTypes =  Streams.to(method.getParameters())
                .map(Parameter::getParameterizedType)
                .toArray(Type[]::new);

        assertEquals(String.class, ReflectionUtil.getParameterizedTypes(paramTypes[0])[0]);
        assertNull(ReflectionUtil.getParameterizedTypes(paramTypes[1]));
    }

    @Test
    public void testFetchFrom() throws NoSuchFieldException {
        Field field = ClassB.class.getDeclaredField("someInt");
        ClassB b = new ClassB(5);

        int val = ReflectionUtil.fetchFrom(field, b);

        assertEquals(5, val);
    }
}
