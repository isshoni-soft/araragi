package tv.isshoni.araragi.reflect;

import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ReflectionUtil {

    public static Class<?>[] getParameterizedTypes(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return Streams.to(parameterizedType.getActualTypeArguments())
                    .map(Type::getTypeName)
                    .map(ReflectionUtil::clazz)
                    .filter(Objects::nonNull)
                    .toArray(Class[]::new);
        } else {
            return null;
        }
    }

    public static boolean isParameterized(Type type) {
        return ParameterizedType.class.isAssignableFrom(type.getClass());
    }

    public static Class<?> clazz(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean hasConstructor(Class<?> clazz, Class<?>... parameters) {
        try {
            clazz.getConstructor(parameters);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

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

    public static Set<Class<? extends Annotation>> getAllParameterAnnotationTypes(Collection<Executable> executables) {
        return Streams.to(executables)
                .flatMap(e -> ReflectionUtil.getAllParameterAnnotationTypes(e).stream())
                .collect(Collectors.toSet());
    }

    public static Set<Class<? extends Annotation>> getAllParameterAnnotationTypes(Executable executable) {
        HashSet<Class<? extends Annotation>> result = new HashSet<>();

        for (Parameter parameter : executable.getParameters()) {
            result.addAll(Streams.to(parameter.getAnnotations())
                    .map(Annotation::annotationType)
                    .toList());
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
