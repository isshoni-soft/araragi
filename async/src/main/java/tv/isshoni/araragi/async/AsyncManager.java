package tv.isshoni.araragi.async;

import tv.isshoni.araragi.machine.AraragiRuntime;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AsyncManager implements IAsyncManager {

    protected final List<Runnable> HOOKS = new LinkedList<>();

    protected ExecutorService service;

    public AsyncManager(ExecutorService service) {
        this.service = service;
    }

    public AsyncManager() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return this.service.submit(callable);
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        return this.service.submit(runnable);
    }

    @Override
    public void addShutdownHook(Runnable runnable) {
        this.HOOKS.add(runnable);
    }

    @Override
    public void hook() {
        AraragiRuntime.registerShutdownHook(() -> {
            this.HOOKS.forEach(Runnable::run);

            this.service.shutdown();

            try {
                this.service.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
