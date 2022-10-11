package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IPreparedAnnotationProcessor<T extends IAnnotationProcessor<Annotation>> extends Comparable<IPreparedAnnotationProcessor<T>> {

    default void executeClass(Object target) {
        this.getProcessor().executeClass(target, (Class<?>) getElement(), this.getAnnotation());
    }

    default void executeMethod(Object target) {
        this.getProcessor().executeMethod(target, (Method) getElement(), this.getAnnotation());
    }

    default void executeField(Object target) {
        this.getProcessor().executeField(target, (Field) getElement(), this.getAnnotation());
    }

    default int compareTo(IPreparedAnnotationProcessor o) {
        return Integer.compare(this.getProcessor().getWeight(this.getAnnotation()), o.getProcessor().getWeight(o.getAnnotation()));
    }

    Annotation getAnnotation();

    AnnotatedElement getElement();

    T getProcessor();
}
