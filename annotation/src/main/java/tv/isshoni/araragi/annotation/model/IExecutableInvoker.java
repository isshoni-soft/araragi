package tv.isshoni.araragi.annotation.model;

import java.lang.reflect.Executable;

@FunctionalInterface
public interface IExecutableInvoker<T extends Executable> {

    Object invoke(T executable, Object target) throws Exception;
}
