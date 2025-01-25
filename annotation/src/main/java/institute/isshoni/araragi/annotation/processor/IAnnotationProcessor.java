package institute.isshoni.araragi.annotation.processor;

import institute.isshoni.araragi.annotation.IncompatibleWith;
import institute.isshoni.araragi.annotation.processor.weight.WeightCalculator;
import institute.isshoni.araragi.annotation.manager.IAnnotationManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public interface IAnnotationProcessor<A extends Annotation> {

    default void onDiscovery(Class<A> clazz) { }

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
        List<Class<? extends Annotation>> incompatible = new LinkedList<>();

        if (annotation.annotationType().isAnnotationPresent(IncompatibleWith.class)) {
            incompatible.addAll(Arrays.asList(annotation.annotationType().getAnnotation(IncompatibleWith.class).value()));
        }

        return incompatible;
    }
}
