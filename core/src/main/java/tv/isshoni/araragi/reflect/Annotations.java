package tv.isshoni.araragi.reflect;

import tv.isshoni.araragi.reflect.annotation.Compound;
import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Deprecated // TODO: Incomplete class
public class Annotations {

//    public static <E extends Annotation> E getAnnotation(AnnotatedElement element, Class<? extends E> annotation) {
//
//    }

    private static Annotation[] findAnnotations(AnnotatedElement element) {
        return findAnnotations(element, new LinkedList<>()).toArray(new Annotation[0]);
    }

    private static List<Annotation> findAnnotations(AnnotatedElement element, List<Annotation> annotations) {
        Streams.to(element.getAnnotations())
                .forEach(a -> {
                    Class<? extends Annotation> at = a.annotationType();
                    annotations.add(a);

                    if (at.isAnnotationPresent(Compound.class)) {
                        Compound compound = at.getAnnotation(Compound.class);
                        List<Class<? extends Annotation>> blacklist = Arrays.asList(compound.blacklist());

                        Streams.to(at.getAnnotations())
                                .map(Annotation::annotationType)
                                .filterInverted(blacklist::contains)
                                .forEach(c -> findAnnotations(c, annotations));
                    }
                });

        return annotations;
    }

    private static AraragiAnnotatedElement makeElement(AnnotatedElement element) {
        return new AraragiAnnotatedElement(element, findAnnotations(element));
    }

    public static class AraragiAnnotatedElement implements AnnotatedElement {
        private final AnnotatedElement wrapped;

        private Annotation[] annotations;

        private AraragiAnnotatedElement(AnnotatedElement wrapped, Annotation... annotations) {
            this.wrapped = wrapped;
            this.annotations = annotations;
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            return (T) Streams.to(this.annotations)
                    .collect(Collectors.toMap(Annotation::annotationType, Function.identity())).get(annotationClass);
        }

        @Override
        public Annotation[] getAnnotations() {
            return this.annotations.clone();
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return this.annotations.clone();
        }
    }

    public enum AraragiElementType {
        TYPE(Class.class, ElementType.TYPE),
        FIELD(Field.class, ElementType.FIELD),
        METHOD(Method.class, ElementType.METHOD),
        PARAMETER(Parameter.class, ElementType.PARAMETER),
        CONSTRUCTOR(Constructor.class, ElementType.CONSTRUCTOR),
        ANNOTATION_TYPE(Class.class, ElementType.ANNOTATION_TYPE),
        PACKAGE(Package.class, ElementType.PACKAGE),
        MODULE(Module.class, ElementType.MODULE),
        RECORD_COMPONENT(RecordComponent.class, ElementType.RECORD_COMPONENT);

        private static final Map<Class<? extends AnnotatedElement>, AraragiElementType> BY_ELEMENT = new HashMap<>();

        static {
            for (AraragiElementType et : values()) {
                if (et.elementType.equals(Class.class)) {
                    continue;
                }

                BY_ELEMENT.put(et.elementType, et);
            }
        }

        private final Class<? extends AnnotatedElement> elementType;
        private final ElementType jElementType;

        AraragiElementType(Class<? extends AnnotatedElement> elementType, ElementType jElementType) {
            this.elementType = elementType;
            this.jElementType = jElementType;
        }

        public ElementType getJavaElementType() {
            return this.jElementType;
        }

        public static AraragiElementType get(AnnotatedElement element) {
            boolean annotation = false;

            if (element instanceof Class) {
                try {
                    Class<? extends Annotation> a = (Class<? extends Annotation>) element;
                    annotation = true;
                } catch (ClassCastException ignored) { }

                if (annotation) {
                    return ANNOTATION_TYPE;
                } else {
                    return TYPE;
                }
            }

            return BY_ELEMENT.get(element.getClass());
        }
    }
}
