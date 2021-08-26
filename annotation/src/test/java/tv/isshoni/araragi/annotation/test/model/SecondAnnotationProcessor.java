package tv.isshoni.araragi.annotation.test.model;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.test.TestAnnotationManager;

public class SecondAnnotationProcessor implements IAnnotationProcessor<Second> {

    public SecondAnnotationProcessor(@TestAnnotation(TestAnnotationManager.EXPECTED_VALUE) String str) {
        System.err.println("--- Construct SecondAnnotationProcessor ---");
        System.out.print(str);
    }
}
