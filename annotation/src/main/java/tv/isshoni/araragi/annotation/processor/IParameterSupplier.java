package tv.isshoni.araragi.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Map;

public interface IParameterSupplier<A extends Annotation, O> extends IAnnotationProcessor<A> {

    default O supply(A annotation, O previous, Parameter parameter) {
        return previous;
    }

    default O supply(A annotation, O previous, Parameter parameter, Map<String, Object> runtimeContext) {
        return supply(annotation, previous, parameter);
    }
}
