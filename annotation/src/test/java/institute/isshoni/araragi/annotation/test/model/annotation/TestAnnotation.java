package institute.isshoni.araragi.annotation.test.model.annotation;

import institute.isshoni.araragi.annotation.Processor;
import institute.isshoni.araragi.annotation.Weight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Weight(5)
@Processor(TestAnnotationProcessor.class)
public @interface TestAnnotation {

    String value() default "NULL";
}
