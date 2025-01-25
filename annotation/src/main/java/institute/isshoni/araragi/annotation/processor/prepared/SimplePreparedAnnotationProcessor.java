package institute.isshoni.araragi.annotation.processor.prepared;

import institute.isshoni.araragi.annotation.manager.IAnnotationManager;
import institute.isshoni.araragi.annotation.processor.IAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public abstract class SimplePreparedAnnotationProcessor {

    protected final Annotation annotation;

    protected final AnnotatedElement element;

    protected final IAnnotationProcessor<Annotation> processor;

    protected final IAnnotationManager annotationManager;

    public SimplePreparedAnnotationProcessor(Annotation annotation, AnnotatedElement element, IAnnotationProcessor<Annotation> processor, IAnnotationManager annotationManager) {
        this.annotation = annotation;
        this.element = element;
        this.processor = processor;
        this.annotationManager = annotationManager;
    }

    public AnnotatedElement getElement() {
        return this.element;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public IAnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }

    public IAnnotationProcessor<Annotation> getProcessorAs() {
        return this.processor;
    }

    public <T extends IAnnotationProcessor<Annotation>> T getProcessorAs(Class<T> clazz) {
        return clazz.cast(this.processor);
    }
}
