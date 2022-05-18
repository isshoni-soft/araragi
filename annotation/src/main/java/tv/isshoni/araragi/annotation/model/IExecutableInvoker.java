package tv.isshoni.araragi.annotation.model;

import java.lang.reflect.Executable;
import java.util.Map;

@FunctionalInterface
public interface IExecutableInvoker<T extends Executable> {

    Object invoke(T executable, Object target, Map<String, Object> runtimeContext) throws Exception;
}
