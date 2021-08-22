package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.model.IAnnotationManager;
import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.machine.reflection.InheritanceCrawler;
import tv.isshoni.araragi.stream.PairStream;
import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class AnnotationManager implements IAnnotationManager {

    protected final Map<Class<? extends Annotation>, List<IAnnotationProcessor<?>>> annotationProcessors;

    protected final Map<Class<? extends IAnnotationProcessor>, BiFunction<Annotation, IAnnotationProcessor<Annotation>, IPreparedAnnotationProcessor>> preparations;

    public AnnotationManager() {
        this.annotationProcessors = new ConcurrentHashMap<>();
        this.preparations = new ConcurrentHashMap<>();

        register(IAnnotationProcessor.class, PreparedAnnotationProcessor::new);
    }

    @Override
    public <T extends Annotation> void unregister(Class<T> annotation) {
        this.annotationProcessors.remove(annotation);
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
        register(annotation, Arrays.stream(processors)
                .map(this::construct)
                .toArray(IAnnotationProcessor<?>[]::new));
    }

    @Override
    public void register(Class<? extends IAnnotationProcessor> processor, BiFunction<Annotation, IAnnotationProcessor<Annotation>, IPreparedAnnotationProcessor> converter) {
        this.preparations.put(processor, converter);
    }

    @Override
    public <T extends Annotation> void register(Class<T> annotation, IAnnotationProcessor<?>... processors) {
        this.annotationProcessors.compute(annotation, (a, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }

            v.addAll(Arrays.asList(processors));

            return v;
        });
    }

    @Override
    public IAnnotationProcessor<?> construct(Class<? extends IAnnotationProcessor<?>> processor) {
        try {
            return processor.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e); // TODO: Add a specialized exception
        }
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
        Arrays.stream(executable.getParameterAnnotations())
                .map(a -> Arrays.stream(a).filter())

        return null;
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
