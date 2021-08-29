package tv.isshoni.araragi.annotation.model;

import tv.isshoni.araragi.annotation.AttachTo;
import tv.isshoni.araragi.annotation.Processor;
import tv.isshoni.araragi.stream.Streams;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import org.reflections8.Reflections;

public interface IAnnotationDiscoverer {

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
                .forEach(getAnnotationManager()::discoverAnnotation);

        return this;
    }

    default IAnnotationDiscoverer discoverAnnotations() {
        getReflections().getTypesAnnotatedWith(Processor.class).stream()
                .map(c -> (Class<? extends Annotation>) c)
                .filter(c -> c.isAnnotationPresent(Processor.class))
                .filter(c -> Streams.to(c.getAnnotation(Processor.class).value())
                        .noneMatch(IParameterSupplier.class::isAssignableFrom))
                .forEach(getAnnotationManager()::discoverAnnotation);

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
