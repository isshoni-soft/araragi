package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface IParameterSupplier<A extends Annotation, O> extends IAnnotationProcessor<A> {

    default O supply(A annotation, O previous) {
        return previous;
    }

    default O supply(A annotation, O previous, Map<String, Object> runtimeContext) {
        return supply(annotation, previous);
    }
}
