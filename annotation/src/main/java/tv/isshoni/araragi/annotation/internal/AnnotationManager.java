package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.DefaultConstructor;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.model.*;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.machine.reflection.InheritanceCrawler;
import tv.isshoni.araragi.stream.PairStream;
import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationManager implements IAnnotationManager {

    protected final Map<Class<? extends Annotation>, List<IAnnotationProcessor<?>>> annotationProcessors;

    protected final Map<Class<? extends IAnnotationProcessor>, BiFunction<Annotation, IAnnotationProcessor<Annotation>, IPreparedAnnotationProcessor>> preparations;

    protected final Map<Class<? extends Executable>, IExecutableInvoker> executableInvokers;

    public AnnotationManager() {
        this.annotationProcessors = new ConcurrentHashMap<>();
        this.preparations = new ConcurrentHashMap<>();
        this.executableInvokers = new ConcurrentHashMap<>();

        register(IAnnotationProcessor.class, PreparedAnnotationProcessor::new);
        register(Method.class, (m, o) -> m.invoke(o, this.prepareExecutable(m)));
        register(Constructor.class, (c, o) -> c.newInstance(this.prepareExecutable(c)));
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
    public void discover(Class<? extends Annotation> annotation) {
        if (!annotation.isAnnotationPresent(Processor.class)) {
            throw new RuntimeException(annotation.getName() + " does not have a processor annotation");
        }

        register(annotation, annotation.getAnnotation(Processor.class).value());
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
                .map(c -> {
                    try {
                        return this.discoverConstructor(c);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(c -> {
                    try {
                        return this.execute(c, null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(IAnnotationProcessor<?>[]::new));
    }

    @Override
    public void register(Class<? extends IAnnotationProcessor> processor, BiFunction<Annotation, IAnnotationProcessor<Annotation>, IPreparedAnnotationProcessor> converter) {
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
    public <T extends Executable> Object execute(T executable, Object target) throws Exception {
        return this.executableInvokers.get(executable.getClass()).invoke(executable, target);
    }

    @Override
    public Constructor<?> discoverConstructor(Class<?> clazz) throws NoSuchMethodException {
        Constructor<?> defaultConstructor = clazz.getConstructor();

        return Streams.to(clazz.getDeclaredConstructors())
                .filter(c -> Streams.to(c.getParameterAnnotations())
                        .flatMap(a -> Streams.to(a)
                                .map(Annotation::annotationType))
                        .anyMatch(getAnnotationsWithProcessorType(IParameterSupplier.class)::contains))
                .find(c -> c.isAnnotationPresent(DefaultConstructor.class), Stream::findFirst)
                .orElse(defaultConstructor);
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
    public IPreparedAnnotationProcessor prepare(Annotation annotation, IAnnotationProcessor<Annotation> processor) {
        Optional<Class<? super IAnnotationProcessor<Annotation>>> processorTypeOptional = getProcessorType((Class<? super IAnnotationProcessor<Annotation>>) processor.getClass());

        if (processorTypeOptional.isEmpty()) {
            throw new IllegalStateException("Unable to find annotation preparer for processor: " + processor.getClass().getName());
        }

        Class<? super IAnnotationProcessor<Annotation>> processorType = processorTypeOptional.get();

        return this.preparations.get(processorType).apply(annotation, processor);
    }

    @Override
    public List<IPreparedAnnotationProcessor> toExecutionList(Collection<Annotation> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .map(this::prepare)
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
        return Arrays.stream(element.getAnnotations())
                .map(Annotation::annotationType)
                .anyMatch(this.annotationProcessors::containsKey);
    }

    @Override
    public boolean isManagedAnnotation(Annotation annotation) {
        return false;
    }

    @Override
    public <A extends Annotation> boolean hasConflictingAnnotations(Collection<A> annotations) {
        return convertCollectionToProcessorStream(annotations)
                .anyMatch(p -> p.getSecond().getIncompatibleWith(p.getFirst())
                        .stream()
                        .anyMatch(c -> annotations.stream().map(Annotation::annotationType).anyMatch(c::equals)));
    }

    @Override
    public Object[] prepareExecutable(Executable executable) {
        return Streams.to(executable.getParameterAnnotations())
                .map(a -> Streams.to(a)
                        .filter(this::isManagedAnnotation)
                        .collect(Collectors.toList()))
                .map(this::toExecutionList)
                .map(l -> Streams.to(l)
                        .filter(p -> IPreparedParameterSupplier.class.isAssignableFrom(p.getClass()))
                        .cast(IPreparedParameterSupplier.class)
                        .collapse((p, o) -> p.supplyParameter(p.getAnnotation(), o)))
                .toArray();
    }

    @Override
    public int getTotalProcessors() {
        return this.annotationProcessors.values().stream()
                .mapToInt(Collection::size)
                .sum();
    }

    private <A extends Annotation> PairStream<A, IAnnotationProcessor<A>> convertCollectionToProcessorStream(Collection<A> annotations) {
        return Streams.to(annotations)
                .flatMapToPair(a -> Streams.to(get(a.annotationType()))
                        .mapToPair(c -> a, p -> (IAnnotationProcessor<A>) p));
    }

    private Optional<Class<? super IAnnotationProcessor<Annotation>>> getProcessorType(Class<? super IAnnotationProcessor<Annotation>> processor) {
        InheritanceCrawler<IAnnotationProcessor<Annotation>> crawler = new InheritanceCrawler<>(processor);

        Class<? super IAnnotationProcessor<Annotation>> result = null;

        while (result == null && crawler.hasNext()) {
            Class<? super IAnnotationProcessor<Annotation>> current = crawler.next();

            if (this.preparations.containsKey(current)) {
                result = current;
            }
        }

        return Optional.ofNullable(result);
    }
}
