package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;
import tv.isshoni.araragi.data.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class PreparedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements IPreparedAnnotationProcessor<IAnnotationProcessor<Annotation>> {

    public PreparedAnnotationProcessor(Annotation annotation, AnnotatedElement element, IAnnotationProcessor<Annotation> processor) {
        super(annotation, element, processor);
    }

    @Override
    public IAnnotationProcessor<Annotation> getProcessor() {
        return this.getProcessorAs();
    }
}
