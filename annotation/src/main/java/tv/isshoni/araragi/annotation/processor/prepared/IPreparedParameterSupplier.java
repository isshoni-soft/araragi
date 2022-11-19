package tv.isshoni.araragi.annotation.processor.prepared;

import tv.isshoni.araragi.annotation.processor.IParameterSupplier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Map;

public interface IPreparedParameterSupplier<T extends IParameterSupplier<Annotation, Object>> extends IPreparedAnnotationProcessor<T> {

    default Object supplyParameter(Annotation annotation, Object previous, Parameter parameter, Map<String, Object> runtimeContext) {
        return this.getProcessor().supply(annotation, previous, parameter, runtimeContext);
    }
}
