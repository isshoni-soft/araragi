package tv.isshoni.araragi.annotation.test;

import static org.junit.Assert.assertEquals;

import tv.isshoni.araragi.annotation.internal.AnnotationManager;
import tv.isshoni.araragi.annotation.test.model.Second;
import tv.isshoni.araragi.annotation.test.model.TestAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class TestAnnotationManager {

    @Test
    public void testAnnotationManager() {
        OutputStream stream = new ByteArrayOutputStream();

        System.setOut(new PrintStream(stream));

        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);
        annotationManager.discoverAnnotation(Second.class);

        assertEquals("Annotation\n", stream.toString());
    }
}
