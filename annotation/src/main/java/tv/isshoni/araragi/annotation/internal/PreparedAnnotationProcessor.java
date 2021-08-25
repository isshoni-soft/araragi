package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;

import java.lang.annotation.Annotation;

public class PreparedAnnotationProcessor extends SimplePreparedAnnotationProcessor implements IPreparedAnnotationProcessor<IAnnotationProcessor<Annotation>> {

    public PreparedAnnotationProcessor(Annotation annotation, IAnnotationProcessor<Annotation> processor) {
        super(annotation, processor);
    }

    @Override
    public IAnnotationProcessor<Annotation> getProcessor() {
        return this.getProcessorAs();
    }
}
