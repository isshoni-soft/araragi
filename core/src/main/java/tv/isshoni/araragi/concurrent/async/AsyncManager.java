package tv.isshoni.araragi.concurrent.async;

import tv.isshoni.araragi.runtime.AraragiRuntime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AsyncManager implements IAsyncManager {

    protected final List<Runnable> hooks;

    protected final Map<String, Thread> managedThreads;

    protected ExecutorService service;

    public AsyncManager(ExecutorService service) {
        this.service = service;
        this.hooks = new LinkedList<>();
        this.managedThreads = new HashMap<>();
    }

    public AsyncManager() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    public Thread newManagedThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable, name);
        this.managedThreads.put(name, thread);

        return thread;
    }

    @Override
    public Thread getManagedThread(String name) {
        return this.managedThreads.get(name);
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
        this.hooks.add(runnable);
    }

    @Override
    public void hook() {
        AraragiRuntime.registerShutdownHook(() -> {
            this.hooks.forEach(Runnable::run);

            this.service.shutdown();

            try {
                this.service.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.managedThreads.values().forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    @Override
    public boolean isMainThread() {
        return Thread.currentThread().getId() == 1;
    }
}
