package tv.isshoni.araragi.annotation.model;

import tv.isshoni.araragi.data.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public abstract class SimplePreparedAnnotationProcessor {

    protected final Annotation annotation;

    protected final AnnotatedElement element;

    protected final IAnnotationProcessor<Annotation> processor;

    public SimplePreparedAnnotationProcessor(Annotation annotation,AnnotatedElement element, IAnnotationProcessor<Annotation> processor) {
        this.annotation = annotation;
        this.element = element;
        this.processor = processor;
    }

    public AnnotatedElement getElement() {
        return this.element;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public IAnnotationProcessor<Annotation> getProcessorAs() {
        return this.processor;
    }

    public <T extends IAnnotationProcessor<Annotation>> T getProcessorAs(Class<T> clazz) {
        return clazz.cast(this.processor);
    }
}
