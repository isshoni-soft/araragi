package tv.isshoni.araragi.annotation.manager;

import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.functional.IExecutableInvoker;
import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.functional.QuadFunction;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IAnnotationManager {

    void unregisterAnnotation(Class<? extends Annotation> annotation);

    void unregisterProcessorConverter(Class<? extends IAnnotationProcessor> processor);

    void unregisterExecutableInvoker(Class<? extends Executable> executable);

    void discoverAnnotation(Class<? extends Annotation> annotation);

    void discoverProcessor(Class<? extends IAnnotationProcessor<Annotation>> processor);

    void register(Class<? extends Annotation>[] annotations, Class<? extends IAnnotationProcessor<?>>... processors);

    void register(Class<? extends Annotation>[] annotations, IAnnotationProcessor<?>... processors);

    void register(Class<? extends Annotation> annotation, Class<? extends IAnnotationProcessor<?>>... processors);

    void register(Class<? extends IAnnotationProcessor> processor, QuadFunction<Annotation, AnnotatedElement, IAnnotationProcessor<Annotation>, IAnnotationManager, IPreparedAnnotationProcessor> converter);

    void register(Class<? extends Annotation> annotation, IAnnotationProcessor<?>... processors);

    <T extends Executable> void register(Class<T> executable, IExecutableInvoker<T> invoker);

    <T extends Executable, R> R execute(T executable, Object target, Map<String, Object> runtimeContext) throws Throwable;

    <T extends Executable, R> R execute(T executable, Object target) throws Throwable;

    <R> R construct(Class<R> clazz) throws Throwable;

    void execute(Class<?> clazz);

    void execute(Object target);

    Constructor<?> discoverConstructor(Class<?> clazz);

    Constructor<?> discoverConstructor(Class<?> clazz, boolean strict);

    <A extends Annotation> int calculateWeight(Collection<A> annotations);

    List<IAnnotationProcessor<?>> get(Class<? extends Annotation> annotation);

    Collection<Class<? extends Annotation>> getManagedAnnotations();

    Collection<Class<? extends Annotation>> getAnnotationsWithProcessorType(Class<? extends IAnnotationProcessor> processor);

    IPreparedAnnotationProcessor prepare(Annotation annotation, AnnotatedElement element, IAnnotationProcessor<Annotation> processor);

    List<IPreparedAnnotationProcessor> toExecutionList(Pair<AnnotatedElement, List<Annotation>> annotations);

    List<Annotation> getManagedAnnotationsOn(AnnotatedElement element);

    List<Pair<Class<? extends Annotation>, Class<? extends Annotation>>> getConflictingAnnotations(Collection<Annotation> annotations);

    boolean hasManagedAnnotation(AnnotatedElement element);

    boolean isManagedAnnotation(Annotation annotation);

    boolean isManagedAnnotation(Class<? extends Annotation> clazz);

    <A extends Annotation> boolean hasConflictingAnnotations(Collection<A> annotations);

    Object[] prepareExecutable(Executable executable, Map<String, Object> runtimeContext);

    int getTotalProcessors();
}
