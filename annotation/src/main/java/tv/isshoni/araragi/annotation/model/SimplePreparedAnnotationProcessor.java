package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;

public abstract class SimplePreparedAnnotationProcessor {

    protected final Annotation annotation;

    protected final IAnnotationProcessor<Annotation> processor;

    public SimplePreparedAnnotationProcessor(Annotation annotation, IAnnotationProcessor<Annotation> processor) {
        this.annotation = annotation;
        this.processor = processor;
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
