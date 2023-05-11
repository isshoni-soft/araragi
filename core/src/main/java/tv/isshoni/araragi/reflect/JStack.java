package tv.isshoni.araragi.reflect;

import tv.isshoni.araragi.stream.Streams;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JStack {

    public static Method getMethodInStack(int index) throws NoSuchMethodException {
        Optional<StackWalker.StackFrame> optionalFrame = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(f -> Streams.to(f).limit(index).findLast());

        if (optionalFrame.isEmpty()) {
            return null;
        }

        StackWalker.StackFrame frame = optionalFrame.get();
        MethodType methodType = frame.getMethodType();

        return frame.getDeclaringClass().getMethod(frame.getMethodName(), methodType.parameterList()
                .toArray(new Class<?>[0]));
    }

    public static Method getEnclosingMethod() throws NoSuchMethodException {
        return getMethodInStack(3);
    }

    public static Method getParentMethod() throws NoSuchMethodException {
        return getMethodInStack(4);
    }

    public static void forEach(Consumer<StackWalker.StackFrame> consumer) {
        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(s -> s.collect(Collectors.toList())).forEach(consumer);
    }
}
