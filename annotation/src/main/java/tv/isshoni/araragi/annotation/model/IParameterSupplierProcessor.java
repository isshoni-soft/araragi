package tv.isshoni.araragi.annotation.model;

import java.lang.annotation.Annotation;

public interface IParameterSupplierProcessor<A extends Annotation, O> extends IAnnotationProcessor<A> {

    O supply(A annotation);
}
