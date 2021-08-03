package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IPreparedAnnotationProcessor extends Comparable<IPreparedAnnotationProcessor> {

    default void executeClass(Class<?> clazz) {
        this.getProcessor().executeClass(clazz, this.getAnnotation());
    }

    default void executeMethod(Method method) {
        this.getProcessor().executeMethod(method, this.getAnnotation());
    }

    default void executeField(Field field) {
        this.getProcessor().executeField(field, this.getAnnotation());
    }

    default int compareTo(IPreparedAnnotationProcessor o) {
        return Integer.compare(this.getProcessor().getWeight(this.getAnnotation()), o.getProcessor().getWeight(o.getAnnotation()));
    }

    Annotation getAnnotation();

    IAnnotationProcessor<Annotation> getProcessor();
}
