package tv.isshoni.araragi.annotation.test;

import static org.junit.Assert.assertEquals;

import tv.isshoni.araragi.annotation.internal.AnnotationManager;
import tv.isshoni.araragi.annotation.test.model.Second;
import tv.isshoni.araragi.annotation.test.model.TestAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class TestAnnotationManager {

    public static final String EXPECTED_VALUE = "EXPECT ME";

    private OutputStream output;

    @Before
    public void before() {
        this.output = new ByteArrayOutputStream();

        System.setOut(new PrintStream(this.output));
    }

    @Test
    public void testAnnotationManagerProcessorConstructors() {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);
        annotationManager.discoverAnnotation(Second.class);

        assertEquals(EXPECTED_VALUE, this.output.toString());
    }

    @Test
    public void testAnnotationManagerMagicMethod() throws Exception {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);

        annotationManager.execute(TestAnnotationManager.class.getMethod("aMethod", String.class), this);

        assertEquals(EXPECTED_VALUE, this.output.toString());
    }

    public void aMethod(@TestAnnotation(EXPECTED_VALUE) String str) {
        System.out.print(str);
    }
}
