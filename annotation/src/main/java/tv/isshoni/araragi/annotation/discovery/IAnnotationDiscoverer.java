package tv.isshoni.araragi.annotation.discovery;

import org.reflections8.Reflections;
import tv.isshoni.araragi.annotation.AttachTo;
import tv.isshoni.araragi.annotation.Depends;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.annotation.manager.IAnnotationManager;
import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public interface IAnnotationDiscoverer {

    IAnnotationManager getAnnotationManager();

    IAnnotationDiscoverer withPackages(String... packages);

    IAnnotationDiscoverer withPackages(Collection<String> packages);

    List<String> getPackages();

    Reflections construct();

    default <A extends Annotation> Set<Class<?>> findWithAnnotations(Class<A> clazz) {
        return construct().getTypesAnnotatedWith(clazz).stream()
                .filter(c -> c.isAnnotationPresent(clazz))
                .collect(Collectors.toSet());
    }

    default Set<Class<? extends Annotation>> findProcessorAnnotations() {
        return findWithAnnotations(Processor.class).stream()
                .map(c -> (Class<? extends Annotation>) c)
                .collect(Collectors.toSet());
    }

    default Set<Class<? extends IAnnotationProcessor<Annotation>>> findAttachedProcessors() {
        return findWithAnnotations(AttachTo.class).stream()
                .filter(IAnnotationProcessor.class::isAssignableFrom)
                .map(c -> (Class<? extends IAnnotationProcessor<Annotation>>) c)
                .collect(Collectors.toSet());
    }

    default IAnnotationDiscoverer discoverAnnotations() {
        Set<Class<? extends Annotation>> all = findProcessorAnnotations();

        all.forEach(ac -> safelyRecursiveDiscover(ac, all, new Stack<>()));

        return this;
    }

    default IAnnotationDiscoverer discoverAttachedProcessors() {
        findAttachedProcessors().forEach(getAnnotationManager()::discoverProcessor);

        return this;
    }

    default void safelyRecursiveDiscover(Class<? extends Annotation> clazz, Set<Class<? extends Annotation>> all, Stack<Class<? extends Annotation>> levels) {
        if (getAnnotationManager().isManagedAnnotation(clazz)) {
            return;
        }

        Set<Class<? extends Annotation>> annotations = Streams.to(clazz.getAnnotation(Processor.class).value())
                .map(getAnnotationManager()::getAllAnnotationsIn)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        if (clazz.isAnnotationPresent(Depends.class)) {
            annotations.addAll(getAnnotationManager().getAllAnnotationsIn(clazz));
        }

        if (annotations.isEmpty() || annotations.stream().allMatch(getAnnotationManager()::isManagedAnnotation)) {
            getAnnotationManager().discoverAnnotation(clazz);
            return;
        }

        List<Class<? extends Annotation>> unregistered = Streams.to(annotations)
                .filterInverted(getAnnotationManager()::isManagedAnnotation)
                .toList();

        for (Class<? extends Annotation> uclazz : unregistered) {
            if (clazz.equals(uclazz) || levels.contains(uclazz)) {
                continue;
            }

            if (all.contains(uclazz)) {
                levels.add(clazz);
                safelyRecursiveDiscover(uclazz, all, levels);
                levels.pop();
            }
        }

        getAnnotationManager().discoverAnnotation(clazz);
    }
}
