package institute.isshoni.araragi.annotation.test.model.annotation;

import institute.isshoni.araragi.annotation.processor.IParameterSupplier;

import java.lang.reflect.Parameter;

public class SupplyNullProcessor implements IParameterSupplier<SupplyNull, String> {

    @Override
    public String supply(SupplyNull annotation, String previous, Parameter parameter) {
        return null;
    }
}
