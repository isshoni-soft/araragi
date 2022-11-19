package tv.isshoni.araragi.annotation.processor.prepared;

import tv.isshoni.araragi.annotation.manager.IAnnotationManager;
import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class PreparedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements IPreparedAnnotationProcessor<IAnnotationProcessor<Annotation>> {

    public PreparedAnnotationProcessor(Annotation annotation, AnnotatedElement element, IAnnotationProcessor<Annotation> processor, IAnnotationManager annotationManager) {
        super(annotation, element, processor, annotationManager);
    }

    @Override
    public IAnnotationProcessor<Annotation> getProcessor() {
        return this.getProcessorAs();
    }
}
