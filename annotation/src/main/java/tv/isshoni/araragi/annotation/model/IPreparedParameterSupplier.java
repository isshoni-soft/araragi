package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;

public interface IPreparedParameterSupplier extends IPreparedAnnotationProcessor<IParameterSupplier<Annotation, Object>> {

    default Object supplyParameter(Annotation annotation, Object previous) {
        return this.getProcessor().supply(annotation, previous);
    }

//    O supply(A annotation, O previous);
}
