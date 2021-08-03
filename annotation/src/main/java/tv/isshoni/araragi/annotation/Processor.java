package tv.isshoni.araragi.annotation;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Processor {

    Class<? extends IAnnotationProcessor<?>>[] value();
}
