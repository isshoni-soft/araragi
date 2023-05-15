package tv.isshoni.araragi.concurrent.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface IAsyncManager {

    <T> Future<T> submit(Callable<T> callable);

    Future<?> submit(Runnable runnable);

    void addShutdownHook(Runnable runnable);

    void hook();

    boolean isMainThread();
}
