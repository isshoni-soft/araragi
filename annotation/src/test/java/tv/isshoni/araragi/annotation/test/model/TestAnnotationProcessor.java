package tv.isshoni.araragi.annotation.test.model;

import tv.isshoni.araragi.annotation.processor.IParameterSupplier;

import java.lang.reflect.Parameter;

public class TestAnnotationProcessor implements IParameterSupplier<TestAnnotation, String> {

    public TestAnnotationProcessor() {
        System.err.println("--- Construct TestAnnotationProcessor ---");
    }

    @Override
    public String supply(TestAnnotation annotation, String previous, Parameter parameter) {
        return annotation.value();
    }
}
