package tv.isshoni.araragi.annotation.processor.prepared;

import tv.isshoni.araragi.annotation.manager.IAnnotationManager;
import tv.isshoni.araragi.annotation.processor.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.processor.IParameterSupplier;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class PreparedParameterSupplier extends SimplePreparedAnnotationProcessor implements IPreparedParameterSupplier<IParameterSupplier<Annotation, Object>> {

    public PreparedParameterSupplier(Annotation annotation,AnnotatedElement element, IAnnotationProcessor<Annotation> processor, IAnnotationManager annotationManager) {
        super(annotation, element, processor, annotationManager);
    }

    @Override
    public IParameterSupplier<Annotation, Object> getProcessor() {
        return (IParameterSupplier<Annotation, Object>) this.getProcessorAs(IParameterSupplier.class);
    }
}
