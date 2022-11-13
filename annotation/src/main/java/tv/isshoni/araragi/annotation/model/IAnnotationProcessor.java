package tv.isshoni.araragi.annotation.model;

import tv.isshoni.araragi.annotation.internal.WeightCalculator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public interface IAnnotationProcessor<A extends Annotation> {

    default void executeClass(Object target, Class<?> clazz, A annotation) { }

    default void executeField(Object target, Field field, A annotation) { }

    default void executeMethod(Object target, Method method, A annotation) { }

    default void executeClass(Object target, Class<?> clazz, A annotation, IAnnotationManager annotationManager) {
        executeClass(target, clazz, annotation);
    }

    default void executeField(Object target, Field field, A annotation, IAnnotationManager annotationManager) {
        executeField(target, field, annotation);
    }

    default void executeMethod(Object target, Method method, A annotation, IAnnotationManager annotationManager) {
        executeMethod(target, method, annotation);
    }

    default int getWeight(A annotation) {
        return WeightCalculator.INSTANCE.calculateWeight(annotation);
    }

    default List<Class<? extends Annotation>> getIncompatibleWith(A annotation) {
        return new LinkedList<>();
    }
}
