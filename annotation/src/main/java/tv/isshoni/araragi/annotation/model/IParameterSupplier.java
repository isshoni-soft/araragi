package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface IParameterSupplier<A extends Annotation, O> extends IAnnotationProcessor<A> {

    O supply(A annotation, O previous);
}
