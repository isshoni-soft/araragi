package tv.isshoni.araragi.reflect;

import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReflectionUtil {

    public static <T> T execute(Class<?> from, Object target, String methodName, Object... parameters) {
        try {
            return (T) from.getMethod(methodName, convertObjectsToClass(parameters)).invoke(target, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T construct(Class<T> clazz, Object... parameters) {
        try {
            return construct(clazz.getConstructor(convertObjectsToClass(parameters)), parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T construct(Constructor<T> constructor, Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void injectField(Field field, Object target, Object injected) {
        boolean couldAccess = field.canAccess(target);

        if (!couldAccess) {
            field.setAccessible(true);
        }

        try {
            field.set(target, injected);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (!couldAccess) {
            field.setAccessible(false);
        }
    }

    public static Class<?>[] convertObjectsToClass(Object... objects) {
        Class<?>[] result = new Class<?>[objects.length];

        for (int x = 0; x < objects.length; x++) {
            result[x] = objects[x].getClass();
        }

        return result;
    }

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
