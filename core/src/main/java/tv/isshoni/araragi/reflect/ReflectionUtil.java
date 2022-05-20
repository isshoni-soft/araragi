package tv.isshoni.araragi.reflect;

import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReflectionUtil {

    @SafeVarargs
    public static <T> List<Constructor<T>> discoverAnnotatedConstructors(Class<T> clazz, Class<? extends Annotation>... annotations) {
        List<Constructor<T>> result = new LinkedList<>();

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (hasAnyAnnotation(constructor, annotations) || hasAnyParameterAnnotation(constructor)) {
                result.add((Constructor<T>) constructor);
            }
        }

        return result;
    }

    public static List<Class<? extends Annotation>> getAllParameterAnnotationTypes(Executable executable) {
        List<Class<? extends Annotation>> result = new LinkedList<>();

        for (Parameter parameter : executable.getParameters()) {
            result.addAll(Streams.to(parameter.getAnnotations()).map(Annotation::annotationType).toList());
        }

        return result;
    }

    @SafeVarargs
    public static boolean hasAnyParameterAnnotation(Executable executable, Class<? extends Annotation>... annotations) {
        return hasAnyParameterAnnotation(executable, Arrays.asList(annotations));
    }

    public static boolean hasAnyParameterAnnotation(Executable executable, List<Class<? extends Annotation>> annotations) {
        for (Parameter parameter : executable.getParameters()) {
            if (hasAnyAnnotation(parameter, annotations)) {
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    public static boolean hasAnyAnnotation(AnnotatedElement element, Class<? extends Annotation>... annotation) {
        return hasAnyAnnotation(element, Arrays.asList(annotation));
    }

    public static boolean hasAnyAnnotation(AnnotatedElement element, List<Class<? extends Annotation>> annotations) {
        if (annotations.isEmpty()) {
            return element.getAnnotations().length > 0;
        }

        for (Class<? extends Annotation> annotation : annotations) {
            if (element.isAnnotationPresent(annotation)) {
                return true;
            }
        }

        return false;
    }
}
