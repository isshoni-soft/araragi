package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface IPreparedParameterSupplier extends IPreparedAnnotationProcessor<IParameterSupplier<Annotation, Object>> {

    default Object supplyParameter(Annotation annotation, Object previous, Map<String, Object> runtimeContext) {
        return this.getProcessor().supply(annotation, previous, runtimeContext);
    }

//    O supply(A annotation, O previous);
}
