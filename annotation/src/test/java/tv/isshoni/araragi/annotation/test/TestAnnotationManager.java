package tv.isshoni.araragi.annotation.test;

import static org.junit.Assert.assertEquals;

import tv.isshoni.araragi.annotation.manager.AnnotationManager;
import tv.isshoni.araragi.annotation.test.model.Second;
import tv.isshoni.araragi.annotation.test.model.TestAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

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
    public void testAnnotationManagerMagicMethod() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);

        annotationManager.execute(TestAnnotationManager.class.getMethod("aMethod", String.class), this);

        assertEquals(EXPECTED_VALUE, this.output.toString());
    }

    @Test
    public void testAnnotationManagerMagicMethodContext() throws Throwable {
        AnnotationManager annotationManager = new AnnotationManager();

        annotationManager.discoverAnnotation(TestAnnotation.class);
        annotationManager.discoverAnnotation(Second.class);

        annotationManager.execute(TestAnnotationManager.class.getMethod("bMethod", String.class), this, new HashMap<>() {{
            put("contextual", EXPECTED_VALUE);
        }});

        assertEquals(EXPECTED_VALUE + EXPECTED_VALUE, this.output.toString());
    }

    public void bMethod(@Second("contextual") String str) {
        System.err.println("--- Execute bMethod --- (" + str + ")");
        System.out.print(str);
    }

    public void aMethod(@TestAnnotation(EXPECTED_VALUE) String str) {
        System.err.println("--- Execute aMethod --- (" + str + ")");
        System.out.print(str);
    }
}
