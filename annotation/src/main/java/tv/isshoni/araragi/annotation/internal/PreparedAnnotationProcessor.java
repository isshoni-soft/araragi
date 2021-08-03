package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;

import java.lang.annotation.Annotation;

public class PreparedAnnotationProcessor implements IPreparedAnnotationProcessor {

    protected final Annotation annotation;

    protected final IAnnotationProcessor<Annotation> processor;

    public PreparedAnnotationProcessor(Annotation annotation, IAnnotationProcessor<Annotation> processor) {
        this.annotation = annotation;
        this.processor = processor;
    }

    @Override
    public Annotation getAnnotation() {
        return this.annotation;
    }

    @Override
    public IAnnotationProcessor<Annotation> getProcessor() {
        return this.processor;
    }
}
