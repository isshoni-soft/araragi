package institute.isshoni.araragi.annotation.test.model.annotation;

import institute.isshoni.araragi.annotation.processor.IParameterSupplier;
import institute.isshoni.araragi.annotation.test.TestAnnotationManager;

import java.lang.reflect.Parameter;
import java.util.Map;

public class SecondAnnotationProcessor implements IParameterSupplier<Second, String> {

    public SecondAnnotationProcessor(@TestAnnotation(TestAnnotationManager.EXPECTED_VALUE) String str) {
        System.err.println("--- Construct SecondAnnotationProcessor --- (" + str + ")");
        System.out.print(str);
    }

    @Override
    public String supply(Second annotation, String previous, Parameter parameter, Map<String, Object> runtimeContext) {
        return runtimeContext.getOrDefault(annotation.value(), "DEFAULT").toString();
    }
}
