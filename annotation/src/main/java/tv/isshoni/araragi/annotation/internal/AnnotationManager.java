package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.AttachTo;
import tv.isshoni.araragi.annotation.DefaultConstructor;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.model.IAnnotationManager;
import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IExecutableInvoker;
import tv.isshoni.araragi.annotation.model.IParameterSupplier;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedParameterSupplier;
import tv.isshoni.araragi.data.collection.TypeMap;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.functional.TriFunction;
import tv.isshoni.araragi.stream.PairStream;
import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationManager implements IAnnotationManager {

    protected final Map<Class<? extends Annotation>, List<IAnnotationProcessor<?>>> annotationProcessors;

    protected final Map<Class<? extends IAnnotationProcessor>, TriFunction<Annotation, AnnotatedElement, IAnnotationProcessor<Annotation>, IPreparedAnnotationProcessor>> preparations;

    protected final Map<Class<? extends Executable>, IExecutableInvoker> executableInvokers;

    public AnnotationManager() {
        this.annotationProcessors = new TypeMap<>();
        this.preparations = new TypeMap<>();
        this.executableInvokers = new TypeMap<>();

        register(IAnnotationProcessor.class, PreparedAnnotationProcessor::new);
        register(IParameterSupplier.class, PreparedParameterSupplier::new);

        register(Method.class, (m, o, ctx) -> m.invoke(o, this.prepareExecutable(m, ctx)));
        register(Constructor.class, (c, o, ctx) -> c.newInstance(this.prepareExecutable(c, ctx)));
    }

    @Override
    public void unregisterAnnotation(Class<? extends Annotation> annotation) {
        this.annotationProcessors.remove(annotation);
    }

    @Override
    public void unregisterProcessorConverter(Class<? extends IAnnotationProcessor> processor) {
        this.preparations.remove(processor);
    }

    @Override
    public void unregisterExecutableInvoker(Class<? extends Executable> executable) {
        this.executableInvokers.remove(executable);
    }

    @Override
    public void discoverAnnotation(Class<? extends Annotation> annotation) {
        if (!annotation.isAnnotationPresent(Processor.class)) {
            throw new RuntimeException(annotation.getName() + " does not have a @Processor annotation");
        }

        register(annotation, annotation.getAnnotation(Processor.class).value());
    }

    @Override
    public void discoverProcessor(Class<? extends IAnnotationProcessor<Annotation>> processor) {
        if (!processor.isAnnotationPresent(AttachTo.class)) {
            throw new RuntimeException(processor.getName() + " does not have an @AttachTo annotation");
        }

        register(processor.getAnnotation(AttachTo.class).value(), processor);
    }

    @Override
    public void register(Class<? extends Annotation>[] annotations, Class<? extends IAnnotationProcessor<?>>... processors) {
        for (Class<? extends Annotation> annotation : annotations) {
            register(annotation, processors);
        }
    }

    @Override
    public void register(Class<? extends Annotation>[] annotations, IAnnotationProcessor<?>... processors) {
        for (Class<? extends Annotation> annotation : annotations) {
            register(annotation, processors);
        }
    }

    @Override
    public void register(Class<? extends Annotation> annotation, Class<? extends IAnnotationProcessor<?>>... processors) {
        register(annotation, Streams.to(processors)
                .map(this::discoverConstructor)
                .map(c -> {
                    try {
                        return this.execute(c, null);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(IAnnotationProcessor<?>[]::new));
    }

    @Override
    public void register(Class<? extends IAnnotationProcessor> processor, TriFunction<Annotation, AnnotatedElement, IAnnotationProcessor<Annotation>, IPreparedAnnotationProcessor> converter) {
        this.preparations.put(processor, converter);
    }

    @Override
    public void register(Class<? extends Annotation> annotation, IAnnotationProcessor<?>... processors) {
        this.annotationProcessors.compute(annotation, (a, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }

            v.addAll(Arrays.asList(processors));

            return v;
        });
    }

    @Override
    public <T extends Executable> void register(Class<T> executable, IExecutableInvoker<T> invoker) {
        this.executableInvokers.put(executable, invoker);
    }

    @Override
    public <T extends Executable, R> R execute(T executable, Object target, Map<String, Object> runtimeContext) throws Throwable {
        try {
            return (R) this.executableInvokers.get(executable.getClass()).invoke(executable, target, runtimeContext);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Override
    public <T extends Executable, R> R execute(T executable, Object target) throws Throwable {
        return execute(executable, target, new HashMap<>());
    }

    @Override
    public <R> R construct(Class<R> clazz) throws Throwable {
        return execute(discoverConstructor(clazz), null);
    }

    @Override
    public void execute(Class<?> clazz) {
        Streams.to(annotatedElementToExecutionList(clazz))
                .flatMap(List::stream)
                .forEach(ap -> ap.executeClass(null));
    }

    @Override
    public void execute(Object target) {
        List<AnnotatedElement> elements = new ArrayList<>();
        elements.addAll(Arrays.asList(target.getClass().getDeclaredFields()));
        elements.addAll(Arrays.asList(target.getClass().getDeclaredMethods()));

        Streams.to(annotatedElementToExecutionList(elements.toArray(new AnnotatedElement[0])))
                .flatMap(List::stream)
                .forEach(ap -> {
                    AnnotatedElement element = ap.getElement();

                    if (element instanceof Field) {
                        ap.executeField(target);
                    } else if (element instanceof Method) {
                        ap.executeMethod(target);
                    }
                });
    }

    @Override
    public Constructor<?> discoverConstructor(Class<?> clazz) {
        return Streams.to(clazz.getDeclaredConstructors())
                .filter(c -> Streams.to(c.getParameterAnnotations())
                        .flatMap(a -> Streams.to(a).map(Annotation::annotationType))
                        .anyMatch(getAnnotationsWithProcessorType(IParameterSupplier.class)::contains))
                .find(c -> c.isAnnotationPresent(DefaultConstructor.class), Stream::findFirst)
                .orElseGet(() -> {
                    try {
                        return clazz.getConstructor();
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                });
    }

    @Override
    public <A extends Annotation> int calculateWeight(Collection<A> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .mapToInt(p -> p.getSecond().getWeight(p.getFirst()))
                .sum();
    }

    @Override
    public List<IAnnotationProcessor<?>> get(Class<? extends Annotation> annotation) {
        return this.annotationProcessors.getOrDefault(annotation, new LinkedList<>());
    }

    @Override
    public Collection<Class<? extends Annotation>> getManagedAnnotations() {
        return this.annotationProcessors.keySet();
    }

    @Override
    public Collection<Class<? extends Annotation>> getAnnotationsWithProcessorType(Class<? extends IAnnotationProcessor> processor) {
        return Streams.to(this.annotationProcessors)
                .filter((a, l) -> l.stream()
                        .map(Object::getClass)
                        .anyMatch(processor::isAssignableFrom))
                .mapFirst()
                .collect(Collectors.toList());
    }

    @Override
    public IPreparedAnnotationProcessor prepare(Annotation annotation, AnnotatedElement element, IAnnotationProcessor<Annotation> processor) {
        return this.preparations.get(processor.getClass()).apply(annotation, element, processor);
    }

    @Override
    public List<IPreparedAnnotationProcessor> toExecutionList(Pair<AnnotatedElement, List<Annotation>> annotations) {
        return convertCollectionToProcessorStream(annotations.getSecond())
                .map((a, p) -> this.prepare(a, annotations.getFirst(), p))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<Annotation> getManagedAnnotationsOn(AnnotatedElement element) {
        return Arrays.stream(element.getAnnotations())
                .filter(f -> this.annotationProcessors.containsKey(f.annotationType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pair<Class<? extends Annotation>, Class<? extends Annotation>>> getConflictingAnnotations(Collection<Annotation> annotations) {
        List<Pair<Class<? extends Annotation>, Class<? extends Annotation>>> result = new LinkedList<>();

        List<Class<? extends Annotation>> annotationTypes = annotations.stream()
                .map(Annotation::annotationType)
                .collect(Collectors.toList());

        convertCollectionToProcessorStream(annotations)
                .forEach((a, p) -> result.addAll(Streams.to(p.getIncompatibleWith(a))
                        .filter(annotationTypes::contains)
                        .<Class<? extends Annotation>, Class<? extends Annotation>>mapToPair(c -> a.annotationType(), t -> t)
                        .collect(Collectors.toList())));

        return result;
    }

    @Override
    public boolean hasManagedAnnotation(AnnotatedElement element) {
        return Streams.to(element.getAnnotations())
                .map(Annotation::annotationType)
                .anyMatch(this.annotationProcessors::containsKey);
    }

    @Override
    public boolean isManagedAnnotation(Annotation annotation) {
        return Streams.to(this.annotationProcessors.keySet())
                .anyMatch(m -> annotation.annotationType().equals(m));
    }

    @Override
    public <A extends Annotation> boolean hasConflictingAnnotations(Collection<A> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .anyMatch(p -> p.getSecond().getIncompatibleWith(p.getFirst())
                        .stream()
                        .anyMatch(c -> annotations.stream().map(Annotation::annotationType).anyMatch(c::equals)));
    }

    @Override
    public Object[] prepareExecutable(Executable executable, Map<String, Object> runtimeContext) {
        return Streams.to(annotatedElementToExecutionList(executable.getParameters()))
                .map(l -> Streams.to(l)
                        .filter(p -> IPreparedParameterSupplier.class.isAssignableFrom(p.getClass()))
                        .cast(IPreparedParameterSupplier.class)
                        .collapse((p, o) -> p.supplyParameter(p.getAnnotation(), o, (Parameter) p.getElement(), new HashMap<>(runtimeContext))))
                .toArray();
    }

    @Override
    public int getTotalProcessors() {
        return this.annotationProcessors.values().stream()
                .mapToInt(Collection::size)
                .sum();
    }

    protected List<List<IPreparedAnnotationProcessor>> annotatedElementToExecutionList(AnnotatedElement... elements) {
        return Streams.to(elements)
                .cast(AnnotatedElement.class)
                .mapToPair(p -> p, p -> Streams.to(p.getAnnotations())
                        .filter(this::isManagedAnnotation)
                        .collect(Collectors.toList()))
                .map(this::toExecutionList)
                .collect(Collectors.toList());
    }

    protected <A extends Annotation> PairStream<A, IAnnotationProcessor<A>> convertCollectionToProcessorStream(Collection<A> annotations) {
        return Streams.to(annotations)
                .flatMapToPair(a -> Streams.to(get(a.annotationType()))
                        .mapToPair(c -> a, p -> (IAnnotationProcessor<A>) p));
    }
}
