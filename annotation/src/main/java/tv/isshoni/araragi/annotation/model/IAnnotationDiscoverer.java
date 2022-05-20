package tv.isshoni.araragi.annotation.model;

import tv.isshoni.araragi.annotation.AttachTo;
import tv.isshoni.araragi.annotation.DefaultConstructor;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.reflect.ReflectionUtil;
import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections8.Reflections;

public interface IAnnotationDiscoverer {

    Comparator<Class<? extends Annotation>> PARAMETER_DEPENDENCY_COMPARATOR = (f, s) -> {
        Processor firstProcessor = f.getAnnotation(Processor.class);
        Processor secondProcessor = s.getAnnotation(Processor.class);

        Set<Class<? extends Annotation>> firstDeps = new HashSet<>(Streams.to(firstProcessor.value())
                .flatMap(pc -> ReflectionUtil.discoverAnnotatedConstructors(pc, DefaultConstructor.class).stream()
                        .flatMap(c -> ReflectionUtil.getAllParameterAnnotationTypes(c).stream()))
                .toList());

        Set<Class<? extends Annotation>> secondDeps = new HashSet<>(Streams.to(secondProcessor.value())
                .flatMap(pc -> ReflectionUtil.discoverAnnotatedConstructors(pc, DefaultConstructor.class).stream()
                        .flatMap(c -> ReflectionUtil.getAllParameterAnnotationTypes(c).stream()))
                .toList());

        boolean secondEmpty = secondDeps.isEmpty();

        if (firstDeps.isEmpty()) {
            if (secondEmpty) {
                return 0;
            }

            return 1;
        }

        if (secondEmpty) {
            return -1;
        }

        boolean secondContains = secondDeps.contains(f);

        if (firstDeps.contains(s)) {
            if (secondContains) {
                throw new IllegalStateException("Circular dependencies found: " + f.getName() + " <-> " + s.getName());
            }

            return -1;
        }

        if (secondContains) {
            return 1;
        }

        return 0;
    };

    IAnnotationManager getAnnotationManager();

    IAnnotationDiscoverer withPackages(String... packages);

    IAnnotationDiscoverer withPackages(Collection<String> packages);

    List<String> getPackages();

    Reflections getReflections();

    Reflections construct();

    default IAnnotationDiscoverer discoverParameterAnnotations() {
        getReflections().getTypesAnnotatedWith(Processor.class).stream()
                .map(c -> (Class<? extends Annotation>) c)
                .filter(c -> c.isAnnotationPresent(Processor.class))
                .filter(c -> Streams.to(c.getAnnotation(Processor.class).value())
                        .anyMatch(IParameterSupplier.class::isAssignableFrom))
                .sorted(PARAMETER_DEPENDENCY_COMPARATOR.reversed())
                .forEachOrdered(getAnnotationManager()::discoverAnnotation);

        return this;
    }

    default IAnnotationDiscoverer discoverAnnotations() {
        getReflections().getTypesAnnotatedWith(Processor.class).stream()
                .map(c -> (Class<? extends Annotation>) c)
                .filter(c -> c.isAnnotationPresent(Processor.class))
                .filter(c -> Streams.to(c.getAnnotation(Processor.class).value())
                        .noneMatch(IParameterSupplier.class::isAssignableFrom))
                .forEachOrdered(getAnnotationManager()::discoverAnnotation);

        return this;
    }

    default IAnnotationDiscoverer discoverAttachedProcessors() {
        getReflections().getTypesAnnotatedWith(AttachTo.class).stream()
                .filter(c -> c.isAnnotationPresent(AttachTo.class))
                .filter(IAnnotationProcessor.class::isAssignableFrom)
                .map(c -> (Class<IAnnotationProcessor<Annotation>>) c)
                .forEach(getAnnotationManager()::discoverProcessor);

        return this;
    }
}
