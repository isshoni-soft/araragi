package tv.isshoni.araragi.annotation.test.model;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;

public class SecondAnnotationProcessor implements IAnnotationProcessor<Second> {

    public SecondAnnotationProcessor(@TestAnnotation("Annotation") String str) {
        System.err.println("--- Construct SecondAnnotationProcessor ---");
        System.out.println(str);
    }
}
