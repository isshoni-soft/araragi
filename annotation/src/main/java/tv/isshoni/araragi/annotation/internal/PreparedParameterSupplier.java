package tv.isshoni.araragi.annotation.internal;

import tv.isshoni.araragi.annotation.model.IAnnotationProcessor;
import tv.isshoni.araragi.annotation.model.IParameterSupplier;
import tv.isshoni.araragi.annotation.model.IPreparedParameterSupplier;
import tv.isshoni.araragi.annotation.model.SimplePreparedAnnotationProcessor;

import java.lang.annotation.Annotation;

public class PreparedParameterSupplier extends SimplePreparedAnnotationProcessor implements IPreparedParameterSupplier {

    public PreparedParameterSupplier(Annotation annotation, IAnnotationProcessor<Annotation> processor) {
        super(annotation, processor);
    }

    @Override
    public IParameterSupplier<Annotation, Object> getProcessor() {
        return (IParameterSupplier<Annotation, Object>) this.getProcessorAs(IParameterSupplier.class);
    }
}
