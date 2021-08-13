package tv.isshoni.araragi.annotation.model;

import tv.isshoni.araragi.data.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public interface IAnnotationManager {

    <T extends Annotation> void unregister(Class<T> annotation);

    void discover(Class<? extends Annotation> annotation);

    void register(Class<? extends Annotation>[] annotations, Class<? extends IAnnotationProcessor<?>>... processors);

    void register(Class<? extends Annotation>[] annotations, IAnnotationProcessor<?>... processors);

    void register(Class<? extends Annotation> annotation, Class<? extends IAnnotationProcessor<?>>... processors);

    void register(Class<? extends IAnnotationProcessor> processor, BiFunction<Annotation, IAnnotationProcessor<Annotation>, IPreparedAnnotationProcessor> converter);

    <T extends Annotation> void register(Class<T> annotation, IAnnotationProcessor<?>... processors);

    IAnnotationProcessor<?> construct(Class<? extends IAnnotationProcessor<?>> processor);

    <A extends Annotation> int calculateWeight(Collection<A> annotations);

    List<IAnnotationProcessor<?>> get(Class<? extends Annotation> annotation);

    Collection<Class<? extends Annotation>> getManagedAnnotations();

    IPreparedAnnotationProcessor prepare(Annotation annotation, IAnnotationProcessor<Annotation> processor);

    List<IPreparedAnnotationProcessor> toExecutionList(Collection<Annotation> annotations);

    List<Annotation> getManagedAnnotationsOn(AnnotatedElement element);

    List<Pair<Class<? extends Annotation>, Class<? extends Annotation>>> getConflictingAnnotations(Collection<Annotation> annotations);

    boolean hasManagedAnnotation(AnnotatedElement element);

    <A extends Annotation> boolean hasConflictingAnnotations(Collection<A> annotations);

    int getTotalProcessors();
}
