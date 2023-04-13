package tv.isshoni.araragi.annotation.test;

import org.junit.Test;
import tv.isshoni.araragi.annotation.manager.AnnotationManager;
import tv.isshoni.araragi.annotation.test.model.ComplexConstructor;
import tv.isshoni.araragi.annotation.test.model.MixedConstructed;
import tv.isshoni.araragi.annotation.test.model.Second;
import tv.isshoni.araragi.annotation.test.model.TestAnnotation;

import static org.junit.Assert.assertEquals;

public class TestMixedConstructors {

    @Test
    public void testAnnotationManagerMixedConstructor() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);

        MixedConstructed mixed = annotationManager.construct(MixedConstructed.class, "other", "other2");

        assertEquals("value!", mixed.getValue());
        assertEquals("other", mixed.getOther());
        assertEquals("other2", mixed.getOther2());
    }

    @Test
    public void testAnnotationManagerMixedConstructorAdvanced() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);
        annotationManager.discoverAnnotation(Second.class);

        MixedConstructed mixed = annotationManager.construct(MixedConstructed.class, 10);

        assertEquals("value!", mixed.getValue());
        assertEquals("DEFAULT", mixed.getOther());
        assertEquals("10", mixed.getOther2());
    }

    @Test
    public void testAnnotationManagerComplexConstructor() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);

        ComplexConstructor complex = annotationManager.construct(ComplexConstructor.class, 10, "str");

        assertEquals("injected", complex.getInjected());
        assertEquals("str", complex.getOne());
        assertEquals(10, complex.getTwo());
    }
}
