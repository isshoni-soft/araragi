package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IParameterSupplier;
import tv.isshoni.araragi.annotation.model.IPreparedParameterSupplier;
import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;
import tv.isshoni.araragi.data.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

public class PreparedParameterSupplier extends SimplePreparedAnnotationProcessor implements IPreparedParameterSupplier {

    public PreparedParameterSupplier(Annotation annotation,AnnotatedElement element, IAnnotationProcessor<Annotation> processor) {
        super(annotation, element, processor);
    }

    @Override
    public IParameterSupplier<Annotation, Object> getProcessor() {
        return (IParameterSupplier<Annotation, Object>) this.getProcessorAs(IParameterSupplier.class);
    }
}
