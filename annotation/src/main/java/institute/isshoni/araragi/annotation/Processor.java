package institute.isshoni.araragi.annotation;

import institute.isshoni.araragi.annotation.processor.IAnnotationProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Processor {

    Class<? extends IAnnotationProcessor<?>>[] value();
}
