package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Map;

public interface IPreparedParameterSupplier extends IPreparedAnnotationProcessor<IParameterSupplier<Annotation, Object>> {

    default Object supplyParameter(Annotation annotation, Object previous, Parameter parameter, Map<String, Object> runtimeContext) {
        return this.getProcessor().supply(annotation, previous, parameter, runtimeContext);
    }

//    O supply(A annotation, O previous);
}
