package tv.isshoni.araragi.annotation.model;

import tv.isshoni.araragi.annotation.internal.WeightCalculator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public interface IAnnotationProcessor<A extends Annotation> {

    default void executeClass(Class<?> clazz, A annotation) { }

    default void executeField(Field field, A annotation) { }

    default void executeMethod(Method method, A annotation) { }

    default int getWeight(A annotation) {
        return WeightCalculator.INSTANCE.calculateWeight(annotation);
    }

    default List<Class<? extends Annotation>> getIncompatibleWith(A annotation) {
        return new LinkedList<>();
    }
}
