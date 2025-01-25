package institute.isshoni.araragi.annotation.test;

import org.junit.Ignore;
import org.junit.Test;
import institute.isshoni.araragi.annotation.manager.AnnotationManager;
import institute.isshoni.araragi.annotation.test.model.ComplexConstructor;
import institute.isshoni.araragi.annotation.test.model.MixedConstructed;
import institute.isshoni.araragi.annotation.test.model.annotation.Second;
import institute.isshoni.araragi.annotation.test.model.annotation.TestAnnotation;
import institute.isshoni.araragi.annotation.test.model.WeirdTypesConstructor;

import static org.junit.Assert.assertEquals;

public class TestMixedConstructors {

    @Test
    public void testAnnotationManagerMixedConstructor() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);

        MixedConstructed mixed = annotationManager.construct(MixedConstructed.class, "other", "other2");

        assertEquals("value!", mixed.value);
        assertEquals("other", mixed.other);
        assertEquals("other2", mixed.other2);
    }

    @Test
    public void testAnnotationManagerMixedConstructorAdvanced() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);
        annotationManager.discoverAnnotation(Second.class);

        MixedConstructed mixed = annotationManager.construct(MixedConstructed.class, 10);

        assertEquals("value!", mixed.value);
        assertEquals("DEFAULT", mixed.other);
        assertEquals("10", mixed.other2);
    }

    @Test
    public void testAnnotationManagerComplexConstructor() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);

        ComplexConstructor complex = annotationManager.construct(ComplexConstructor.class, 10, "str");

        assertEquals("injected", complex.injected);
        assertEquals("str", complex.one);
        assertEquals(10, complex.two);
    }

    @Test
    @Ignore
    public void testAnnotationManagerComplexConstructorFullFed() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);

        ComplexConstructor complex = annotationManager.construct(ComplexConstructor.class, "inject2", 10, "str");

        assertEquals("inject2", complex.injected);
        assertEquals("str", complex.one);
        assertEquals(10, complex.two);
    }

    @Test
    public void testAnnotationManagerWeirdConstructorInts() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        WeirdTypesConstructor weird = annotationManager.construct(WeirdTypesConstructor.class, 10, 11, "str");

        assertEquals(10.0f, weird.first, 0f);
        assertEquals(11, weird.second);
        assertEquals("str", weird.third);
    }
}
